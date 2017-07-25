package tr.edu.ege.seagent.wodqa.evaluation;

import static org.junit.Assert.assertFalse;

import java.net.MalformedURLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import tr.edu.ege.seagent.dataset.vocabulary.VOIDIndividualOntology;
import tr.edu.ege.seagent.voidextractor.FileOperations;
import tr.edu.ege.seagent.voidextractor.VOIDExtractor;
import tr.edu.ege.seagent.wodqa.QueryVocabulary;

import com.hp.hpl.jena.query.ARQ;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.OpAsQuery;
import com.hp.hpl.jena.sparql.core.DatasetGraphFactory;
import com.hp.hpl.jena.sparql.util.Context;

public class VOIDAnalysisTest {

	private static Model mainModel;

	private static Logger logger;

	@BeforeClass
	public static void beforeClass() throws Exception {
		logger = Logger.getLogger(VOIDAnalysisTest.class);
		constructVOIDSpaceModel();
	}

	private static void constructVOIDSpaceModel() throws MalformedURLException {
		List<VOIDIndividualOntology> readModels = new VOIDExtractor(null)
				.readFilesIntoModel(FileOperations.COMPLETE_VOIDS);
		mainModel = ModelFactory.createDefaultModel();
		for (VOIDIndividualOntology voidIndividualOntology : readModels) {
			mainModel.add(voidIndividualOntology.getOntModel());
		}
	}

	@Test
	public void removeNonReachableLinksets() throws Exception {

		// retrieve non appropriate linkset
		List<Resource> nonReachableLinksets = getNonAppropriateLinksets();
		for (Resource linkset : nonReachableLinksets) {
			mainModel.removeAll(linkset, null, (RDFNode) null);
			assertFalse(mainModel.contains(linkset, null, (RDFNode) null));
		}
		// assertTrue(mainModel
		// .contains(
		// ResourceFactory
		// .createResource("http://datasets/dbpedia#indv_0.9145900649750467"),
		// null, (RDFNode) null));

	}

	/**
	 * This method gets non appropriate linksets and datasets that holds exluded
	 * datasets as their objectsTarget.
	 * 
	 * @return
	 */
	private List<Resource> getNonAppropriateLinksets() {
		List<Resource> linksets = new ArrayList<Resource>();
		String query = QueryVocabulary.RDF_PREFIX_URI
				+ QueryVocabulary.VOID_PREFIX_URI
				+ "SELECT ?linkset ?dataset WHERE{"
				+ "?linkset void:objectsTarget ?dataset."
				+ "FILTER NOT EXISTS{?dataset rdf:type void:Dataset.}}";
		QueryExecution queryExecution = QueryExecutionFactory.create(query,
				mainModel);
		ResultSet resultSet = queryExecution.execSelect();
		while (resultSet.hasNext()) {
			linksets.add(resultSet.next().getResource("linkset"));
		}
		return linksets;
	}

	@Test
	public void listAllLinksets() throws Exception {
		String query = QueryVocabulary.RDF_PREFIX_URI
				+ QueryVocabulary.VOID_PREFIX_URI
				+ "SELECT (COUNT(DISTINCT ?linkset) AS ?count) WHERE { "
				+ "?referrerDataset rdf:type void:Dataset."
				+ "?linkset void:subjectsTarget ?referrerDataset. "
				+ "?linkset void:linkPredicate ?predicate."
				+ "?linkset void:objectsTarget ?referencedDataset. " + "}";
		QueryExecution queryExecution = QueryExecutionFactory.create(query,
				mainModel);
		ResultSet resultSet = queryExecution.execSelect();
		logger.info(MessageFormat.format("All linkset count: {0}", resultSet
				.next().get("count").asLiteral().getInt()));
	}

	@Test
	public void listAllVirtualLinksets() throws Exception {
		String query = QueryVocabulary.RDF_PREFIX_URI
				+ QueryVocabulary.VOID_PREFIX_URI
				+ "SELECT (COUNT(*) AS ?count) WHERE { "
				+ "?referrerDataset rdf:type void:Dataset."
				+ "?referrerDataset void:sparqlEndpoint ?endpoint1. "
				+ "?linkset void:subjectsTarget ?referrerDataset. "
				+ "?linkset void:linkPredicate ?predicate."
				+ "?linkset void:objectsTarget ?referencedDataset. "
				+ "FILTER NOT EXISTS {?referencedDataset void:sparqlEndpoint ?endpoint2.} "
				+ "?referencedDataset rdf:type void:Dataset. " + "}";
		QueryExecution queryExecution = QueryExecutionFactory.create(query,
				mainModel);
		ResultSet resultSet = queryExecution.execSelect();
		logger.info(MessageFormat.format("All virtual linkset count: {0}",
				resultSet.next().get("count").asLiteral().getInt()));
	}

	@Test
	public void listAllVirtualLinksetsWithUriSpaces() throws Exception {
		// String query = QueryVocabulary.RDF_PREFIX_URI
		// + QueryVocabulary.VOID_PREFIX_URI
		// + "SELECT (COUNT(DISTINCT ?referrerDataset) AS ?count) WHERE { "
		// + "?referrerDataset rdf:type void:Dataset."
		// +
		// " FILTER (?referrerDataset IN(<http://datasets/geonames#indv_0.32581606535856833>, "
		// +
		// "<http://datasets/0#indv_0.925227985707652>, <http://datasets/geodata#indv_0.4950269197915621>, "
		// +
		// "<http://datasets/0#indv_0.5966600923294868>, <http://datasets/0#indv_0.7403931421308076>, "
		// +
		// "<http://datasets/0#indv_0.7154469541987871>, <http://datasets/0#indv_0.4246927590455861>, "
		// +
		// "<http://datasets/linkedMdb#indv_0.7447588411027833>, <http://datasets/0#indv_0.992789130503645>, "
		// + "<http://datasets/0#indv_0.45234945970298823>)). "
		// + "?linkset void:subjectsTarget ?referrerDataset. "
		// +
		// "?linkset void:linkPredicate <http://www.w3.org/2002/07/owl#sameAs>."
		// + "?linkset void:objectsTarget ?referencedDataset. "
		// +
		// "FILTER NOT EXISTS {?referencedDataset void:sparqlEndpoint ?endpoint.} "
		// + "?referencedDataset void:uriSpace ?uriSpace. " + "}";

		String query2 = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix void: <http://rdfs.org/ns/void#> "
				+ "SELECT DISTINCT ?referrerDataset1 ?referrerDataset2 WHERE {"
				+ " ?referrerDataset1 rdf:type void:Dataset.  "
				+ "VALUES ?referrerDataset1 { "
				+ "<http://datasets/geonames#indv_0.32581606535856833> "
				+ "<http://datasets/0#indv_0.925227985707652> <http://datasets/geodata#indv_0.4950269197915621> "
				+ "<http://datasets/0#indv_0.5966600923294868> <http://datasets/0#indv_0.7403931421308076> "
				+ "<http://datasets/0#indv_0.7154469541987871> <http://datasets/0#indv_0.4246927590455861> "
				+ "<http://datasets/linkedMdb#indv_0.7447588411027833> "
				+ " <http://datasets/0#indv_0.992789130503645> <http://datasets/0#indv_0.45234945970298823>"
				+ "}.  "
				+ "?linkset1 void:subjectsTarget ?referrerDataset1. "
				+ "?linkset1 void:linkPredicate <http://www.w3.org/2002/07/owl#sameAs>. "
				+ "{ ?linkset1 void:objectsTarget ?referencedDataset1. "
				+ "FILTER NOT EXISTS {?referencedDataset1 void:sparqlEndpoint ?endpoint1.} } "
				+ "?referencedDataset1 void:uriSpace ?uriSpace. "
				+ "{?referencedDataset2 void:uriSpace ?uriSpace. "
				+ "FILTER NOT EXISTS {?referencedDataset2 void:sparqlEndpoint ?endpoint2.}} "
				+ "?referrerDataset2 rdf:type void:Dataset. "
				+ "VALUES ?referrerDataset2 { <http://datasets/linkedMdb#indv_0.7447588411027833>}. "
				+ "?linkset2 void:subjectsTarget ?referrerDataset2. "
				+ "?linkset2 void:objectsTarget ?referencedDataset2. "
				+ "?linkset2 void:linkPredicate <http://www.w3.org/2002/07/owl#sameAs>. "
				+ "}";

		Context context = new Context();
		ARQ.setStrictMode(context);
		context.set(ARQ.optFilterPlacement, false);

		Query createdQuery = QueryFactory.create(query2);
		Op op = QueryExecutionFactory.createPlan(createdQuery,
				DatasetGraphFactory.createMem(), null, context).getOp();
		Query query = OpAsQuery.asQuery(op);
		System.out.println(query);

		QueryExecution queryExecution = QueryExecutionFactory.create(query2,
				mainModel);

		long startTime = System.currentTimeMillis();

		ResultSet resultSet = queryExecution.execSelect();
		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			Resource referrerDataset1 = querySolution
					.getResource("referrerDataset1");
			Resource referrerDataset2 = querySolution
					.getResource("referrerDataset2");
			System.out.println(referrerDataset1 + "-" + referrerDataset2);
		}

		// ResultSet resultSet = queryExecution.execSelect();
		// // System.out.println(ResultSetFormatter.asText(resultSet));
		// // logger.info(MessageFormat.format(
		// // "All virtual linksets have uriSpace count: {0}", resultSet
		// // .next().get("count").asLiteral().getInt()));
		// logger.info(MessageFormat.format("Result row count: {0}", resultSet
		// .next().get("count").asLiteral().getInt()));
		System.out.println((System.currentTimeMillis() - startTime));
	}

	@Test
	public void listAllDatasetsWithUriSpaces() throws Exception {
		String query = QueryVocabulary.RDF_PREFIX_URI
				+ QueryVocabulary.VOID_PREFIX_URI
				+ "SELECT (COUNT( DISTINCT ?dataset) AS ?count) WHERE { "
				+ "?dataset rdf:type void:Dataset."
				+ "?dataset void:uriSpace ?uriSpace. " + "}";
		QueryExecution queryExecution = QueryExecutionFactory.create(query,
				mainModel);
		ResultSet resultSet = queryExecution.execSelect();
		logger.info(MessageFormat.format(
				"All dataset with uri space count: {0}",
				resultSet.next().get("count").asLiteral().getInt()));
	}

	@Test
	public void listAllLinksetsWithOWLSameAs() throws Exception {
		String query = QueryVocabulary.RDF_PREFIX_URI
				+ QueryVocabulary.VOID_PREFIX_URI
				+ "SELECT (COUNT(DISTINCT ?linkset) AS ?count) WHERE { "
				+ "?referrerDataset rdf:type void:Dataset."
				+ "?linkset void:subjectsTarget ?referrerDataset. "
				+ "?linkset void:linkPredicate <http://www.w3.org/2002/07/owl#sameAs>. "
				+ "?linkset void:objectsTarget ?referencedDataset. " + "}";
		QueryExecution queryExecution = QueryExecutionFactory.create(query,
				mainModel);
		ResultSet resultSet = queryExecution.execSelect();
		logger.info(MessageFormat.format("All owl:sameAs linkset count: {0}",
				resultSet.next().get("count").asLiteral().getInt()));
	}

	@Test
	public void listVirtualLinksetsWithOWLSameAs() throws Exception {
		String query = QueryVocabulary.RDF_PREFIX_URI
				+ QueryVocabulary.VOID_PREFIX_URI
				+ "SELECT (COUNT(DISTINCT ?linkset) AS ?count) WHERE { "
				+ "?referrerDataset rdf:type void:Dataset."
				+ "?linkset void:subjectsTarget ?referrerDataset. "
				+ "?linkset void:linkPredicate <http://www.w3.org/2002/07/owl#sameAs>. "
				+ "?linkset void:objectsTarget ?referencedDataset. "
				+ "FILTER NOT EXISTS {?referencedDataset void:sparqlEndpoint ?endpoint.} "
				+ "}";
		QueryExecution queryExecution = QueryExecutionFactory.create(query,
				mainModel);
		ResultSet resultSet = queryExecution.execSelect();
		logger.info(MessageFormat.format(
				"Virtual owl:sameAs linkset count: {0}",
				resultSet.next().get("count").asLiteral().getInt()));
	}

	@Test
	public void listAllLinksetsWithCD4RelevantDatasets() throws Exception {
		String query = QueryVocabulary.RDF_PREFIX_URI
				+ QueryVocabulary.VOID_PREFIX_URI
				+ "SELECT (COUNT(DISTINCT ?linkset) AS ?count) WHERE { "
				+ "?referrerDataset rdf:type void:Dataset."
				+ " FILTER (?referrerDataset IN(<http://datasets/geonames#indv_0.32581606535856833>, "
				+ "<http://datasets/0#indv_0.925227985707652>, <http://datasets/geodata#indv_0.4950269197915621>, "
				+ "<http://datasets/0#indv_0.5966600923294868>, <http://datasets/0#indv_0.7403931421308076>, "
				+ "<http://datasets/0#indv_0.7154469541987871>, <http://datasets/0#indv_0.4246927590455861>, "
				+ "<http://datasets/linkedMdb#indv_0.7447588411027833>, <http://datasets/0#indv_0.992789130503645>, "
				+ "<http://datasets/0#indv_0.45234945970298823>)). "
				+ "?linkset void:subjectsTarget ?referrerDataset. "
				+ "?linkset void:linkPredicate <http://www.w3.org/2002/07/owl#sameAs>. "
				+ "?linkset void:objectsTarget ?referencedDataset. " + "}";
		QueryExecution queryExecution = QueryExecutionFactory.create(query,
				mainModel);
		ResultSet resultSet = queryExecution.execSelect();
		logger.info(MessageFormat
				.format("All owl:sameAs linkset count with related dataset of \"?actor @owl:sameAs ?x\" triple: {0}",
						resultSet.next().get("count").asLiteral().getInt()));
	}

	@Test
	public void listAllVirtualLinksetsWithCD4RelevantDatasets()
			throws Exception {
		String query = QueryVocabulary.RDF_PREFIX_URI
				+ QueryVocabulary.VOID_PREFIX_URI
				+ "SELECT DISTINCT (COUNT(DISTINCT ?linkset) AS ?count) WHERE { "
				+ "?referrerDataset rdf:type void:Dataset."
				+ " FILTER (?referrerDataset IN(<http://datasets/geonames#indv_0.32581606535856833>, "
				+ "<http://datasets/0#indv_0.925227985707652>, <http://datasets/geodata#indv_0.4950269197915621>, "
				+ "<http://datasets/0#indv_0.5966600923294868>, <http://datasets/0#indv_0.7403931421308076>, "
				+ "<http://datasets/0#indv_0.7154469541987871>, <http://datasets/0#indv_0.4246927590455861>, "
				+ "<http://datasets/linkedMdb#indv_0.7447588411027833>, <http://datasets/0#indv_0.992789130503645>, "
				+ "<http://datasets/0#indv_0.45234945970298823>)). "
				+ "?linkset void:subjectsTarget ?referrerDataset. "
				+ "?linkset void:linkPredicate <http://www.w3.org/2002/07/owl#sameAs>. "
				+ "?linkset void:objectsTarget ?referencedDataset. "
				+ "FILTER NOT EXISTS {?referencedDataset void:sparqlEndpoint ?endpoint.} "
				+ "}";
		QueryExecution queryExecution = QueryExecutionFactory.create(query,
				mainModel);
		ResultSet resultSet = queryExecution.execSelect();
		logger.info(MessageFormat
				.format("Virtual owl:sameAs linkset count with related dataset of \"?actor @owl:sameAs ?x\" triple: {0}",
						resultSet.next().get("count").asLiteral().getInt()));
	}

}
