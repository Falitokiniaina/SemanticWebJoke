import random
import string

from nltk.corpus import wordnet as wn 
import cherrypy
import rdflib

#test it!
#http://127.0.0.1:8080/generate?topic=insults

class JokeGenerator(object):
    
    def __init__(self, rdf):
        if not rdf:
            return "Please specify a rdf file."
        self.g = rdflib.Graph() 
        try:
            self.g.parse(rdf)
            self.genres = self.getGenres()
        except:
            print("{0:s} is not a valid rdf file.".format(rdf))

    @cherrypy.expose
    def index(self):
        return "This app gives you random jokes..."
        
    @cherrypy.expose
    def generate(self, topic):
        if not topic:
            return "Give me a topic, I'll tell you a joke!"
        similarity = {}
        for gen in self.genres:
            #try an exact match
            if topic.strip().lower() == gen.strip().lower():
                similarity[gen] = 1
            #assign similarity based on wordnet
            else:
                similarity[gen] = self.wordNetSimilarity(topic, gen)
        best_match = argmax(similarity)
        #log
        print("BEST MATCH: {0} {1} {2}".format(topic, best_match, similarity[best_match]))
        #output
        jokes = self.getJoke(best_match)
        joke = jokes[random.randint(0, len(jokes)-1)]
        return "Here is a joke about <b>{0}</b>:<br/>{1}".format(best_match, joke)

    
    def wordNetSimilarity(self, term1, term2):
        #http://www.nltk.org/howto/wordnet.html
        try:
            wn_term1 = wn.synset(term1 + ".n.01")
            wn_term2 = wn.synset(term2 + ".n.01")
            sim = wn.path_similarity(wn_term1, wn_term2)
        except:
            sim = 0
        return sim
        
    def getGenres(self):
        q = "SELECT ?name WHERE {{ ?genre a <http://www.semanticweb.org/marc/ontologies/2014/10/untitled-ontology-8#Genre>. ?genre untitled-ontology-82:name ?name }}"
        r = self.g.query(q)
        cat = []
        for i in r:
            cat.append(i[0])
        return cat

    def getJoke(self, genre):
        q = "SELECT ?content WHERE {{ ?joke a <http://www.semanticweb.org/marc/ontologies/2014/10/untitled-ontology-8#TextJoke>. ?joke untitled-ontology-82:content ?content. ?joke untitled-ontology-82:hasGenre <http://www.semanticweb.org/marc/ontologies/2014/10/untitled-ontology-8#{0:s}> }}".format(genre)
        r = self.g.query(q)
        jokes = []
        for i in r:
            jokes.append(i[0])
        return jokes
    
    
def argmax(d):
    """ Get key of the maximum value of a dictionary """
    v=list(d.values())
    k=list(d.keys())
    return k[v.index(max(v))]    
        
        
if __name__ == '__main__':
    rdf_file = "jokeOntology_rdf_with_instances.owl"
    cherrypy.quickstart(JokeGenerator(rdf=rdf_file))
