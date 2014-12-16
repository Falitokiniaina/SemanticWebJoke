package jena_project;

import java.io.FileOutputStream;
import java.util.Iterator;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class TestJoke {

	public static void main(String[] args) {			
		// TODO Auto-generated method stub		
		// create the base model

		OntModel model = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM );
		model.read( "jokeOntology_rdf.owl", "TURTLE" );
		
		//Jena: Listing All Classes And Instances In A Jena Ontology Model
		ExtendedIterator classes = model.listClasses();
		while (classes.hasNext()){		
			OntClass thisClass = (OntClass) classes.next();		
			System.out.println("Found class: " + thisClass.toString());
			ExtendedIterator instances = thisClass.listInstances();				 		
			while (instances.hasNext()){		
				Individual thisInstance = (Individual) instances.next();		
				System.out.println("  Found instance: " + thisInstance.toString());		
			}				
		}
		
		//Write the new model in file
		FileOutputStream output = null;
		try  {
		  output = new FileOutputStream( "new_owl.owl");     
		} catch(Exception e) {}
		model.writeAll(output, "TURTLE");
		
		/*
		 * 		ExtendedIterator classes = model.listClasses();
		String textJokeClass = "";
		while (classes.hasNext()){		
			OntClass thisClass = (OntClass) classes.next();		
			if(thisClass.toString().endsWith("#TextJoke"))
				textJokeClass = thisClass.toString();
		}		
				
		String NS[] = textJokeClass.split("#TextJoke");
		System.out.println(NS[0]);
		OntClass textJokeRes = model.getOntClass(textJokeClass);
		OntProperty nameProp = model.getOntProperty(NS[0]+"#jokeName");
		OntProperty descProp = model.getOntProperty(NS[0]+"#description");
		OntProperty contentProp = model.getOntProperty(NS[0]+"#content");
		OntProperty urlProp = model.getOntProperty(NS[0]+"#url");
		
		System.out.println(nameProp);
		
		textJokeRes.createIndividual(NS[0]+"#joke1")
				.addProperty(nameProp, "testName") 
			    .addProperty(descProp, "testDescrip")   
			    .addProperty(contentProp, "testContent")
			    .addProperty(urlProp, "testURL"); 		
		
		FileOutputStream output = null;
		try  {
		  output = new FileOutputStream( "new_owl.owl");     
		} catch(Exception e) {}
		model.writeAll(output, "TURTLE");	
		 * 
		 */
		
	
								
		System.out.println("here");
	}
}
