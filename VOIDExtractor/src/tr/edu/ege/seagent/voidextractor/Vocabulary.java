package tr.edu.ege.seagent.voidextractor;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class Vocabulary {

	private static final String VOID_PREFIX = "http://rdfs.org/ns/void#";
	public static final Object RDF_TYPE_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
	public static final Property VOID_VOCABULARY_PROPERTY = ResourceFactory
			.createProperty(VOID_PREFIX + "vocabulary");
	public static final Property VOID_URISPACE_PROPERTY = ResourceFactory
			.createProperty(VOID_PREFIX + "uriSpace");
	public static final String VOID_ONTOLOGY_URI_PREFIX = "http://datasets/";
	public static final String VOID_FILE_LOCATION_PREFIX = "/datasets";
	public static final String RDF_SCHEMA_PREFIX = "http://www.w3.org/2000/01/rdf-schema#";
	public static final String RDF_PREFIX = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public static final String OWL_PREFIX = "http://www.w3.org/2002/07/owl#";
	public static final String XML_SCHEMA_PREFIX = "http://www.w3.org/2001/XMLSchema#";

}
