import random
import string

import nltk
import cherrypy

class JokeGenerator(object):
    
    @cherrypy.expose
    def index(self):
        return "Hello world!"

    @cherrypy.expose
    def generate(self, topic=None):
        
        
        return "About {0}".format(topic)
        
    
    def getCategories(self):
        return    
    
    def wordNetDistance(self):
        return
        
if __name__ == '__main__':
    cherrypy.quickstart(JokeGenerator())
