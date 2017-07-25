package tr.edu.ege.seagent.voidextractor;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import tr.edu.ege.seagent.dataset.vocabulary.VOIDIndividualOntology;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class LinkPredicateExtractor {

	public Logger logger = Logger.getLogger(LinkPredicateExtractor.class);

	/**
	 * This method creates Linkset descriptions for given voidIndividual
	 * ontology
	 * 
	 * @param allVoidIndvModels
	 * @param uriSpace
	 *            uri space that triples of given dataset contains
	 * @param referredDatasetNumber
	 *            number of referred dataset in the allVoidIndvModels
	 * @param referrerDatasetNumber
	 *            TODO
	 * @param isQueryForObjectVariable
	 *            TODO
	 */
	public List<Individual> createLinksetForGivenIndividualOntology(
			List<VOIDIndividualOntology> allVoidIndvModels, String uriSpace,
			int referredDatasetNumber, int referrerDatasetNumber,
			boolean isQueryForObjectVariable) {
		VOIDIndividualOntology referrerVOID = allVoidIndvModels
				.get(referrerDatasetNumber);
		List<Individual> linksetIndividuals = new ArrayList<Individual>();
		String sparqlServiceEndPoint = "";
		try {
			if (referrerDatasetNumber != referredDatasetNumber) {
				// generate linkPredicate query
				String linkPredicateQuery = generateLinkPredicateQueryOfObject(
						uriSpace, isQueryForObjectVariable);
				// get sparql endpoint of given individual ontology
				sparqlServiceEndPoint = getEndpointOfGivenVoidModel(referrerVOID
						.getOntModel());
				// execute query
				QueryExecution queryExecution = QueryExecutionFactory
						.sparqlService(sparqlServiceEndPoint,
								linkPredicateQuery);
				ResultSet resultSet = queryExecution.execSelect();
				logger.info(MessageFormat.format("Querying endpoint {0}",
						sparqlServiceEndPoint));
				// get linkpredicates from result
				while (resultSet.hasNext()) {
					QuerySolution querySolution = (QuerySolution) resultSet
							.next();
					Resource linkPredicate = querySolution.get("p")
							.asResource();
					// create linkset description
					Individual linksetIndv = createLinksetIndividual(
							allVoidIndvModels, referredDatasetNumber,
							referrerVOID, linkPredicate,
							isQueryForObjectVariable);
					linksetIndividuals.add(linksetIndv);
				}
			}
		} catch (Exception e) {
			String errorLog = MessageFormat
					.format("VOID number {0}, requested Endpoint ##{1}## is not available ,caused by: {2}",
							referrerDatasetNumber, sparqlServiceEndPoint,
							e.getCause());
			logger.error(errorLog);
			writeErrorLogIntoFile(errorLog);
		}
		return linksetIndividuals;
	}

	/**
	 * This method creates linkset individual according to generated query is
	 * for subject or object
	 * 
	 * @param allVoidIndvModels
	 * @param referredDatasetNumber
	 * @param referrerVOID
	 * @param linkPredicate
	 * @param isQueryForObjectVariable
	 * @return
	 */
	private Individual createLinksetIndividual(
			List<VOIDIndividualOntology> allVoidIndvModels,
			int referredDatasetNumber, VOIDIndividualOntology referrerVOID,
			Resource linkPredicate, boolean isQueryForObjectVariable) {
		// check generated query type
		if (isQueryForObjectVariable) {
			return referrerVOID.createLinkset(linkPredicate, allVoidIndvModels
					.get(referredDatasetNumber).listDatasets().get(0),
					referrerVOID.listDatasets().get(0));
		} else {
			return referrerVOID.createLinkset(linkPredicate, referrerVOID
					.listDatasets().get(0),
					allVoidIndvModels.get(referredDatasetNumber).listDatasets()
							.get(0));
		}
	}

	/**
	 * This method returns sparqlEnpoint value of given ontModel
	 * 
	 * @param voidModel
	 * @return
	 */
	public String getEndpointOfGivenVoidModel(OntModel voidModel) {
		return voidModel
				.listObjectsOfProperty(
						ResourceFactory
								.createProperty("http://rdfs.org/ns/void#sparqlEndpoint"))
				.next().asLiteral().getString();
	}

	/**
	 * This method generates object link predicate query for given uriSpace
	 * 
	 * @param uriSpace
	 *            to its query to be generated.
	 * @param isObjectQuery
	 *            TODO
	 * @return generated link predicate query
	 */
	public String generateLinkPredicateQueryOfObject(String uriSpace,
			boolean isObjectQuery) {
		if (isObjectQuery) {
			return "SELECT DISTINCT ?p WHERE {?s ?p ?o FILTER REGEX (?o,\"^"
					+ uriSpace + "\")}";
		} else {
			return "SELECT DISTINCT ?p WHERE {?s ?p ?o FILTER REGEX (?s,\"^"
					+ uriSpace + "\")}";
		}
	}

	public void writeErrorLogIntoFile(String errorLog) {
		try {
			FileOperations.writeFile(FileOperations.BROKEN_ENDPOINTS_PATH,
					true, errorLog);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

}
