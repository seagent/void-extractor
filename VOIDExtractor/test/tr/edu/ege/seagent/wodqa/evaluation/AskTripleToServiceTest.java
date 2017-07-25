package tr.edu.ege.seagent.wodqa.evaluation;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class AskTripleToServiceTest {

	private Logger logger = Logger.getLogger(this.getClass());

	private String[] endpoints = { "http://localhost:2500/sparql/",
			"http://localhost:9000/sparql/", "http://localhost:8000/sparql/",
			"http://localhost:7000/sparql/", "http://localhost:5500/sparql/",
			"http://localhost:5000/sparql/", "http://localhost:4000/sparql/",
			"http://localhost:3000/sparql/", "http://localhost:2000/sparql/" };

	private String[] triples = QueryTripleContainer.TRIPLES_OF_LS7;

	@Test
	public void askTripleToService() throws Exception {
		for (String triple : triples) {
			List<String> appropriateEndpoints = new ArrayList<String>();
			String query = "ASK{" + triple + "}";
			for (String endpoint : endpoints) {
				QueryExecution execution = QueryExecutionFactory.sparqlService(
						endpoint, query);
				boolean isContained = execution.execAsk();
				if (isContained) {
					appropriateEndpoints.add(endpoint);
				}
				logger.debug(MessageFormat.format(
						"Asking \"{0}\" triple to \"{1}\" service is: \"{2}\"",
						triple, endpoint, getSituation(isContained)));
			}
			printAppropriateEndpoints(triple, appropriateEndpoints);
		}
	}

	private void printAppropriateEndpoints(String triple,
			List<String> appropriateEndpoints) {
		String endpointsText = "";
		for (String endpoint : appropriateEndpoints) {
			endpointsText += "\"" + endpoint + "\", ";
		}
		logger.info(MessageFormat.format(
				"Appropriate endpoints for the triple \"{0}\" are: {1}",
				triple, endpointsText));
	}

	private String getSituation(boolean situationCondition) {
		if (situationCondition) {
			return "positive";
		}
		return "negative";
	}

	@Test
	public void extractPredicatesOfEndpoints() throws Exception {

		String predicate = "http://bio2rdf.org/ns/bio2rdf#mass";
		String query = "ASK {?subject <" + predicate + "> ?object}";
		List<String> included = new ArrayList<String>();
		for (String endpoint : endpoints) {
			boolean isExist = QueryExecutionFactory.sparqlService(endpoint,
					query).execAsk();
			if (isExist) {
				included.add(endpoint);
			}
		}
		String endpointsText = "";
		for (String endpoint : included) {
			endpointsText += endpoint + ", ";
		}
		logger.info(MessageFormat.format(
				"Predicate \"{0}\" included by the endpoints \"{1}\"",
				predicate, endpointsText));

	}

	@Test
	public void testName() throws Exception {
		QueryExecution queryExecution = QueryExecutionFactory
				.create("SELECT ?party ?page  WHERE {SERVICE <http://localhost:7000/sparql/>{<http://dbpedia.org/resource/Barack_Obama> <http://dbpedia.org/ontology/party> ?party .} SERVICE <http://localhost:9000/sparql/>{?x <http://data.nytimes.com/elements/topicPage> ?page . ?x <http://www.w3.org/2002/07/owl#sameAs> <http://dbpedia.org/resource/Barack_Obama> .}}",
						ModelFactory.createDefaultModel());
		ResultSet resultSet = queryExecution.execSelect();
		int i = 0;
		for (i = 0; resultSet.hasNext(); i++) {
			resultSet.next();
		}
		System.out.println(i);
	}
}
