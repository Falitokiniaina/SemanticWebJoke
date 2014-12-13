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
		model.read( "jokeOntology_rdf.owl", "RDF/XML" );
		
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
		
		System.out.println("here");
	}
}
