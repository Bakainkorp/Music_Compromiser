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
    try:
        input = request.get_json()
        host = input['user']
        data = input['data'] #this should be the top tracks json that you get from the api call
        server = Server(host, data)
        output = {'serverid': server.serverid}
        return json.dumps(output)
    except:
        return 'error'

@app.route('/join', methods=['POST'])
def join():
    try:
        input = request.get_json()
        client = input['user'] #joining user
        serverid = input['serverid'] #serverid
        data = input['data'] #this should be the top tracks json that you get from the api call
        success = {'success': 'False'}
        for server in servers:
            if server.serverid == serverid:
                server.user_join(client, data)
                success['success'] = 'True'
                return json.dumps(success)
        return json.dumps(success)
    except:
        return 'error'

@app.route('/start', methods=['POST'])
def start():
    try:
        req_data = request.args
        serverid = req_data.get('serverid')
        for server in servers:
            if server.serverid == serverid:
                server.make_comprimise()
                return(json.dumps(server.complist))
    except:
        return 'error'

@app.route('/kill')
def kill():
    try:
        servers.clear()
    except:
        return 'error'

if __name__ == '__main__':
    # run!
    app.run()