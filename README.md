# SemanticWebJoke
the Joke Ontology and Web Application

Architecture:
RDF/OWL design with Protégé
  ● web crawling in Java crawler4j
  ● dynamic pages:
    ○ server side with Python NLTK, cherrypy, rdflib
    ○ client side with Javascript jQuery
    ○ cross-domain compatibility with PHP
  ● user interaction via web browser
  
so If you want to run the project, we have already crawled 2000 jokes with "BasicCrawlerController.java", all of them are stored
in new_owl.owl. In the web service is run the SPARQL. So just
  1) run "joke_service.py" will use port 8080 and may be set up.
    To check if it works correctly, go to http://localhost:8080.
  2) run PHP/apache server and put the folder "joke_generator" in www of the server.
    With google Chrome, go to http://localhost:<your_apache_port>/joke_generator/index_joke.php
  3) Have fun.

January 22, 2015
Marcello Benedetti <4marcello@gmail.com>
Falitokiniaina Rebearison <r.falitokiniaina@gmail.com>
  
