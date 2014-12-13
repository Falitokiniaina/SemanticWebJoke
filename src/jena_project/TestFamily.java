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

public class TestFamily {

	public static void main(String[] args) {			
		// TODO Auto-generated method stub		
		// create the base model
		String SOURCE = "http://lacot.org/public/owl/famille";
		String NS = SOURCE + "#";
		OntModel model = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM );
		model.read( "family_test.owl", "RDF/XML" );
		
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
		
		//study of a specific class "Humain"
		OntClass humain = model.getOntClass( NS + "Humain" );				
		for (Iterator<OntClass> i = humain.listSubClasses(); i.hasNext(); ) {
		  OntClass c = i.next();
		  System.out.println( c.getURI() );
		}	
		
		//take the subclass of a specific class here humain
		for (Iterator<OntClass> i = humain.listSubClasses(); i.hasNext(); ) {
			  OntClass c = i.next();
			  System.out.println( c.getURI() );
		}
				
		//create individual of "humain"
		Individual indivHumain = model.createIndividual( NS + "humainTest", humain );
		indivHumain.addComment("humain test", "individu humain");
		OntProperty nom = model.getOntProperty(NS+"nom");
		indivHumain.addProperty(nom,"RABEARISON");
		
		ExtendedIterator instances = humain.listInstances();
		System.out.println("new List");
		while (instances.hasNext())		
			{		
				Individual thisInstance = (Individual) instances.next();		
				System.out.println("  Found instance for class " + humain.toString() + ": "+ thisInstance.toString());		
			}				
		
		//Write the new model in file
		FileOutputStream output = null;
		try  {
		  output = new FileOutputStream( "new_owl.owl");     
		} catch(Exception e) {}
		model.writeAll(output, "RDF/XML","xmlbase");
		
		
		System.out.println("here");
	}
}
