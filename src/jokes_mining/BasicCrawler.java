/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jokes_mining;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

import java.io.FileOutputStream;
//import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.http.Header;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

/**
 * @author Yasser Ganjisaffar <lastname at gmail dot com>
 */
public class BasicCrawler extends WebCrawler {
    
	static OntModel model;
	static FileOutputStream output = null;
	String NS;	
	OntClass textJokeRes, GenreClassRes;
	OntProperty nameProp, descProp, contentProp, urlProp;  

  private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g" 
                                                       + "|png|tiff?|mid|mp2|mp3|mp4"
                                                       + "|wav|avi|mov|mpeg|ram|m4v|pdf" 
                                                       + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

  public BasicCrawler() {
	super();
	BasicCrawler.model = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM );      
	BasicCrawler.model.read( "jokeOntology_rdf.owl", "RDF/XML" );	
	
	ExtendedIterator classes = BasicCrawler.model.listClasses();
	String textJokeClass = "";
	String genreClass = "";
	while (classes.hasNext()){		
		OntClass thisClass = (OntClass) classes.next();		
		if(thisClass.toString().endsWith("#TextJoke"))
			textJokeClass = thisClass.toString();
		if(thisClass.toString().endsWith("#Genre"))
			genreClass = thisClass.toString();
	}
	
	this.NS = textJokeClass.split("TextJoke")[0];					//NS:Namespace ->http://www.semanticweb.org/marc/ontologies/2014/10/untitled-ontology-8#
	this.textJokeRes = model.getOntClass(textJokeClass);
	this.GenreClassRes = model.getOntClass(genreClass);
	this.nameProp = model.getOntProperty(NS+"jokeName");
	this.descProp = model.getOntProperty(NS+"description");
	this.contentProp = model.getOntProperty(NS+"content");
	this.urlProp = model.getOntProperty(NS+"url");    
	try  {
	  output = new FileOutputStream( "new_owl.owl");     
	} catch(Exception e) {
			System.out.println("ERRRRRRRRRRRRRRRRRRRROOOOOOOOOOOORRRRR : writing on new_owl.owl");
	}  	
  }  
  
  /**
   * You should implement this function to specify whether the given url
   * should be crawled or not (based on your crawling logic).
   */
  @Override
  public boolean shouldVisit(WebURL url) {
    String href = url.getURL().toLowerCase();
    return !FILTERS.matcher(href).matches() && href.startsWith("http://jokes.cc.com/");
  }

  /**
   * This function is called when a page is fetched and ready to be processed
   * by your program.
   */
  @Override
  public void visit(Page page) {	  
    int docid = page.getWebURL().getDocid();
    String url = page.getWebURL().getURL();
    String domain = page.getWebURL().getDomain();
    String path = page.getWebURL().getPath();
    String subDomain = page.getWebURL().getSubDomain();
    String parentUrl = page.getWebURL().getParentUrl();
    String anchor = page.getWebURL().getAnchor();

    /*System.out.println("Docid: " + docid);
    System.out.println("URL: " + url);
    System.out.println("Domain: '" + domain + "'");
    System.out.println("Sub-domain: '" + subDomain + "'");
    System.out.println("Path: '" + path + "'");
    System.out.println("Parent page: " + parentUrl);
    System.out.println("Anchor text: " + anchor);*/            	

    if (page.getParseData() instanceof HtmlParseData) {    	    			        	
      HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
      String text = htmlParseData.getText();
      String html = htmlParseData.getHtml();
      List<WebURL> links = htmlParseData.getOutgoingUrls();
      
      //
      Document doc = Jsoup.parseBodyFragment(html);
      //Element jokeContainer = doc.select("div.module_content").first();
      Elements jokeHeader = doc.select("div.module > div.module_content > div.header > meta");
      String jokeName = null;
      String jokeUrl = null;
      String jokeDesc = null;
      for (Element e : jokeHeader) {
          //if (e.tagName().equals("meta")) {
          if (e.attr("itemprop").equals("name")) jokeName = e.attr("content");
          else if (e.attr("itemprop").equals("url")) jokeUrl = e.attr("content");
          else if (e.attr("itemprop").equals("description")) jokeDesc = html2text(e.attr("content"));
      }      
      String joke = doc.select("div.module > div.module_content > div.middle >" +
                               "div.arrow_area > div.content_wrap > p").text();
      Elements jokeTags = doc.select("div.module > div.module_content > div.middle >" +
                                     "div.module_teaser > div.tags > a");
      List<String> jokeCategory = new ArrayList<String>(); 
      for (Element e : jokeTags) {
          if (e.tagName().equals("a")) jokeCategory.add(e.getElementsByTag("span").text());
      }                                
      if (jokeName != null && jokeDesc != null && jokeUrl != null) {    	  
          System.out.println("CATEGORIES");
          for (String s : jokeCategory) {        	  
              System.out.println(s);
          }
          System.out.println("\nNAME\n" + jokeName + "\n");
          System.out.println("DESCRIPTION\n" + jokeDesc + "\n");
          System.out.println("JOKE\n" + joke + "\n");
          System.out.println("URL\n" + jokeUrl + "\n");
                    
  		 //GenreClassRes.createIndividual()          
          String newGenre;
          for (String s : jokeCategory) {        	
        	  newGenre = this.clean_string(s);        	  
      		  ExtendedIterator instances = GenreClassRes.listInstances();
      		  boolean found = false;
      		  while (instances.hasNext())		
      			{		
      				Individual thisInstance = (Individual) instances.next();
      				if(thisInstance.toString().endsWith(newGenre)){found=true; break;}      						
      			}	        	  
      		  if(!found){
      			model.createIndividual(NS+newGenre, GenreClassRes);
      		  }              
          }          
          
         //textJokeRes.createIndividual(NS+"#joke1")
          String indivName = this.clean_string(jokeName);          
          model.createIndividual(NS+indivName,textJokeRes)  			
          	.addProperty(nameProp, jokeName) 
			.addProperty(descProp, jokeDesc)   
			.addProperty(contentProp, joke)
			.addProperty(urlProp, jokeUrl);
          Individual ind = model.getIndividual(NS+indivName);
          for (String s : jokeCategory) {        	  
        	  newGenre = this.clean_string(s);        	  
        	  ind.addProperty(model.getOntProperty(NS+"hasGenre"), model.getIndividual(NS+newGenre));
          }           	                    			
  		//model.commit();
  		model.writeAll(BasicCrawler.output, "TURTLE");  		
      }      
    }
    Header[] responseHeaders = page.getFetchResponseHeaders();
    System.out.println("=============");
  }
  
  private String clean_string(String s) {
	  return s.replace("jokes","").replace("joke", "").replace(" ", "").replace("'", "").replace("&","").replace(";", "").toLowerCase();	
}

public static String html2text(String html) {
    return Jsoup.parse(html).text();
  }
}



