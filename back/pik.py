import pickle
import classes

def save():
    pickle.dump(classes.servers, open("save.p", "wb"))

def load():
    try:
        classes.servers = pickle.load(open( "save.p", "rb" ))
    except Exception as e:
        print(e)