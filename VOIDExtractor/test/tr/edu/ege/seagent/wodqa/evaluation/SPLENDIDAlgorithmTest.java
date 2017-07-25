package tr.edu.ege.seagent.wodqa.evaluation;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;

import tr.edu.ege.seagent.dataset.vocabulary.VOIDIndividualOntology;
import tr.edu.ege.seagent.dataset.vocabulary.VOIDOntologyVocabulary;
import tr.edu.ege.seagent.voidextractor.FileOperations;
import tr.edu.ege.seagent.voidextractor.Queries;
import tr.edu.ege.seagent.voidextractor.VOIDExtractor;
import tr.edu.ege.seagent.voidextractor.thread.EndpointTimeoutThread;
import tr.edu.ege.seagent.wodqa.QueryElementOperations;
import tr.edu.ege.seagent.wodqa.QueryVocabulary;
import tr.edu.ege.seagent.wodqa.query.analyzer.QueryAnalyzer;
import tr.edu.ege.seagent.wodqa.voiddocument.VoidConceptOperations;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;

/**
 * This test executes SPLENDID algorithm to analyze relevant datasets for
 * FedBench queries. This test is part of analyze time comparison for WoDQA.
 * 
 * @author etmen
 * 
 */
public class SPLENDIDAlgorithmTest {

	/**
	 * List of VOID descriptions which will be analyzed with SPLENDID algorithm.
	 */
	private List<OntModel> allVoidModels;

	@Before
	public void before() throws Exception {
		List<VOIDIndividualOntology> readVOIDOnts = new VOIDExtractor(null)
				.readFilesIntoModel(FileOperations.CLEANSED_9_VOIDS);
		allVoidModels = new ArrayList<OntModel>();
		for (VOIDIndividualOntology indvOnt : readVOIDOnts) {
			allVoidModels.add(indvOnt.getOntModel());
		}
	}

	@Test
	public void analyzeBySPLENDIDTest() throws Exception {
		List<List<OntModel>> ontmodels = new Vector<List<OntModel>>();
		// get triple patterns
		QueryAnalyzer analyzer = new QueryAnalyzer((Model) null);
		List<Triple> tripleList = analyzer
				.fillTriplePatternList(Queries.LIFE_SCIENCES_QUERY_7);
		long now = System.currentTimeMillis();
		// SPLENDID algorithm
		for (int i = 0; i < tripleList.size(); i++) {
			Triple triple = tripleList.get(i);
			List<OntModel> tempModelList = new Vector<OntModel>();

			System.out.println("Executing triple: \"" + triple + "\"");

			Node predicate = triple.getPredicate();
			Node object = triple.getObject();
			Node subject = triple.getSubject();

			if (predicate.isVariable()) {
				tempModelList.addAll(allVoidModels);
			} else {
				if (predicate.getURI().equals(
						"http://www.w3.org/1999/02/22-rdf-syntax-ns#type")
						&& object.isURI()) {
					tempModelList = executeVocabularyMatchRule(object,
							allVoidModels);
				} else {
					tempModelList = executeVocabularyMatchRule(predicate,
							allVoidModels);
				}
			}

			// check emptyness
			if (tempModelList.size() == 0)
				tempModelList.addAll(allVoidModels);
			// ASK queries
			if (predicate.isVariable() || !subject.isVariable()
					|| !object.isVariable()) {
				Query query = QueryFactory.create("ASK {"
						+ QueryElementOperations.convertTripleToString(triple)
						+ "}");
				List<EndpointTimeoutThread> threadList = new Vector<EndpointTimeoutThread>();
				int askCount = 0;
				System.out.println("Eliminated Model size: "
						+ tempModelList.size());
				for (OntModel ontmodel : tempModelList) {
					String endpoint = ontmodel
							.listStatements(
									null,
									VOIDOntologyVocabulary.DATASET_sparqlEndpoint_prp,
									(Literal) null).toList().get(0).getObject()
							.as(Literal.class).getString().toString();
					QueryExecution exec = QueryExecutionFactory.sparqlService(
							endpoint, query);
					EndpointTimeoutThread askthread = new EndpointTimeoutThread(
							exec);
					askCount++;
					threadList.add(askthread);
					askthread.start();

				}
				System.out.println("ASK count:" + askCount + " for triple: \""
						+ triple + "\"");
				List<OntModel> newTempList = new Vector<OntModel>();
				// execute ask queries
				for (int j = 0; j < threadList.size(); j++) {
					EndpointTimeoutThread endpointTimeoutThread = threadList
							.get(j);
					while (endpointTimeoutThread.isAlive()) {
					}
					if (endpointTimeoutThread.getAskResult() == 1)
						newTempList.add(tempModelList.get(j));
				}
				tempModelList = newTempList;
			}
			ontmodels.add(i, tempModelList);
		}
		System.out.println("Total time: " + (System.currentTimeMillis() - now));
		for (List<OntModel> ontModelList : ontmodels) {
			System.out.println(ontModelList.size());
		}
	}

	/**
	 * Executes vocabulary match rule and returns the relevant void models that
	 * include vocabulary used by the triple pattern.
	 * 
	 * @param vocabularyNode
	 *            vocabulary node. It can be predicate or object when the
	 *            predicate is rdf:type.
	 * @return
	 */
	public List<OntModel> executeVocabularyMatchRule(Node vocabularyNode,
			List<OntModel> voidModels) {
		String searchedVoc = VoidConceptOperations.getGraphURI(vocabularyNode
				.toString());
		String query = QueryVocabulary.VOID_PREFIX_URI
				+ QueryVocabulary.RDF_PREFIX_URI
				+ "SELECT ?dataset WHERE {?dataset rdf:type void:Dataset. ?dataset void:vocabulary \""
				+ searchedVoc + "\"}";
		List<OntModel> relatedModels = new Vector<OntModel>();
		// get vocabulary and datasets from ontmodel.
		for (OntModel voidModel : voidModels) {
			// execute query on void model...
			QueryExecution execution = QueryExecutionFactory.create(query,
					voidModel);
			ResultSet set = execution.execSelect();
			if (set.hasNext()) {
				relatedModels.add(voidModel);
			}
			execution.close();
		}
		return relatedModels;
	}

}
