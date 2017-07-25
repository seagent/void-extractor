package tr.edu.ege.seagent.voidextractor.component;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;

public class LinksetProperties {

	/**
	 * It creates a linkset concept with these given properties
	 * 
	 * @param sourceDataset
	 * @param destinationDataset
	 * @param linkPredicate
	 * @param sourceOntologyURI
	 */
	public LinksetProperties(OntModel sourceDataset,
			OntModel destinationDataset, Resource linkPredicate,
			String sourceOntologyURI) {
		this.sourceDataset = sourceDataset;
		this.destinationDataset = destinationDataset;
		this.linkPredicate = linkPredicate;
		this.sourceOntologyURI = sourceOntologyURI;
	}

	/**
	 * Source dataset of linkset.
	 */
	private OntModel sourceDataset;
	/**
	 * Destination dataset of linkset.
	 */
	private OntModel destinationDataset;
	/**
	 * Linkpredicate of linkset.
	 */
	private Resource linkPredicate;
	/**
	 * Ontology URI of the owner model of linkset.
	 */
	private String sourceOntologyURI;

	public OntModel getSourceDataset() {
		return sourceDataset;
	}

	public OntModel getDestinationDataset() {
		return destinationDataset;
	}

	public Resource getLinkPredicate() {
		return linkPredicate;
	}

	public String getSourceOntologyURI() {
		return sourceOntologyURI;
	}

}
