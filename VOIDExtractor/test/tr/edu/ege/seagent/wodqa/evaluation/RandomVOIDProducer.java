package tr.edu.ege.seagent.wodqa.evaluation;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Vector;

import tr.edu.ege.seagent.dataset.vocabulary.VOIDIndividualOntology;
import tr.edu.ege.seagent.voidextractor.component.LinksetProperties;
import tr.edu.ege.seagent.wodqa.VOIDCreator;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class RandomVOIDProducer {

	/**
	 * It cretes a VOID model with these given basic properties.
	 * 
	 * @param vocList
	 * @param urispaceList
	 * @param ontURI
	 *            TODO
	 * @throws MalformedURLException
	 */
	public static OntModel createRandomVOIDModel(List<String> vocList,
			List<RDFNode> urispaceList, String ontURI)
			throws MalformedURLException {
		OntModel createdVOID = VOIDCreator.createVOID(
				"http://randomEndpoint/sparql", ontURI);
		VOIDIndividualOntology indvOnt = new VOIDIndividualOntology(ontURI,
				createdVOID, null);
		for (RDFNode urispace : urispaceList) {
			VOIDCreator.addUrispacePropertyToAllIndv(createdVOID, indvOnt,
					urispace);
		}
		for (String vocabulary : vocList) {
			VOIDCreator.addVocabularyPropertyToAllIndv(createdVOID, indvOnt,
					vocabulary);
		}
		return createdVOID;
	}

	/**
	 * It is used to create linkset individuals.
	 * 
	 * @param linksetsList
	 */
	public static void addLinksetsToVOID(List<LinksetProperties> linksetsList) {
		for (LinksetProperties linksetProperties : linksetsList) {
			VOIDCreator.createLinksets(linksetProperties.getSourceDataset(),
					linksetProperties.getDestinationDataset(),
					linksetProperties.getLinkPredicate(),
					linksetProperties.getSourceOntologyURI());
		}
	}

	/**
	 * It creates Linkset properties list to add to dataset.
	 * 
	 * @param linksetProperties
	 * @return
	 */
	public static List<LinksetProperties> createLinksetPropertiesList(
			LinksetProperties... linksetProperties) {
		List<LinksetProperties> linksetPropertiesList = new Vector<LinksetProperties>();
		for (LinksetProperties linksetProperty : linksetProperties) {
			linksetPropertiesList.add(linksetProperty);
		}
		return linksetPropertiesList;
	}

	/**
	 * It creates a linkset properties to create a linkset individual with given
	 * properties.
	 * 
	 * @param sourceDataset
	 * @param destinationDataset
	 * @param linkPredicate
	 * @return
	 */
	public static LinksetProperties createLinksetProperty(
			OntModel sourceDataset, OntModel destinationDataset,
			Resource linkPredicate) {
		String ontURI = sourceDataset.listOntologies().toList().get(0).getURI();
		return new LinksetProperties(sourceDataset, destinationDataset,
				linkPredicate, ontURI);
	}

	/**
	 * It creates urispaces list from the given urispace strings.
	 * 
	 * @param urispaces
	 * @return
	 */
	public static List<RDFNode> createUrispaces(String... urispaces) {
		List<RDFNode> urispacesList = new Vector<RDFNode>();
		for (String string : urispaces) {
			urispacesList.add(ResourceFactory.createPlainLiteral(string));
		}
		return urispacesList;
	}

	/**
	 * It creates vocabulary list to add to a dataset.
	 * 
	 * @param vocabularies
	 * @return
	 */
	public static List<String> createVocabularyList(String... vocabularies) {
		List<String> vocList = new Vector<String>();
		for (String string : vocabularies) {
			vocList.add(string);
		}
		return vocList;
	}
}
