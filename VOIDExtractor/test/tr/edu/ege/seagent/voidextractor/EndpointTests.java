package tr.edu.ege.seagent.voidextractor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;

import tr.edu.ege.seagent.wodqa.voiddocument.VOIDIndividualOntology;

public class EndpointTests {
	private static final String SERVICE = "SERVICE";

	Logger logger = Logger.getLogger(this.getClass());

	private static final String UNION = "UNION";
	private static final String BIND = "BIND";

	private String DBPEDIA_ENDPOINT_URI = "http://dbpedia.org/sparql";
	private String LMDB_ENDPOINT_URI = "http://data.linkedmdb.org/sparql";
	private String NYTIMES_ENDPOINT_URI = "http://155.223.24.47:8890/sparql";
	private String DOGFOOD_ENDPOINT_URI = "http://data.semanticweb.org/sparql";
	private static final String CD_QUERY5_DBPEDIA_PART = "SELECT ?film ?director WHERE { SERVICE <http://dbpedia.org/sparql>"
			+ "{?film <http://dbpedia.org/ontology/director> ?director."
			+ " ?director <http://dbpedia.org/ontology/nationality> <http://dbpedia.org/resource/Italy> . }}";

	private String firstPartQuery = "SELECT  * WHERE { "
			+ "SERVICE <http://data.linkedmdb.org/sparql> {";
	private String unionPart1 = "{ ?x <http://www.w3.org/2002/07/owl#sameAs> <";
	private String unionPart2 = "> ."
			+ " ?x <http://data.linkedmdb.org/resource/movie/genre> ?genre }";

	private String PREFIXES = "PREFIX foaf:<http://xmlns.com/foaf/0.1/> "
			+ "PREFIX owl:<http://www.w3.org/2002/07/owl#>"
			+ "PREFIX nytimes:<http://data.nytimes.com/elements/>";
	private String ask = " ASK { ";
	private String select = " SELECT DISTINCT (COUNT(?artist) AS ?rowCount) WHERE { ";
	private String firstPartASK = PREFIXES + ask;
	private String firstPartSelect = PREFIXES + select;
	private String firstTripleOfCD6 = "?artist foaf:name ?name .";
	private String secondTripleOfCD6 = "?artist foaf:based_near ?location .";
	private String thirdTripleOfCD7 = "{?y owl:sameAs ?location.}";
	private String fourthTripleOfCD7 = "{?y nytimes:topicPage ?news.}";
	private String[] endpoints = { "http://dbpedia.org/sparql",
			"http://data.linkedmdb.org/sparql",
			"http://155.223.24.47:8890/sparql",
			"http://data.semanticweb.org/sparql" };

	/**
	 * This test explores the endpoints of given triples.
	 * 
	 * @throws Exception
	 */
	@Test
	public void exploreEndpoints() throws Exception {
		logger.info("Endpoints for Cross domain query 6");
		List<String> endpointListOfFirstTripleCD6 = exploreEnpointOfTriple(firstTripleOfCD6);
		logger.info(generateLogMessage(firstTripleOfCD6,
				endpointListOfFirstTripleCD6));
		List<String> endpointListOfSecondTripleCD6 = exploreEnpointOfTriple(secondTripleOfCD6);
		logger.info(generateLogMessage(secondTripleOfCD6,
				endpointListOfSecondTripleCD6));
		logger.info("Endpoints for Cross domain query 7");
		List<String> endpointListOfThirdTripleCD7 = exploreEnpointOfTriple(thirdTripleOfCD7);
		logger.info(generateLogMessage(thirdTripleOfCD7,
				endpointListOfThirdTripleCD7));
		List<String> endpointListOfFourthTripleCD7 = exploreEnpointOfTriple(fourthTripleOfCD7);
		logger.info(generateLogMessage(fourthTripleOfCD7,
				endpointListOfFourthTripleCD7));

	}

	@Test
	public void countOfSWDogFood() throws Exception {
		System.out.println("Sorgu basliyor...");
		String query = "ASK {<http://xxx.com/xxx> ?p ?o}";
		QueryExecution queryExecution = QueryExecutionFactory.sparqlService(
				DOGFOOD_ENDPOINT_URI, query);
		boolean resultSet = queryExecution.execAsk();
		System.out.println(resultSet);
	}

	@Test
	public void getSubResultsOfCrossDomainQuery6And7() throws Exception {
		String query = firstPartSelect + SERVICE + " <" + DOGFOOD_ENDPOINT_URI
				+ ">" + "{" + firstTripleOfCD6 + secondTripleOfCD6 + "}}";

		ResultSet resultSet = executeQuery(query);
		for (int i = 0; resultSet.hasNext(); i++) {
			QuerySolution solution = resultSet.next();
			System.out.println(solution.getLiteral("rowCount").getValue());
			System.out.println(i);
		}
	}

	/**
	 * This method generates log string of given triple and endpoint list
	 * 
	 * @param triple
	 * @param endpointListOfFirstTriple
	 * @return
	 */
	private String generateLogMessage(String triple,
			List<String> endpointListOfFirstTriple) {
		return MessageFormat.format("Endpoint list of {0} triple is : {1}",
				triple, endpointListOfFirstTriple);
	}

	/**
	 * This method asks given triple to constant endpoints to find whether there
	 * exists such any.
	 * 
	 * @param triple
	 * @return
	 */
	private List<String> exploreEnpointOfTriple(String triple) {
		List<String> exploredEndpoitns = new ArrayList<String>();
		for (String endpoint : endpoints) {
			String queryStr = firstPartASK + triple + "}";
			QueryExecution queryExecution = QueryExecutionFactory
					.sparqlService(endpoint, queryStr);
			boolean askResult = queryExecution.execAsk();
			if (askResult) {
				exploredEndpoitns.add(endpoint);
			}
		}
		return exploredEndpoitns;
	}

	@Test
	public void nyTimesEnpointAvailibilityControl() throws Exception {
		String query = "ASK {?s ?p ?o.}";
		QueryExecution queryExecution = QueryExecutionFactory.sparqlService(
				"http://155.223.24.47:8891/nytimes/sparql", query);
		boolean result = queryExecution.execAsk();
		assertTrue(result);
	}

	/**
	 * This test sends an union query to linkedmdb to check whether it answers
	 * query with big union block.
	 * 
	 * @throws Exception
	 */
	@Test
	public void linkedmdbStressControl() throws Exception {
		ResultSet resultSetDBP = executeQuery(CD_QUERY5_DBPEDIA_PART);
		String unionedQuery = null;
		for (int i = 0; resultSetDBP.hasNext(); i++) {
			String filmURI = getFilmURI(resultSetDBP);
			if (i == 0) {
				unionedQuery = firstPartQuery + generateUnionPart(filmURI);
			} else {
				unionedQuery += "\n" + UNION + generateUnionPart(filmURI);
			}
		}
		unionedQuery += "\n }}";
		ResultSet resultSetLMDB = executeQuery(unionedQuery);
		while (resultSetLMDB.hasNext()) {
			QuerySolution querySolutionLMDB = (QuerySolution) resultSetLMDB
					.next();
			System.out.println(querySolutionLMDB);
		}
	}

	@Test
	public void drugbankEndpointTest() throws Exception {
		QueryExecution queryExecution = QueryExecutionFactory.sparqlService(
				"http://www4.wiwiss.fu-berlin.de/drugbank/sparql",
				"ASK {?s ?p ?o}");
		boolean result = queryExecution.execAsk();
		assertTrue(result);
	}

	@Test
	public void exploreDataset() throws Exception {
		List<VOIDIndividualOntology> readModels = new VOIDExtractor(null)
				.readFilesIntoModel(FileOperations.REFRESHED_VOIDS);
		boolean isExist = false;
		for (int i = 0; i < readModels.size(); i++) {
			VOIDIndividualOntology voidIndividualOntology = readModels.get(i);
			Individual individual = voidIndividualOntology
					.getOntModel()
					.getIndividual("http://datasets/0#indv_0.00420166959681767");
			if (individual != null) {
				System.out.println("Void number i:" + i + " VOID Uri:"
						+ individual.getURI());
				isExist = true;
			}
		}
		assertTrue(isExist);
	}

	@Test
	public void checkObjectSubjectChain() throws Exception {
		checkChainForGivenEndpoint("http://155.223.24.47:8892/chebi/sparql");
		checkChainForGivenEndpoint("http://155.223.24.47:8893/drugbank/sparql");
		checkChainForGivenEndpoint("http://155.223.24.47:8894/kegg/sparql");
	}
	
	@Test
	public void checkObjectSharing() throws Exception {
		checkObjectSharingForGivenEndpoint("http://155.223.24.47:8894/kegg/sparql");
		checkObjectSharingForGivenEndpoint("http://155.223.24.47:8892/chebi/sparql");
	}
	
	/**
	 * This method checks object sharing connectivity for given endpoint
	 * URI.
	 * 
	 * @param endpointURI
	 */
	private void checkObjectSharingForGivenEndpoint(String endpointURI) {
		String query = "PREFIX drugbank: <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/> "
				+ "PREFIX bio2rdf: <http://bio2rdf.org/ns/bio2rdf#> "
				+ "ASK { SERVICE <http://155.223.24.47:8893/drugbank/sparql>{?drug drugbank:casRegistryNumber ?cas .} "
				+ "SERVICE <" + endpointURI + ">{?s ?p ?cas .}" + "}";
		QueryExecution queryExecution = QueryExecutionFactory.create(query,
				ModelFactory.createDefaultModel());
		assertTrue(queryExecution.execAsk());
	}

	/**
	 * This method checks chain object-subject connectivity for given endpoint
	 * URI.
	 * 
	 * @param endpointURI
	 */
	private void checkChainForGivenEndpoint(String endpointURI) {
		String query = "PREFIX drugbank: <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/> "
				+ "PREFIX bio2rdf: <http://bio2rdf.org/ns/bio2rdf#> "
				+ "ASK { SERVICE <http://155.223.24.47:8893/drugbank/sparql>{?drug drugbank:casRegistryNumber ?cas .} "
				+ "SERVICE <" + endpointURI + ">{?cas ?p ?o .}" + "}";
		QueryExecution queryExecution = QueryExecutionFactory.create(query,
				ModelFactory.createDefaultModel());
		assertFalse(queryExecution.execAsk());
	}

	/**
	 * this method executes given query and returns resultset.
	 * 
	 * @param query
	 *            TODO
	 * 
	 * @return
	 */
	private ResultSet executeQuery(String query) {
		QueryExecution queryExecution = QueryExecutionFactory.create(query,
				ModelFactory.createDefaultModel());
		ResultSet resultSetDBP = queryExecution.execSelect();
		return resultSetDBP;
	}

	/**
	 * This method gets film URI from given resultset
	 * 
	 * @param resultSetDBP
	 * @return
	 */
	private String getFilmURI(ResultSet resultSetDBP) {
		QuerySolution querySolutionDBP = (QuerySolution) resultSetDBP.next();
		RDFNode filmNode = querySolutionDBP.get("film");
		String filmURI = filmNode.asResource().getURI();
		return filmURI;
	}

	private String generateUnionPart(String filmURI) {
		return "\n" + unionPart1 + filmURI + unionPart2;
	}
}
