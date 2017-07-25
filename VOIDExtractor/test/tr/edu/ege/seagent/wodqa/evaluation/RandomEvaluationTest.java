package tr.edu.ege.seagent.wodqa.evaluation;

import java.util.List;

import com.hp.hpl.jena.rdf.model.RDFNode;

public class RandomEvaluationTest extends AbstractEvaluationTest {
	private static final String baseOntURI = "http://randomOntURI.org/";
	private static List<String> dbpediaVocList;
	private static List<String> linkedMDBVocList;
	private static List<RDFNode> dbpediaUrispaceList;
	private static List<RDFNode> linkedMDBUrispaceList;

//	@BeforeClass
//	public static void beforeClass() {
//		executor = new QueryExecutor();
//		dbpediaVocList = RandomVOIDProducer.createVocabularyList(
//				ExampleVocabulary.DBPEDIA_PROPERTY_PROP,
//				ExampleVocabulary.DBPEDIA_ONTOLOGY_PROP);
//		linkedMDBVocList = RandomVOIDProducer
//				.createVocabularyList(ExampleVocabulary.LINKED_MDB_VOC);
//		dbpediaUrispaceList = RandomVOIDProducer
//				.createUrispaces(ExampleVocabulary.DBPEDIA_URISPACE);
//		linkedMDBUrispaceList = RandomVOIDProducer
//				.createUrispaces(ExampleVocabulary.LINKED_MDB_URI_SPACE);
//		allVoidModels = new Vector<OntModel>();
//	}
//
//	/**
//	 * 500 VOID'de 250 vocabulary ve urispace bazında ilişkili : 32,101
//	 * 
//	 * 
//	 * 1000 VOID'de 500 vocabulary ve urispace bazında ilişkili : 123,004
//	 * 
//	 * 
//	 * 2000 VOID'de 1000 vocabulary ve urispace bazında ilişkili : 477006
//	 * 
//	 * 
//	 * @throws Exception
//	 */
//	@Test
//	public void createRandomVOIDsWithBasicPropertiesTest() throws Exception {
//		executeWithVOIDs(2000, 2);
//		System.out.println(executeQuery(CROSS_DOMAIN_QUERY_1, false, 1));
//	}
//
//	/**
//	 * 500 VOID'de 250 ilişkili + 1 linkset : 33593
//	 * 
//	 * 500 VOID'de 250 ilişkili + 30 linkset : 32694
//	 * 
//	 * 1000 VOID'de 500 ilişkili + 1 linkset : 121222
//	 * 
//	 * @throws Exception
//	 */
//	@Test
//	public void createRandomVOIDsWithLinksetsTest() throws Exception {
//		executeWithVOIDs(500, 2);
//		// add one linkset...
//		createLinsketBetweenVOIDs(300, 0);
//		createLinsketBetweenVOIDs(301, 1);
//		createLinsketBetweenVOIDs(302, 2);
//		createLinsketBetweenVOIDs(303, 3);
//		createLinsketBetweenVOIDs(304, 4);
//		createLinsketBetweenVOIDs(305, 5);
//		createLinsketBetweenVOIDs(306, 6);
//		createLinsketBetweenVOIDs(307, 7);
//		createLinsketBetweenVOIDs(308, 8);
//		createLinsketBetweenVOIDs(309, 9);
//		createLinsketBetweenVOIDs(310, 10);
//		createLinsketBetweenVOIDs(311, 11);
//		createLinsketBetweenVOIDs(312, 12);
//		createLinsketBetweenVOIDs(313, 13);
//		createLinsketBetweenVOIDs(314, 14);
//		createLinsketBetweenVOIDs(315, 15);
//		createLinsketBetweenVOIDs(316, 16);
//		createLinsketBetweenVOIDs(317, 17);
//		createLinsketBetweenVOIDs(318, 18);
//		createLinsketBetweenVOIDs(319, 19);
//		createLinsketBetweenVOIDs(320, 20);
//		createLinsketBetweenVOIDs(321, 21);
//		createLinsketBetweenVOIDs(322, 22);
//		createLinsketBetweenVOIDs(323, 23);
//		createLinsketBetweenVOIDs(324, 24);
//		createLinsketBetweenVOIDs(325, 25);
//		createLinsketBetweenVOIDs(326, 26);
//		createLinsketBetweenVOIDs(327, 27);
//		createLinsketBetweenVOIDs(328, 28);
//		createLinsketBetweenVOIDs(329, 29);
//		// execute...
//		System.out.println(executeQuery(CROSS_DOMAIN_QUERY_1, false, 1));
//	}
//
//	private void createLinsketBetweenVOIDs(int sourceIndex, int destinationIndex) {
//		List<LinksetProperties> linksetPropList = RandomVOIDProducer
//				.createLinksetPropertiesList(RandomVOIDProducer
//						.createLinksetProperty(allVoidModels.get(sourceIndex),
//								allVoidModels.get(destinationIndex),
//								ExampleVocabulary.OWL_SAMEAS_RSC));
//		RandomVOIDProducer.addLinksetsToVOID(linksetPropList);
//	}
//
//	private void executeWithVOIDs(int voidCount, int rate)
//			throws MalformedURLException {
//		for (int i = 0; i < voidCount; i++) {
//			OntModel voidModel;
//			if (i < voidCount / rate)
//				voidModel = RandomVOIDProducer.createRandomVOIDModel(
//						dbpediaVocList, dbpediaUrispaceList, baseOntURI + i);
//			else
//				voidModel = RandomVOIDProducer
//						.createRandomVOIDModel(linkedMDBVocList,
//								linkedMDBUrispaceList, baseOntURI + i);
//			allVoidModels.add(voidModel);
//		}
//	}
}
