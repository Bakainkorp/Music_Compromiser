import random
import json

class Server:
    def __init__(self, host, host_json):
        self.users = [host]
        self.datalist = []
        self.populate_datalist(host_json, host)
        self.complist = []
        self.serverid = random.randint(1, 9999999)
        while(self.isActive()):
            self.serverid = random.randint(1, 9999999)
        servers.append(self)
    
    def isActive(self):
        for server in servers:
            if self.serverid == server.id:
                return True
        return False

    def populate_datalist(self, input_json, user):
        self.datalist.append(User(user))
        for item in input_json:
            try:
                self.datalist[-1].uri_list.append(item['uri'])
            except KeyError:
                pass
    
    def user_join(self, user, input_json):
        self.users.append(user)
        self.populate_datalist(input_json, user)


    def make_comprimise(self):
        for user in self.datalist:
            for uri in user.uri_list:
                if uri not in [sublist[1] for sublist in self.complist]:
                    self.complist.append(1, uri, [user.user])
                else:
                    for item in self.complist:
                        if item[1] == uri:
                            item[0] += 1
                            item[2].append(user.user)
        self.complist.sort(key = lambda x: x[0])
        
                



class User:
    def __init__(self, user):
        self.uri_list = []
        self.user = user


servers = []

'''
with open('myfile.json') as json_file:
    data = json.load(json_file)

Server("TAD", data)
print("reeee")

out = {'user' : 'test user', 'data': data}
print("ree")
file1 = open("myfile.txt","w") 
file1.write(json.dumps(out))
'''