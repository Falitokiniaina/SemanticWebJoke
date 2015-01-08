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
            print(self.genres)
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
        for genre in self.genres:
            #try an exact match
            if topic.strip().lower() == genre.strip().lower():
                similarity[genre] = 1
            #assign similarity based on wordnet
            else:
                max_sim = 0
                for term in genre.split(' '):
                    term_sim = self.wordNetSimilarity(topic, term)
                    if term_sim > term_sim:
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
        html_content = """
        <html>
            <head>
                <link href="/css/style.css" rel="stylesheet">
                <script src="https://ajax.googleapis.com/ajax/libs/jquery/1/jquery.min.js"></script>
                <script >
                    var voices = [];
                    $(document).ready(function() {{
                        var ischrome = navigator.userAgent.match(/chrome/i);
                        if(ischrome) {{
                            var u = new SpeechSynthesisUtterance({1});
                            u.lang = 'en-US';
                            u.pitch = 1;
                            u.rate = 1;
                            u.voice = voices[10];
                            u.voiceURI = 'native';
                            u.volume = 1;
                            speechSynthesis.speak(u);
                            console.log("Voice " + u.voice.name);
                        }}
                    }});
                </script>
            </head>
            <body>
                Here is a joke about <b>{0}</b>:
                <div id='joke'>{1}</div>
            </body>
        </html>""".format(best_match, joke)
        return html_content

    
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
        q = "SELECT ?name WHERE {{ ?genre a <http://www.semanticweb.org/joke_ontology#Genre>. ?genre joke-ontology:name ?name }}"
        r = self.g.query(q)
        cat = []
        for i in r:
            cat.append(i[0])
        return cat

    def getJoke(self, genre):
        q = "SELECT ?content WHERE {{ ?joke a <http://www.semanticweb.org/joke_ontology#TextJoke>. ?joke joke-ontology:content ?content. ?joke joke-ontology:hasGenre <http://www.semanticweb.org/joke_ontology#{0:s}> }}".format(genre)
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
    rdf_file = "joke_ontology_rdf.owl"
    cherrypy.quickstart(JokeGenerator(rdf=rdf_file))
