from flask import Flask, request, jsonify
import json
import classes
from classes import Server, servers

app = Flask(__name__)
app.config["DEBUG"] = False

@app.route('/', methods=['GET'])
def landing():
    return """<h1>Da Landing</h1>"""

@app.route('/host', methods=['POST'])
def host():
    input = request.get_json()
    host = input['user']
    data = input['data'] #this should be the top tracks json that you get from the api call
    server = Server(host, data)
    return str(server.serverid)

@app.route('/join', methods=['POST'])
def join():
    input = request.get_json()
    client = input['user'] #joining user
    serverid = input['serverid'] #serverid
    data = input['data'] #this should be the top tracks json that you get from the api call
    for server in servers:
        if server.serverid == serverid:
            server.user_join(client, data)
            return 'Joined Server'
    return 'Invalid Server ID'

@app.route('/start', methods=['POST'])
def start():
    req_data = request.args
    serverid = req_data.get('serverid')
    for server in servers:
        if server.serverid == serverid:
            server.make_comprimise()
            return(json.dumps(server.complist))

if __name__ == '__main__':
    # run!
    app.run()