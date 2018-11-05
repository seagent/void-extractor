package tr.edu.ege.seagent.voidextractor;

import java.util.List;

import com.hp.hpl.jena.ontology.Individual;

import tr.edu.ege.seagent.wodqa.voiddocument.VOIDIndividualOntology;

public class VoidLinkPredicates {
	private VOIDIndividualOntology linkerIndividualOntology;
	private VOIDIndividualOntology linkedIndividualOntology;
	private List<Individual> linkPredicateList;

	public VoidLinkPredicates(VOIDIndividualOntology linkerIndividualOntology,
			VOIDIndividualOntology linkedIndividualOntology) {
		super();
		this.linkerIndividualOntology = linkerIndividualOntology;
		this.linkedIndividualOntology = linkedIndividualOntology;
	}

	public VOIDIndividualOntology getLinkerIndividualOntology() {
		return linkerIndividualOntology;
	}

	public void setLinkerIndividualOntology(
			VOIDIndividualOntology linkerIndividualOntology) {
		this.linkerIndividualOntology = linkerIndividualOntology;
	}

	public VOIDIndividualOntology getLinkedIndividualOntology() {
		return linkedIndividualOntology;
	}

	public void setLinkedIndividualOntology(
			VOIDIndividualOntology linkedIndividualOntology) {
		this.linkedIndividualOntology = linkedIndividualOntology;
	}

	public List<Individual> getLinkPredicateList() {
		return linkPredicateList;
	}

	public void setLinkPredicateList(List<Individual> linkPredicateList) {
		this.linkPredicateList = linkPredicateList;
	}

}
