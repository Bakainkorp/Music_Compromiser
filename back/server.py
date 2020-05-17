from flask import Flask, request, jsonify, redirect, url_for
from flask_pymongo import PyMongo
import json
import classes
from classes import Server
import pik

app = Flask(__name__)
app.config["MONGO_URI"] = "mongodb+srv://bakainkorp:Zxcvbnm%2C.%2F1Zxcvbnm%2C.%2F1@musiccompromiser-s4phd.mongodb.net/test"
mongo = PyMongo(app)
app.config["DEBUG"] = False
database = mongo.db.database

@app.route('/', methods=['GET'])
def landing():
    return '<h1>Da Landing</h1>'

@app.route('/host', methods=['POST'])
def host():
    try:
        pik.load()
        input = request.get_json()
        if input:
            host = input['user']
            data = input['data'] #this should be the top tracks json that you get from the api call
            server = Server(host, data)
            output = {'serverid': server.serverid}

            #Used for temporary information storage between phones and routes
            #ServerID prevents the database from making mistakes via duplicate information
            database.insert ({
                "User" : host,
                "ServerID" : server.serverid,
                "Prep" : False
            })

        pik.save()

        return json.dumps(output)
    except Exception as e:
        return str(e)


@app.route('/join', methods=['POST'])
def join():
    try:
        pik.load()
        input = request.get_json()
        client = input['user'] #joining user
        serverid = input['serverid'] #serverid
        data = input['data'] #this should be the top tracks json that you get from the api call
        success = {'success': 'False'}
        for server in classes.servers:
            if str(server.serverid) == serverid:
                server.user_join(client, data)
                success['success'] = 'True'

                #Used for temporary information storage between phones and routes
                #ServerID prevents the database from making mistakes via duplicate information
                database.insert ({
                    "User" : client,
                    "ServerID" : server.serverid,
                    "Prep" : False
                })

                pik.save()
                return json.dumps(success)

        #Used for temporary information storage between phones and routes
        #ServerID prevents the database from making mistakes via duplicate information
        database.insert ({
            "User" : client,
            "ServerID" : server.serverid,
            "Prep" : False
        })

        pik.save()
        return json.dumps(success)
    except:
        return 'error'

@app.route('/start', methods=['POST'])
def start():
    try:
        pik.load()
        req_data = request.get_json()
        serverid = req_data['serverid']
        for server in classes.servers:
            if str(server.serverid) == str(serverid):
                if len(server.complist) == 0:
                    server.make_comprimise()

                #fileString is the entire JSON file combined with all participating users
                #Prep allows /select to find only items with JSON files attached to them
                sameSID = database.find_one({'ServerID' : server.ServerID})
                Users = []
                while sameSID:
                    Users.append(sameSID['User'])
                    database.remove(sameSID)
                    sameSID = database.find_one({'ServerID' : server.ServerID})
                fileString = json.dumps(server.complist)
                database.insert ({
                    "ServerID" : server.serverid,
                    "JSON file" : fileString,
                    "Users" : Users,
                    "Prep" : True
                })

                pik.save()
                return fileString
        pik.save()
        return 'server not found'
    except Exception as e:
        return str(e)


@app.route('/show')
def show():
    pik.load()
    out = []
    for server in classes.servers:
        out.append(server.serverid)
    pik.save()
    return json.dumps(out)

#User presses "Past Playlists" and the Server returns every playlist the User participated in
#Returns userPlaylists, which is an array of all serverIDs, meaning different playlists
#@app.route('/select', methods=['POST'])
#def select(theUser):
#    try:
#        pik.load()
#        userPlaylists = []

#        pastPlaylists = database.find({'Prep' : True})
#        for x in pastPlaylists:
#            check = x['Users']
#            while len(check) != 0:
#                if check[0] == theUser:
#                    userPlaylists.append(x['ServerID'])
#                check.pop(0)

#        pik.save()

#        return json.dumps(userPlaylists)
#    except Exception as e:
#        return str(e)

#User presses the desired playlist
#Returns the JSON file in pastPlaylist, which has the same ID as the user's choice
#@app.route('/pastplay', methods=['POST'])
#def pastplay(theID):
#    try:
#        pik.load()
#        pastPlaylist = database.find_one({"$and":
#            [{'Prep' : True},
#            {'ServerID' : theID}]})
#        pik.save()

#        return json.dumps(pastPlaylist['JSON file'])
#    except Exception as e:
#        return str(e)



@app.route('/kill')
def kill():
    try:
        pik.load()
        classes.servers.clear()
        pik.save()
        return show()
    except:
        return 'error'

if __name__ == '__main__':
    # run!
    app.run()
