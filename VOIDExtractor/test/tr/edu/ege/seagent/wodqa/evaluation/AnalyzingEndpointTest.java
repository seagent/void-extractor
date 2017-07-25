package tr.edu.ege.seagent.wodqa.evaluation;

import static org.junit.Assert.assertTrue;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import tr.edu.ege.seagent.dataset.vocabulary.VOIDIndividualOntology;
import tr.edu.ege.seagent.dataset.vocabulary.VOIDOntologyVocabulary;
import tr.edu.ege.seagent.voidextractor.FileOperations;
import tr.edu.ege.seagent.voidextractor.Queries;
import tr.edu.ege.seagent.voidextractor.VOIDExtractor;
import tr.edu.ege.seagent.voidextractor.thread.EndpointTimeoutThread;
import tr.edu.ege.seagent.wodqa.QueryElementOperations;
import tr.edu.ege.seagent.wodqa.query.analyzer.QueryAnalyzer;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class AnalyzingEndpointTest {

	private static String testQuery = "SELECT * WHERE {?s ?p ?o} LIMIT 1";
	private Logger logger = Logger.getLogger(EvaluationTest.class);
	private static List<OntModel> voidList;

	@BeforeClass
	public static void before() throws Exception {
		voidList = new ArrayList<OntModel>();
		List<VOIDIndividualOntology> readModels = new VOIDExtractor(null)
				.readFilesIntoModel(FileOperations.COMPLETE_VOIDS);
		for (VOIDIndividualOntology voidIndividualOntology : readModels) {
			voidList.add(voidIndividualOntology.getOntModel());
		}
	}

	@Test
	public void getAskListForOneTriple() throws Exception {
		List<String> askList = new Vector<String>();
		for (OntModel voidModel : voidList) {
			askList.add(voidModel
					.listStatements(null,
							VOIDOntologyVocabulary.DATASET_sparqlEndpoint_prp,
							(Literal) null).toList().get(0).getObject()
					.as(Literal.class).getString().toString());
		}
		Query query = QueryFactory
				.create("ASK {?x <http://www.w3.org/2002/07/owl#sameAs> ?president}");
		List<EndpointTimeoutThread> threadList = new Vector<EndpointTimeoutThread>();
		for (String endpoint : askList) {
			QueryExecution exec = QueryExecutionFactory.sparqlService(endpoint,
					query);
			EndpointTimeoutThread askthread = new EndpointTimeoutThread(exec);
			threadList.add(askthread);
			askthread.start();

		}
		Thread.sleep(50000);
		int index = 0;
		for (EndpointTimeoutThread endpointTimeoutThread : threadList) {
			if (endpointTimeoutThread.getAskResult() == 1)
				System.out.println(askList.get(index));
			index++;
		}
	}

	@Test
	public void askQueryForAllDatasets() throws Exception {
		List<String> sonucList = new Vector<String>();
		List<String> askList = new Vector<String>();
		for (OntModel voidModel : voidList) {
			askList.add(voidModel
					.listStatements(null,
							VOIDOntologyVocabulary.DATASET_sparqlEndpoint_prp,
							(Literal) null).toList().get(0).getObject()
					.as(Literal.class).getString().toString());
		}
		int count = 0;
		QueryAnalyzer analyzer = new QueryAnalyzer((Model) null);

		List<Triple> tripleList = analyzer
				.fillTriplePatternList(Queries.CROSS_DOMAIN_QUERY_1);
		for (Triple triple : tripleList) {
			Query query = QueryFactory.create("ASK {"
					+ QueryElementOperations.convertTripleToString(triple)
					+ "}");
			long now = System.currentTimeMillis();
			List<EndpointTimeoutThread> threadList = new Vector<EndpointTimeoutThread>();
			for (String endpoint : askList) {
				QueryExecution exec = QueryExecutionFactory.sparqlService(
						endpoint, query);
				EndpointTimeoutThread askthread = new EndpointTimeoutThread(
						exec);
				threadList.add(askthread);
				askthread.start();

			}
			// Thread.sleep(2200);
			for (EndpointTimeoutThread endpointTimeoutThread : threadList) {
				while (endpointTimeoutThread.isAlive()) {
				}
				if (endpointTimeoutThread.getAskResult() == 1)
					count++;
			}
			now = System.currentTimeMillis() - now;
			sonucList.add("Triple: " + triple.toString() + " " + now
					+ " mili saniye, " + count + " adet veriseti");
			count = 0;
		}
		System.out.println(sonucList);
	}

	/**
	 * This test checks whether endpoints are available with a simple query
	 * 
	 * @throws Exception
	 */
	@Test
	public void endpointAvailibilityTest() throws Exception {
		List<String> endpointList = new ArrayList<String>();
		// get Endpoint values contained each void
		for (OntModel voidModel : voidList) {
			NodeIterator endPointObjects = voidModel
					.listObjectsOfProperty(ResourceFactory
							.createProperty("http://rdfs.org/ns/void#sparqlEndpoint"));
			while (endPointObjects.hasNext()) {
				RDFNode endPointObject = (RDFNode) endPointObjects.next();
				String endPointValue = endPointObject.asLiteral().getString();
				endpointList.add(endPointValue);
				System.out.println(endPointValue);
			}
		}
		// send a sipmle query to All void enpoints to check whether this
		// enpoint is available or not.
		List<String> brokenEndpoints = new ArrayList<String>();
		for (int i = 0; i < endpointList.size(); i++) {
			String endpointValue = endpointList.get(i);
			try {
				ResultSet resultSet = QueryExecutionFactory.sparqlService(
						endpointValue, testQuery).execSelect();
				assertTrue(resultSet.hasNext());
				logger.info(MessageFormat
						.format("VOID number {0}, requested Endpoint ^^{1}^^ is available",
								i, endpointValue));
			} catch (Exception e) {
				logger.error(MessageFormat
						.format("VOID number {0}, requested Endpoint ##{1}## is not available ,caused by: {2}",
								i, endpointValue, e.getCause()));
				brokenEndpoints.add(endpointValue);
			}
		}

	}
}
