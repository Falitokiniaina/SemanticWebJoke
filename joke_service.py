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
        except:
            print("{0:s} is not a valid rdf file.".format(rdf))
            quit()
        self.genres = self.getGenres()
        print(self.genres)

    @cherrypy.expose
    def index(self):
        return "This app gives you random jokes..."
        
    @cherrypy.expose
    def generate(self, topic):
        if not topic:
            return "Give me a topic, I'll tell you a joke!"
        similarity = {}
        for genre in self.genres:
            #try an exact match
            if topic.strip().lower() == genre.strip().lower():
                similarity[genre] = 1
            #assign similarity based on wordnet
            else:
                max_sim = 0
                for term in genre.split(' '):
                    term_sim = self.wordNetSimilarity(topic, term)
                    if term_sim > max_sim:
                        max_sim = term_sim
                similarity[genre] = max_sim
        #get best genre for the user
        best_match = argmax(similarity)
        #log
        print("BEST MATCH: {0} {1} {2}".format(topic, best_match, similarity[best_match]))
        #output
        jokes = self.getJoke(best_match)
        joke = jokes[random.randint(0, len(jokes)-1)]
        #text to speech in chrome
        #http://html5-examples.craic.com/google_chrome_text_to_speech.html
        html_content = """Here is a joke about : {0}\n
        {1}
        """.format(best_match, joke)
        return html_content

    
    def wordNetSimilarity(self, term1, term2):
        #http://www.nltk.org/howto/wordnet.html
        sim = None
        try:
            wn_term1 = wn.synsets(term1)[0] #+ ".n.01")
            wn_term2 = wn.synsets(term2)[0] #+ ".n.01")
            sim = wn.path_similarity(wn_term1, wn_term2)
        except:
            print("Error computing similarity.")
        if not sim:
            sim = 0
        return sim
        
    def getGenres(self):
        q = "PREFIX jo:<http://www.semanticweb.org/joke_ontology#> " \
            "SELECT ?name WHERE {{ ?genre a <http://www.semanticweb.org/joke_ontology#Genre>. " \
            "?genre jo:name ?name }}"
        r = self.g.query(q)
        cat = []
        for i in r:
            cat.append(i[0])
        return cat

    def getJoke(self, genre):
        q = "PREFIX jo:<http://www.semanticweb.org/joke_ontology#> "\
            "SELECT ?content "\
            "WHERE {{ "\
            "?joke a <http://www.semanticweb.org/joke_ontology#TextJoke> ."\
            "?genre a <http://www.semanticweb.org/joke_ontology#Genre> ."\
            "?joke jo:content ?content ."\
            "?joke jo:hasGenre ?genre ."\
            "?genre jo:name '{0:s}' }}".format(genre)
        print(q)
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
    rdf_file = "new_owl.owl"
    cherrypy.quickstart(JokeGenerator(rdf=rdf_file))
