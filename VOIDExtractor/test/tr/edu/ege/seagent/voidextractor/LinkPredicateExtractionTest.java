package tr.edu.ege.seagent.voidextractor;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import tr.edu.ege.seagent.dataset.vocabulary.VOIDIndividualOntology;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class LinkPredicateExtractionTest {

	public List<VOIDIndividualOntology> allVoidIndvModels;
	public LinkPredicateExtractor linkPredicateExtractor;
	public Logger logger = Logger.getLogger(LinkPredicateExtractionTest.class);
	public List<Integer> brokenVoidNumberList;

	@Before
	public void before() throws Exception {
		generateBrokenVoidList();
		readIndividualOntologiesToModel();
		linkPredicateExtractor = new LinkPredicateExtractor();
	}

	/**
	 * This method generates broken VOID number list to not to query their
	 * endpoints
	 */
	private void generateBrokenVoidList() {
		// generate broken void number list and avoid them
		brokenVoidNumberList = new ArrayList<Integer>();
		int brokenVoidNumbers[] = { 4, 5, 7, 9, 10, 13, 14, 17, 19, 24, 25, 27,
				29, 30, 31, 33, 34, 35, 36, 38, 41, 43, 45, 47, 49, 52, 59, 63,
				64, 65, 70 };
		for (int i = 0; i < brokenVoidNumbers.length; i++) {
			brokenVoidNumberList.add(brokenVoidNumbers[i]);
		}
	}

	/**
	 * This method reads all individual ontologies to the model
	 * 
	 * @throws MalformedURLException
	 */
	private void readIndividualOntologiesToModel() throws MalformedURLException {
		// first read all void models
		allVoidIndvModels = new VOIDExtractor(null).readFilesIntoModel(
				FileOperations.REFRESHED_VOIDS);
	}

	@Test
	public void getLinkPredicatesOfOneEndpointForGivenUriSpace()
			throws Exception {
		// generate query string
		String linkPredicateQuery = linkPredicateExtractor
				.generateLinkPredicateQueryOfObject(
						ExampleVocabulary.GEONAMES_URI_SPACE, true);
		// execute query on given sparql endpoint
		QueryExecution queryExecution = QueryExecutionFactory.sparqlService(
				"http://155.223.24.47:8891/nytimes/sparql", linkPredicateQuery);
		ResultSet resultSet = queryExecution.execSelect();

		// iterate on result set
		List<String> solutionList = new ArrayList<String>();
		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			// add solution to list
			solutionList.add(querySolution.get("p").asResource().getURI());
		}
		// check solution size
		assertEquals(5, solutionList.size());
	}

	@Test
	public void updateLinkPredicateOfOneDataset() throws Exception {

		// adjust query type
		boolean isQueryForObject = false;

		// update and check for geonames uri space
		List<Individual> geoLinks = linkPredicateExtractor
				.createLinksetForGivenIndividualOntology(allVoidIndvModels,
						ExampleVocabulary.GEONAMES_URI_SPACE, 70, 66,
						isQueryForObject);
		checkLinksets(geoLinks, allVoidIndvModels.get(66),
				allVoidIndvModels.get(70), isQueryForObject);

		// update and check for drugbank uri space
		List<Individual> drugbankLinks = linkPredicateExtractor
				.createLinksetForGivenIndividualOntology(allVoidIndvModels,
						ExampleVocabulary.DRUGBANK_URI_SPACE, 69, 66,
						isQueryForObject);
		checkLinksets(drugbankLinks, allVoidIndvModels.get(66),
				allVoidIndvModels.get(69), isQueryForObject);

		// update and check for chebi uri space
		List<Individual> chebiLinks = linkPredicateExtractor
				.createLinksetForGivenIndividualOntology(allVoidIndvModels,
						ExampleVocabulary.CHEBI_URI_SPACE, 68, 66,
						isQueryForObject);
		checkLinksets(chebiLinks, allVoidIndvModels.get(66),
				allVoidIndvModels.get(68), isQueryForObject);

		// update and check for kegg uri space
		List<Individual> keggLinks = linkPredicateExtractor
				.createLinksetForGivenIndividualOntology(allVoidIndvModels,
						ExampleVocabulary.KEGG_URI_SPACE, 67, 66,
						isQueryForObject);
		checkLinksets(keggLinks, allVoidIndvModels.get(66),
				allVoidIndvModels.get(67), isQueryForObject);

		// write updated void model into file after all.
		FileOperations.writeVoidModelFile(allVoidIndvModels.get(66)
				.getOntModel(), 66, FileOperations.REFRESHED_VOIDS);
	}

	/**
	 * This method checks subject and object values of created linkset
	 * individuals
	 * 
	 * @param linksetIndvs
	 * @param referrerVOID
	 * @param referredVOID
	 * @param isQueryForObject
	 *            TODO
	 */
	private void checkLinksets(List<Individual> linksetIndvs,
			VOIDIndividualOntology referrerVOID,
			VOIDIndividualOntology referredVOID, boolean isQueryForObject) {
		if (linksetIndvs != null && !linksetIndvs.isEmpty()) {
			// iterate on all linkset individuals
			for (Individual linksetIndv : linksetIndvs) {
				// get subject value of linkset individual
				RDFNode subject = linksetIndv
						.getProperty(
								ResourceFactory
										.createProperty("http://rdfs.org/ns/void#subjectsTarget"))
						.getObject();
				// get object value of linkset individual
				RDFNode object = linksetIndv
						.getProperty(
								ResourceFactory
										.createProperty("http://rdfs.org/ns/void#objectsTarget"))
						.getObject();
				// check subject and object values are correct
				if (isQueryForObject) {
					assertEquals(referrerVOID.listDatasets().get(0), subject);
					assertEquals(referredVOID.listDatasets().get(0), object);
				} else {
					assertEquals(referrerVOID.listDatasets().get(0), object);
					assertEquals(referredVOID.listDatasets().get(0), subject);
				}
			}
		}
	}

	@Test
	public void updateLinkPredicatesOAllfDatasets() throws Exception {
		for (int i = 0; i < allVoidIndvModels.size(); i++) {

			// check void is not in the broken void list
			if (!brokenVoidNumberList.contains(i)) {
				// adjust query type
				boolean isQueryForObject = false;

				VOIDIndividualOntology voidIndividualOntology = allVoidIndvModels
						.get(i);

				// update and check for geonames uri space
				List<Individual> geoLinks = linkPredicateExtractor
						.createLinksetForGivenIndividualOntology(
								allVoidIndvModels,
								ExampleVocabulary.JAMENDO_URI_SPACE, 72, i,
								isQueryForObject);
				checkLinksets(geoLinks, voidIndividualOntology,
						allVoidIndvModels.get(72), isQueryForObject);

				// update and check for chebi uri space
				List<Individual> drugbankLinks = linkPredicateExtractor
						.createLinksetForGivenIndividualOntology(
								allVoidIndvModels,
								ExampleVocabulary.SW_DOGFOOD_URI_SPACE, 71, i,
								isQueryForObject);
				checkLinksets(drugbankLinks, voidIndividualOntology,
						allVoidIndvModels.get(71), isQueryForObject);

				// write updated void model into file after all.
				FileOperations.writeVoidModelFile(
						voidIndividualOntology.getOntModel(), i,
						FileOperations.REFRESHED_VOIDS);
			}

		}
	}

}
