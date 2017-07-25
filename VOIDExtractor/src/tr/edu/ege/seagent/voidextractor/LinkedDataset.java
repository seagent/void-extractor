package tr.edu.ege.seagent.voidextractor;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.rdf.model.Property;

public class LinkedDataset {

	private Individual linkedDataset;
	private Property linkPredicate;

	public LinkedDataset(Individual linkedDataset, Property linkPredicate) {
		super();
		this.linkedDataset = linkedDataset;
		this.linkPredicate = linkPredicate;
	}

	public Property getLinkPredicate() {
		return linkPredicate;
	}

	public Individual getLinkedDataset() {
		return linkedDataset;
	}

}
