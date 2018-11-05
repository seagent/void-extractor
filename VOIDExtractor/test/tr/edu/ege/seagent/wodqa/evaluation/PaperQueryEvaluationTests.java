package tr.edu.ege.seagent.wodqa.evaluation;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;

import tr.edu.ege.seagent.voidextractor.FileOperations;
import tr.edu.ege.seagent.voidextractor.Queries;
import tr.edu.ege.seagent.voidextractor.VOIDExtractor;
import tr.edu.ege.seagent.wodqa.voiddocument.VOIDIndividualOntology;

public class PaperQueryEvaluationTests extends AbstractEvaluationTest {

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
	public void listAllEndpointsTest() throws Exception {
		for (OntModel ontModel : voidList) {
			Query query = QueryFactory
					.create("SELECT ?endpoint WHERE {?s <http://rdfs.org/ns/void#sparqlEndpoint> ?endpoint}");
			QueryExecution execution = QueryExecutionFactory.create(query,
					ontModel);
			String string = execution.execSelect().next()
					.getLiteral("endpoint").getString().toString();
			System.out.println(string);
		}
	}

	/**
	 * QUERY TIMES: 28456,46317, 30525, 26464, 22217, 26769, 28712, 31065,
	 * 27688, 29379 AVG=29759
	 * 
	 * @throws Exception
	 */
	@Test
	public void analyzeNearestAirportsQueryOverNewVoids() throws Exception {
		executeQuery(Queries.NEAREST_AIRPORTS_QUERY, "NEAREST AIRPORTS QUERY",
				false, 10);
	}

	/**
	 * QUERY TIMES: 816, 760, 752, 745, 734, 731, 720, 1992, 2792 AVG=1116
	 * 
	 * @throws Exception
	 */
	@Test
	public void getNikolaTeslaTest() throws Exception {
		executeQuery(Queries.VOCABULARY_MATCH_RULE_EXAMPLE_QUERY,
				"VOCABULARY MATCH RULE EXAMPLE QUERY", true, 10);
	}

	/**
	 * QUERY TIMES: 876, 528, 3249, 4451, 294, 304, 294, 277, 262, 1576 AVG=1211
	 * 
	 * @throws Exception
	 */
	@Test
	public void getAllProducersTest() throws Exception {
		executeQuery(Queries.RDF_TYPE_MATCH_RULE_EXAMPLE_QUERY,
				"RDF TYPE MATCH RULE EXAMPLE QUERY", true, 10);
	}

	/**
	 * QUERY TIMES: 4331, 5671, 3221, 3199, 7463, 3198, 3357, 4877, 3116, 3072
	 * AVG=4150
	 * 
	 * @throws Exception
	 */
	@Test
	public void linkingToURITest() throws Exception {
		executeQuery(Queries.LINKING_TO_URI_RULE_EXAMPLE_QUERY,
				"LINKING TO URI RULE EXAMPLE QUERY", true, 10);
	}

	/**
	 * QUERY TIMES: 5668, 3082, 980, 892, 2691, 2491, 4914, 966, 913, 910
	 * AVG=2351
	 * 
	 * @throws Exception
	 */
	@Test
	public void IRILinksToTest() throws Exception {
		executeQuery(Queries.IRI_LINKS_TO_RULE_EXAMPLE_QUERY,
				"IRI LINKS TO RULE EXAMPLE QUERY", true, 10);
	}

	/**
	 * QUERY TIMES: 2680, 954, 904, 837, 836, 1799, 1210, 1865, 890, 948
	 * AVG=1292
	 * 
	 * @throws Exception
	 */
	@Test
	public void chainingRuleExampleTest() throws Exception {
		executeQuery(Queries.CHAINING_RULE_EXAMPLE_QUERY,
				"CHAINING RULE EXAMPLE QUERY", true, 10);
	}

	/**
	 * QUERY TIMES: 3949, 3355, 5894, 2945, 2869, 5924, 2772, 7338, 2720,3472
	 * AVG=4124
	 * 
	 * @throws Exception
	 */
	@Test
	public void objectSharingRuleExampleTest() throws Exception {
		executeQuery(Queries.OBJECT_SHARING_RULE_EXAMPLE_QUERY,
				"OBJECT SHARING RULE EXAMPLE QUERY", true, 10);
	}

	/**
	 * QUERY TIMES: 7706, 5694, 3303, 1649, 4715, 1690, 1641, 4168, 1602, 2526
	 * AVG=3469
	 * 
	 * @throws Exception
	 */
	@Test
	public void subjectSharingRuleExampleTest() throws Exception {
		executeQuery(Queries.SUBJECT_SHARING_RULE_EXAMPLE_QUERY,
				"SUBJECT SHARING RULE EXAMPLE QUERY", true, 10);
	}

}
