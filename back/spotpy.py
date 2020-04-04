import spotipy
from spotipy.oauth2 import SpotifyClientCredentials

client_id = "742f91b7fae24f33980b28029b79bd45"
client_secret = "2823cbbf6bd94e619bb855d893b2865c"

client_credentials_manager = SpotifyClientCredentials(client_id, client_secret)
sp = spotipy.Spotify(client_credentials_manager=client_credentials_manager)


def get_genres(artists):
    genre_list = []
    for artist in artists:
        genres = sp.artist(artist['id'])['genres']
        for genre in genres:
            if genre not in genre_list:
                genre_list.append(genre)
    return genre_list
    