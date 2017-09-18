package tr.edu.ege.seagent.voidextractor;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import tr.edu.ege.seagent.dataset.vocabulary.VOIDIndividualOntology;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class LinkPredicateExtractor {

	public Logger logger = Logger.getLogger(LinkPredicateExtractor.class);

	/**
	 * FIXME: tüm dataseti dolaşan en etkin çözümleme yöntemi kurulacak. Linkset'ler
	 * hashmap'e atılıp tek bir kez üretilmesi kontrol edilecek.
	 * 
	 * This method creates Linkset descriptions for given voidIndividual ontology
	 * 
	 * @param allVoidIndvModels
	 * @param referrer
	 *            TODO
	 * @param uriSpace
	 *            uri space that triples of given dataset contains
	 * @param referenced
	 *            number of referred dataset in the allVoidIndvModels
	 */
	public List<Individual> createLinksetForGivenIndividualOntology(List<VOIDIndividualOntology> allVoidIndvModels,
			String[] uriSpaces, int referrer) {
		VOIDIndividualOntology referrerVOID = allVoidIndvModels.get(referrer);
		List<Individual> linksetIndividuals = new ArrayList<Individual>();
		String sparqlServiceEndPoint = "";
		HashMap<String, Individual> linksetMap = new HashMap<String, Individual>();

		try {

			int offsetIncValue = 100000;
			boolean continueBlockQuery = true;
			long offsetIndex = 0;
			while (continueBlockQuery) {
				// generate linkPredicate query
				String linkPredicateQuery = generateLinkPredicateQueryOfObject(offsetIndex);
				// String linkPredicateQuery =
				// generateLinkPredicateQueryOfObject(isQueryForObjectVariable,
				// uriSpaces[referenced]);
				// get sparql endpoint of given individual ontology
				sparqlServiceEndPoint = getEndpointOfGivenVoidModel(referrerVOID.getOntModel());
				// execute query
				QueryExecution queryExecution = QueryExecutionFactory.sparqlService(sparqlServiceEndPoint,
						linkPredicateQuery);
				ResultSet resultSet = queryExecution.execSelect();
				logger.info(MessageFormat.format("Querying endpoint {0} with query {1}", sparqlServiceEndPoint,
						linkPredicateQuery));
				// get linkpredicates from result
				while (resultSet.hasNext()) {
					// TODO: çok daha iyi bir yöntem düşünülecek
					QuerySolution querySolution = (QuerySolution) resultSet.next();
					Resource linkPredicate = querySolution.get("p").asResource();
					Resource subject = querySolution.getResource("s");
					RDFNode object = querySolution.get("o");
					for (int referenced = 0; referenced < uriSpaces.length; referenced++) {

						if (referrer != referenced) {
							if (object.isResource() && object.asResource().getURI().startsWith(uriSpaces[referenced])) {
								String key = generateKey(referrerVOID, linkPredicate,
										allVoidIndvModels.get(referenced));
								if (linksetMap.get(key) == null) {
									// create linkset description
									Individual linksetIndv = createLinksetIndividual(allVoidIndvModels, referrerVOID,
											linkPredicate, allVoidIndvModels.get(referenced));
									linksetIndividuals.add(linksetIndv);
									linksetMap.put(key, linksetIndv);
								}
							} else if (subject.getURI().startsWith(uriSpaces[referenced])) {
								String key = generateKey(allVoidIndvModels.get(referenced), linkPredicate,
										referrerVOID);
								if (linksetMap.get(key) == null) {
									// create linkset description
									Individual linksetIndv = createLinksetIndividual(allVoidIndvModels,
											allVoidIndvModels.get(referenced), linkPredicate, referrerVOID);
									linksetIndividuals.add(linksetIndv);
									linksetMap.put(key, linksetIndv);
								}
							}
						}

					}
				}
				// If getting row count slower than 20001 Query finish log...
				if (resultSet.getRowNumber() < offsetIncValue) {
					logger.info("Query is completed");
					continueBlockQuery = false;
				}
				logger.info(MessageFormat.format("Seeked row: \"{0}\"", offsetIndex));
				// increment offset for another execution..
				offsetIndex += offsetIncValue;
				queryExecution.close();
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return linksetIndividuals;
	}

	private String generateKey(VOIDIndividualOntology firstVoid, Resource linkPredicate,
			VOIDIndividualOntology secondVoid) {
		String key = "";
		key += firstVoid.listDatasets().get(0).getURI();
		key += linkPredicate.getURI();
		key += secondVoid.listDatasets().get(0).getURI();
		return key;
	}

	/**
	 * This method creates linkset individual according to generated query is for
	 * subject or object
	 * 
	 * @param allVoidIndvModels
	 * @param referrerVOID
	 * @param linkPredicate
	 * @param referencedVOID
	 *            TODO
	 * @return
	 */
	private Individual createLinksetIndividual(List<VOIDIndividualOntology> allVoidIndvModels,
			VOIDIndividualOntology referrerVOID, Resource linkPredicate, VOIDIndividualOntology referencedVOID) {
		return referrerVOID.createLinkset(linkPredicate, referencedVOID.listDatasets().get(0),
				referrerVOID.listDatasets().get(0));
	}

	/**
	 * This method returns sparqlEnpoint value of given ontModel
	 * 
	 * @param voidModel
	 * @return
	 */
	public String getEndpointOfGivenVoidModel(OntModel voidModel) {
		return voidModel.listObjectsOfProperty(ResourceFactory.createProperty("http://rdfs.org/ns/void#sparqlEndpoint"))
				.next().asLiteral().getString();
	}

	/**
	 * This method generates object link predicate query for given uriSpace
	 * 
	 * @return generated link predicate query
	 */
	public String generateLinkPredicateQueryOfObject(boolean isObjectQuery, String uriSpace) {
		if (isObjectQuery) {
			return "SELECT DISTINCT ?p WHERE {?s ?p ?o FILTER REGEX (str(?o),\"^" + uriSpace + "\")}";
		} else {
			return "SELECT DISTINCT ?p WHERE {?s ?p ?o FILTER REGEX (str(?s),\"^" + uriSpace + "\")}";
		}
	}

	/**
	 * This method generates object link predicate query for given uriSpace
	 * 
	 * @return generated link predicate query
	 */
	public String generateLinkPredicateQueryOfObject(long offset) {
		return "SELECT * WHERE {?s ?p ?o } LIMIT 100000 OFFSET " + offset;
	}

	public void writeErrorLogIntoFile(String errorLog) {
		try {
			FileOperations.writeFile(FileOperations.BROKEN_ENDPOINTS_PATH, true, errorLog);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

}