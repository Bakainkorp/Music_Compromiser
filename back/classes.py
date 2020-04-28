import random
import json
from spotpy import get_genres


servers = []

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
            if self.serverid == server.serverid:
                return True
        return False

    def populate_datalist(self, input_json, user):
        self.datalist.append(User(user))
        for item in input_json:
            try:
                genres = get_genres(item['artists'])
                self.datalist[-1].uri_list.append([item['uri'], genres, item['name'], self.get_artists(item)])
                self.datalist[-1].add_genre(genres)
            except KeyError:
                pass

    def get_artists(self, item):
        out = []
        for artist in item['artists']:
            if artist['name'] is not None:
                out.append(artist['name'])
        return out
    
    def user_join(self, user, input_json):
        self.users.append(user)
        self.populate_datalist(input_json, user)


    def make_comprimise(self):
        for user in self.datalist:
            for uri in user.uri_list:
                if uri[0] not in [sublist[1] for sublist in self.complist]:
                    self.complist.append([1, uri[0], uri[2], uri[3], [user.user]])
                else:
                    for item in self.complist:
                        if item[1] == uri[0]:
                            item[0] += 1
                            item[4].append(user.user)
        self.complist.sort(key = lambda x: x[0])
        
                



class User:
    def __init__(self, user):
        self.uri_list = []
        self.user = user
        self.genres = []

    def add_genre(self, genres_in):
        for genre_in in genres_in:
            for genre in self.genres:
                if genre[0] == genre_in:
                    genre[1] += 1
                    break
            self.genres.append([genre_in, 1])

            



'''
with open('testdata.json') as json_file:
    data = json.load(json_file)

a = Server("TAD", data)
a.make_comprimise()
print("reeee")


out = {'user' : 'test user', 'data': data}
other = {'user': 'thing', 'serverid': '4', 'data': data}
print("ree")
file1 = open("qqq.txt","w") 
file1.write(json.dumps(other))
'''