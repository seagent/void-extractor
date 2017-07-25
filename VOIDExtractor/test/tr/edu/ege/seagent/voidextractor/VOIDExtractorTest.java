package tr.edu.ege.seagent.voidextractor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import tr.edu.ege.seagent.dataset.vocabulary.VOIDIndividualOntology;
import tr.edu.ege.seagent.dataset.vocabulary.VOIDOntologyVocabulary;
import tr.edu.ege.seagent.voidextractor.exception.NoDatasetException;
import tr.edu.ege.seagent.voidextractor.thread.EndpointTimeoutThread;
import tr.edu.ege.seagent.wodqa.QueryVocabulary;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;

public class VOIDExtractorTest {

	public VOIDExtractor extractor;
	private static final String A_URISPACE = "http://aUrispaces.org/";
	private static final String B_URISPACE = "http://bUrispaces.org/";
	private static final String C_URISPACE = "http://cUrispaces.org/";
	private static final String D_URISPACE = "http://dUrispaces.org/";

	private static Logger logger = Logger.getLogger(VOIDExtractor.class);

	/**
	 * It gets the triple counts
	 * 
	 * @param dsiLod
	 * @param bigEndpoints
	 * @return
	 * @throws Exception
	 */
	private static List<Long> getTripleCounts(String dsiLod,
			List<String> bigEndpoints) throws Exception {
		// String queryForLOD =
		// "prefix void: <http://rdfs.org/ns/void#> SELECT * WHERE {?s void:sparqlEndpoint <"
		// + endpoint + ">. ?s void:triples ?size}";
		List<Long> sizeList = new Vector<Long>();
		for (int i = 0; i < bigEndpoints.size(); i++) {
			String endpoint = bigEndpoints.get(i);
			String queryForLOD = "SELECT (COUNT(*) AS ?size) WHERE {?s ?p ?o}";
			int tryingCount = 0;
			Query query = QueryFactory.create(queryForLOD);
			QueryExecution execution = null;
			while (tryingCount <= 3) {
				try {
					execution = QueryExecutionFactory.sparqlService(endpoint,
							query);
					ResultSet set = execution.execSelect();
					sizeList.add(set.next().getLiteral("size").getLong());
					execution.close();
					break;
				} catch (Exception e) {
					if (tryingCount > 2) {
						bigEndpoints.remove(i);
						i--;
						execution.close();
						break;
					}
					tryingCount++;
					execution.close();
					e.printStackTrace();
				}
			}
		}
		return sizeList;
	}

	/**
	 * It reads endpoints from the given text file.
	 * 
	 * @param filePath
	 * @throws IOException
	 */
	private static List<String> readEndpointsFromTextFile(String filePath,
			String character) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(new File(
				filePath)));
		List<String> endpointList = new Vector<String>();
		String str;
		while ((str = reader.readLine()) != null) {
			if (character != null)
				endpointList.add(str.substring(0, str.indexOf(character)));
			else
				endpointList.add(str);
		}
		return endpointList;
	}

	@Before
	public void before() {
		extractor = new VOIDExtractor(null);

	}

	@Test
	public void extractBigDataset() throws Exception {
		// String endpoint = "http://ft.rkbexplorer.com/sparql/";
		String endpoint = "http://dbpedia.org/sparql";
		List<String> endpointList = new Vector<String>();
		endpointList.add(endpoint);
		// execute...
		extractor.extractBasicPropertiesOfDatasets(endpointList, false,
				FileOperations.VOID_DIRECTORY_NAME);
		// assert
		assertEquals(1, extractor.getBigDatasetList().size());
		assertNull(extractor.getEndpointList());
	}

	@Test
	public void checkDatasetAvailability() throws Exception {
		List<String> endpointList = getEndpointsOfDatasets(OnlineVoidStores.MONDECA);
		extractor.setEndpointList(endpointList);
		List<String> validEndpointList = extractor
				.checkAvailabilityOfEndpoints(endpointList);
		System.out.println("SIZE : " + validEndpointList.size());
		assertTrue(validEndpointList.size() > 20);
	}

	@Test
	public void createVOIDWithBasicProperties() throws Exception {
		String endpoint = "http://ft.rkbexplorer.com/sparql/";
		List<String> endpointList = new Vector<String>();
		endpointList.add(endpoint);
		extractor.setEndpointList(endpointList);
		extractor.extractBasicPropertiesOfDatasets(endpointList, false,
				FileOperations.VOID_DIRECTORY_NAME);
		OntModel voidModel = extractor.getVoidIndvOntList().get(0)
				.getOntModel();
		voidModel.write(System.out);
		List<Statement> vocs = voidModel.listStatements(null,
				Vocabulary.VOID_VOCABULARY_PROPERTY, (RDFNode) null).toList();
		assertEquals(8, vocs.size());
		List<Statement> urispaces = voidModel.listStatements(null,
				Vocabulary.VOID_URISPACE_PROPERTY, (RDFNode) null).toList();
		assertEquals(17, urispaces.size());
	}

	@Test
	public void createVOIDWithLinksetProperties() throws Exception {
		List<String> endpointList = new Vector<String>();
		endpointList.add("http://a.org/sparql");
		endpointList.add("http://b.org/sparql");
		endpointList.add("http://c.org/sparql");
		endpointList.add("http://d.org/sparql");
		extractor.setEndpointList(endpointList);

		// set urispaces of datasets
		extractor.getUrispacesList().add(createUrispaceList(A_URISPACE));
		extractor.getUrispacesList().add(createUrispaceList(B_URISPACE));
		extractor.getUrispacesList().add(createUrispaceList(C_URISPACE));
		extractor.getUrispacesList().add(createUrispaceList(D_URISPACE));
		// set linksetsList
		extractor.getLinksetsList().add(
				createLinksetItems(A_URISPACE + "**http://owlSameAs.org**"
						+ B_URISPACE, A_URISPACE + "**http://owlSameAs.org**"
						+ C_URISPACE));
		extractor.getLinksetsList().add(
				createLinksetItems(B_URISPACE + "**http://owlSameAs.org**"
						+ C_URISPACE));
		extractor.getLinksetsList().add(
				createLinksetItems(C_URISPACE + "**http://owlSameAs.org**"
						+ B_URISPACE));

		extractor.getVoidIndvOntList().add(
				extractor.createVOID(0, endpointList,
						FileOperations.VOID_DIRECTORY_NAME, 0));
		extractor.getVoidIndvOntList().add(
				extractor.createVOID(1, endpointList,
						FileOperations.VOID_DIRECTORY_NAME, 1));
		extractor.getVoidIndvOntList().add(
				extractor.createVOID(2, endpointList,
						FileOperations.VOID_DIRECTORY_NAME, 2));
		extractor.getVoidIndvOntList().add(
				extractor.createVOID(3, endpointList,
						FileOperations.VOID_DIRECTORY_NAME, 3));

		// assertVoid they must be empty...s
		List<VOIDIndividualOntology> voidModels = extractor
				.getVoidIndvOntList();
		assertTrue(voidModels.get(0).getOntModel()
				.listIndividuals(VOIDOntologyVocabulary.LINKSET_rsc).toList()
				.size() < 1);
		// EXTRACT LINKSETS...
		extractor.extractLinksetDescriptions(extractor.getLinksetsList(),
				extractor.getVoidIndvOntList(),
				FileOperations.VOID_DIRECTORY_NAME);
		// assert linksets...
		assertEquals(
				0,
				voidModels
						.get(3)
						.getOntModel()
						.listStatements(
								null,
								VOIDOntologyVocabulary.LINKSET_linkPredicate_prp,
								(RDFNode) null).toList().size());
		assertEquals(
				2,
				voidModels
						.get(0)
						.getOntModel()
						.listStatements(
								null,
								VOIDOntologyVocabulary.LINKSET_linkPredicate_prp,
								(RDFNode) null).toList().size());

	}

	@Test
	public void executeScenarioForOneEndpoint() throws Exception {
		String endpoint = "http://ft.rkbexplorer.com/sparql/";
		// String endpoint = "http://dbpedia.org/sparql";
		List<String> endpointList = new Vector<String>();
		endpointList.add(endpoint);
		extractor.setEndpointList(endpointList);
		long firstTime = System.currentTimeMillis();
		// execute...
		extractor
				.performVOIDAnalyzeAllScenario(FileOperations.VOID_DIRECTORY_NAME);
		assertEquals(1, extractor.getVocabulariesList().size());
		System.out.println("Time :" + (System.currentTimeMillis() - firstTime));
	}

	@Test
	public void exploreTripleCount() throws Exception {
		String query = "select (count(*) as ?count) where {?s ?p ?o}";
		// String serviceEndpoint = "http://dbtune.org/jamendo/sparql";
		String serviceEndpoint = "http://data.semanticweb.org/sparql";
		QueryExecution queryExecution = QueryExecutionFactory.sparqlService(
				serviceEndpoint, query);
		ResultSet resultSet = queryExecution.execSelect();
		QuerySolution querySolution = (QuerySolution) resultSet.next();
		int count = querySolution.get("count").asLiteral().getInt();
		assertTrue(count > 0);
	}

	@Test
	public void extractBasicProperties() throws Exception {
		// DEnenebilir
		String endpoint = "http://155.223.24.47:8893/drugbank/sparql";
		long firstTime = System.currentTimeMillis();
		List<String> endpointList = new Vector<String>();
		endpointList.add(endpoint);
		extractor.setEndpointList(endpointList);
		extractor.extractOneEndpoint(endpointList, false,
				FileOperations.MANUAL_CREATION_DIR, 0, 69);
		assertEquals(1, extractor.getVocabulariesList().size());
		System.out.println("Time :" + (System.currentTimeMillis() - firstTime));
	}

	@Test
	public void extractLinksetsForOneEndpoint() throws Exception {
		/**
		 * Extract void of given dataset
		 */
		// define endpoint of dataset that its void to be extracted.
		String endpoint = "http://155.223.24.47:8896/jamendo/sparql";
		long firstTime = System.currentTimeMillis();
		List<String> endpointList = new Vector<String>();
		// add endpoint to list
		endpointList.add(endpoint);
		extractor.setEndpointList(endpointList);
		// generate void of given endpoint
		extractor.extractOneEndpoint(endpointList, false,
				FileOperations.MANUAL_CREATION_DIR, 0, 72);
		// check contructed void size
		assertEquals(1, extractor.getVocabulariesList().size());
		System.out.println("Time :" + (System.currentTimeMillis() - firstTime));

		/**
		 * extract linksets...
		 */
		List<VOIDIndividualOntology> indvOntListOnManual = extractor
				.readFilesIntoModel(FileOperations.MANUAL_CREATION_DIR);
		VOIDIndividualOntology nyTimesIndvOnt = indvOntListOnManual.get(0);

		// create linksets
		List<VOIDIndividualOntology> indvOntList = new Vector<VOIDIndividualOntology>();
		// read small datasets' void files.
		indvOntList.addAll(extractor
				.readFilesIntoModel(FileOperations.COMPLETE_VOIDS));
		System.out.println(indvOntList.size());

		List<String> givenUrispaces = new Vector<String>();
		List<String> linkpredicateList = new Vector<String>();

		for (String string : extractor.getLinksetsList().get(0)) {
			String object = string.substring(string.lastIndexOf("**") + 2);
			String predicate = string.substring(string.indexOf("**") + 2,
					string.lastIndexOf("**"));
			if (!givenUrispaces.contains(object)) {
				givenUrispaces.add(object);
				linkpredicateList.add(predicate + " ");
			} else {
				int index = givenUrispaces.indexOf(object);
				linkpredicateList.set(index, linkpredicateList.get(index)
						+ predicate + " ");
			}
		}

		List<LinkedDataset> linkedDatasets = extractor
				.searchDatasetsIncludeGivenUrispaces(indvOntList,
						givenUrispaces, linkpredicateList);
		System.out.println(linkedDatasets.size());
		extractor.createLinksetIndividuals(
				nyTimesIndvOnt.listDatasets().get(0), linkedDatasets,
				nyTimesIndvOnt.getOntModel().listOntologies().toList().get(0)
						.getURI());
		FileOperations.writeVoidModelFile(nyTimesIndvOnt.getOntModel(), 250,
				FileOperations.MANUAL_CREATION_DIR);

	}

	/**
	 * This method tests extracting virtual linkset decriptions correctly and
	 * appropriately.
	 * 
	 * @throws Exception
	 */
	@Test
	public void extractVirtualLinksetDescriptions() throws Exception {

		// get all void documents
		List<OntModel> testVOIDModels = getAllVOIDModels(FileOperations.TEST_VOIDS);
		List<OntModel> completeVOIDModels = getAllVOIDModels(FileOperations.COMPLETE_VOIDS);

		// extract virtual linkset descriptions for all VOID document
		VOIDExtractor voidExtractor = new VOIDExtractor();

		// exercise SUT...
		voidExtractor.extractVirtualLinksetDescriptions(testVOIDModels,
				completeVOIDModels);

		// check void model contains at least one linkset property that contains
		// virtual dataset in objectsTarget
		checkVirtualLinksetHasBeenCreated(testVOIDModels, 1);

		// check created virtual URISpace doesn't exist in subject position
		// in any VOID document
		checkVirtualURISpaceHasNoOwner(testVOIDModels, completeVOIDModels);

		// write edited void models into files
		for (int i = 0; i < testVOIDModels.size(); i++) {
			FileOperations.writeVoidModelFile(testVOIDModels.get(i), i,
					FileOperations.VIRTUAL_LINKSET_VOIDS);
		}

	}

	@Test
	public void checkDatasetCountOnVoidStore() throws Exception {
		List<String> endpointList = getEndpointsOfDatasets(OnlineVoidStores.MONDECA);
		assertEquals(321, endpointList.size());
	}

	/**
	 * It retrieves datasets from the given void store.
	 * 
	 * @param voidStoreEndpointURL
	 * @throws NoDatasetException
	 * @throws WstxEOFException
	 */
	public List<String> getEndpointsOfDatasets(String voidStoreEndpointURL)
			throws NoDatasetException {
		Query query = QueryFactory
				.create("PREFIX void:<http://rdfs.org/ns/void#> PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT DISTINCT ?endpoint WHERE {?dataset rdf:type void:Dataset."
						+ "?dataset void:sparqlEndpoint ?endpoint. }");
		QueryExecution execution = QueryExecutionFactory.sparqlService(
				voidStoreEndpointURL, query);
		List<String> endpointList = new Vector<String>();
		ResultSet execSelect = null;
		execSelect = execution.execSelect();
		ResultSet set = execSelect;
		if (!set.hasNext())
			throw new NoDatasetException(
					"There is no dataset with an endpoint in the "
							+ voidStoreEndpointURL);
		while (set.hasNext()) {
			RDFNode rdfNode = set.next().get("endpoint");
			String endpointText = "";
			if (rdfNode.isLiteral())
				endpointText = rdfNode.asLiteral().getString().toString();
			else
				endpointText = rdfNode.asResource().getURI();
			endpointList.add(endpointText);
		}
		execution.close();
		return endpointList;
	}

	@Test
	public void oneEndpointAvailability() throws Exception {
		List<String> endpointList = getEndpointsOfDatasets(OnlineVoidStores.MONDECA);
		extractor.setEndpointList(endpointList);
		Query queryAsk = QueryFactory.create("ASK {?s ?p ?o}");
		QueryExecution exec = QueryExecutionFactory.sparqlService(
				endpointList.get(178), queryAsk);
		// create thread
		EndpointTimeoutThread listenerThread = new EndpointTimeoutThread(exec);
		listenerThread.start();
		Thread.sleep(3000);
		// remove if thread is alive or there is an exception when ask
		// query...
		if (listenerThread.isAlive() || listenerThread.getExec() == null) {
			assertTrue(true);
		} else
			Assert.fail();
	}

	@Test
	public void readFileIntoOntmodel() throws Exception {
		String filePath = FileOperations.VOID_DIRECTORY_NAME + "/datasets" + 0
				+ ".owl";
		File voidFile = new File(filePath);
		boolean isExist = voidFile.exists();
		if (!isExist)
			Assert.fail("There is no fiel to read!");
		OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		model.read(voidFile.toURI().toURL().toString());
		String ontURI = model.listOntologies().toList().get(0).getURI();
		VOIDIndividualOntology indvOnt = new VOIDIndividualOntology(ontURI,
				model);
		indvOnt.getOntModel().write(System.out);
	}

	// /**
	// * It extracts for all scenario.
	// *
	// * @param args
	// * @throws Exception
	// */
	// public static void main(String[] args) throws Exception {
	// VOIDExtractor voidExtractor = new VOIDExtractor(null);
	// List<String> endpointList = voidExtractor
	// .getEndpointsOfDatasets(OnlineVoidStores.DSI_LOD);
	// voidExtractor.setEndpointList(endpointList);
	// logger.info("STARTED...");
	// voidExtractor.checkAvailabilityOfEndpoints(endpointList);
	// voidExtractor
	// .performVOIDAnalyzeAllScenario(FileOperations.VOID_DIRECTORY_NAME);
	// }

	// /**
	// * For SMALL DATASETS' ENDPOINTS
	// *
	// * @param args
	// * @throws Exception
	// */
	// public static void main(String[] args) throws Exception {
	// VOIDExtractor voidExtractor = new VOIDExtractor(null);
	// List<String> smallEndpoints = readEndpointsFromTextFile(
	// FileOperations.VOID_DIRECTORY_NAME + "/completedEndpoints.txt",
	// ",");
	// logger.info("Partially extraction is started for small datasets.");
	// voidExtractor.startVOIDExtractionPartially(smallEndpoints,
	// FileOperations.SMALL_VOIDS_DIRECTORY_NAME);
	// }

	// /**
	// * It finds sizes of big datasets and writes them into sizes.txt and
	// * datasets have more than 30 millions triples are written to
	// specials.txt.
	// *
	// * @param args
	// * @throws Exception
	// */
	// public static void main(String[] args) throws Exception {
	// List<String> bigEndpoints = readEndpointsFromTextFile(
	// FileOperations.VOID_DIRECTORY_NAME + "/bigDatasetsEndpoint.txt",
	// null);
	// logger.info("Partially extraction is started for big datasets.");
	// List<Long> sizeOfBigDatasets = getTripleCounts(
	// OnlineVoidStores.DSI_LOD, bigEndpoints);
	// // write big datasets
	// FileOperations.writeFile("sizes.txt", true, "BIG DATASETS count: \n");
	// for (int i = 0; i < sizeOfBigDatasets.size(); i++) {
	// FileOperations.writeFile("sizes.txt", true, bigEndpoints.get(i)
	// + " " + sizeOfBigDatasets.get(i) + "\n");
	// }
	// // Hepsini tek tek çalıştır ama aynı anda çalıştır, extractone endpoint
	// // metodu ile
	// List<Long> specialBigDatasetsSize = new Vector<Long>();
	// List<String> specialBigDatasetsName = new Vector<String>();
	// for (int i = 0; i < sizeOfBigDatasets.size(); i++) {
	// long size = sizeOfBigDatasets.get(i);
	// if (size > 30000000) {
	// sizeOfBigDatasets.remove(i);
	// specialBigDatasetsName.add(bigEndpoints.get(i));
	// bigEndpoints.remove(i);
	// specialBigDatasetsSize.add(size);
	// i--;
	// }
	// }
	// // write specials
	// FileOperations.writeFile("specials.txt", true,
	// "SPECIAL DATASETS count: \n");
	// for (int i = 0; i < specialBigDatasetsName.size(); i++) {
	// FileOperations.writeFile("specials.txt", true,
	// specialBigDatasetsName.get(i) + " "
	// + specialBigDatasetsSize.get(i) + "\n");
	// }
	//
	// }

	// /**
	// * It extracts big datasets individually.
	// *
	// * @param args
	// * @throws IOException
	// */
	// public static void main(String[] args) throws IOException {
	// List<String> bigEndpoints = new Vector<String>();
	// // get bigdatasets under 30 million triples.
	// BufferedReader reader = new BufferedReader(new FileReader(new File(
	// "sizes.txt")));
	// String str;
	// // pass first line
	// reader.readLine();
	// while ((str = reader.readLine()) != null && str.length() > 10) {
	// String[] split = str.split(" ");
	// if (Long.parseLong(split[1]) < 30000000)
	// bigEndpoints.add(split[0]);
	// }
	//
	// // RUN THREADS
	// for (int i = 0; i < bigEndpoints.size(); i++) {
	// BigDatasetThread bigThread = new BigDatasetThread(
	// bigEndpoints.get(i), i, new VOIDExtractor(null));
	// logger.info(i + ") " + bigEndpoints.get(i)
	// + " is started to extract!");
	// bigThread.start();
	//
	// }
	// }

	// public static void main(String[] args) {
	// String directory = "/home/galaksiya-1/Desktop/TDBdumps/";
	// Model model = TDBFactory.createModel(directory);
	// List<String> urispaceList = new VOIDExtractor(null)
	// .extractUriSpacesManuallyForBigDataset(model,
	// ExampleVocabulary.DBPEDIA_URISPACE);
	// System.out.println("URISPACES: " + urispaceList);
	// model.close();
	// }

	private void checkVirtualLinksetHasBeenCreated(
			List<OntModel> testVOIDModels, int voidModelCount) {
		// check size of void model size
		assertEquals(voidModelCount, testVOIDModels.size());
		for (OntModel voidModel : testVOIDModels) {
			// create a void individual ontology for this void model
			VOIDIndividualOntology voidIndividualOntology = new VOIDIndividualOntology(
					null, voidModel);
			// list all datasets contained in this model
			List<Individual> datasets = voidIndividualOntology.listDatasets();
			// find main and virtual datasets
			Individual mainDatasetIndv = findMainDataset(datasets);
			// check found main and virtual datasets are not null
			assertNotNull(mainDatasetIndv);
			for (Individual virtualDatasetIndv : datasets) {
				if (!mainDatasetIndv.equals(virtualDatasetIndv)) {
					assertNotNull(virtualDatasetIndv);
					// prepare a query for linkset definition of these datasets.
					logger.info(MessageFormat
							.format("Main dataset individual value: {0}, virtual dataset individual value: {1}",
									mainDatasetIndv, virtualDatasetIndv));
					String queryStr = QueryVocabulary.RDF_PREFIX_URI
							+ QueryVocabulary.VOID_PREFIX_URI
							+ "SELECT ?linkset WHERE {"
							+ "?linkset rdf:type void:Linkset. "
							+ "?linkset void:subjectsTarget <"
							+ mainDatasetIndv.getURI() + ">."
							+ "?linkset void:objectsTarget <"
							+ virtualDatasetIndv.getURI() + ">."
							+ "?linkset void:linkPredicate ?linkPredicate. "
							+ "}";
					QueryExecution queryExecution = QueryExecutionFactory
							.create(queryStr, voidModel);
					ResultSet resultSet = queryExecution.execSelect();
					// check there is at least one linkset definition
					assertTrue(resultSet.hasNext());
				}
			}
		}

	}

	/**
	 * This method checks URISpaces of a virtual dataset contained in a void
	 * model doesn't exist URISpace of any of voidModels
	 * 
	 * @param voidModels
	 * @param completeVOIDModels
	 * @throws URISyntaxException
	 */
	private void checkVirtualURISpaceHasNoOwner(List<OntModel> voidModels,
			List<OntModel> completeVOIDModels) {
		// get all void models
		for (OntModel voidModel : voidModels) {
			// get dataset resouces of void models.
			ResIterator resIterator = voidModel.listResourcesWithProperty(
					RDF.type, VOIDOntologyVocabulary.DATASET_rsc);
			// iterate on dataset resources
			while (resIterator.hasNext()) {
				// get next dataset resource
				Resource resource = (Resource) resIterator.next();
				// if dataset is a virtual dataset (checking this that has no
				// sparql endpoint property)
				if (!resource
						.hasProperty(VOIDOntologyVocabulary.DATASET_sparqlEndpoint_prp)) {
					// get dataset urispace statement of this resource
					Statement uriSpacePrpStmt = resource
							.getProperty(VOIDOntologyVocabulary.DATASET_uriSpace_prp);
					// if uriSpaceStmt is not null
					if (uriSpacePrpStmt != null) {
						// search URISpace among all voids
						for (OntModel checkedVOID : completeVOIDModels) {
							// check no void model contains this virtual
							// URISpace value as its URISpaces
							assertFalse(checkedVOID
									.contains(
											null,
											VOIDOntologyVocabulary.DATASET_uriSpace_prp,
											uriSpacePrpStmt.getObject()));
						}
					}
				}
			}
		}
	}

	private List<String> createLinksetItems(String... linksetPatterns) {
		List<String> linksetItemList = new Vector<String>();
		for (String string : linksetPatterns) {
			linksetItemList.add(string);
		}
		return linksetItemList;
	}

	private Vector<String> createUrispaceList(String urispace) {
		Vector<String> aUrispaces = new Vector<String>();
		aUrispaces.add(urispace);
		return aUrispaces;
	}

	/**
	 * This method finds main and virtual datasets.
	 * 
	 * @param datasets
	 */
	private Individual findMainDataset(List<Individual> datasets) {
		for (Individual datasetIndv : datasets) {
			// look main
			if (datasetIndv
					.hasProperty(VOIDOntologyVocabulary.DATASET_sparqlEndpoint_prp)) {
				return datasetIndv;
			}
		}
		return null;
	}

	/**
	 * This method reads all void filescontained in given directory and
	 * retrieves them into ontModels.
	 * 
	 * @param filePath
	 *            TODO
	 * 
	 * @return {@link List} of VOID {@link OntModel} instances
	 * @throws MalformedURLException
	 */
	private List<OntModel> getAllVOIDModels(String filePath)
			throws MalformedURLException {
		List<VOIDIndividualOntology> readModels = new VOIDExtractor(null)
				.readFilesIntoModel(filePath);
		Vector<OntModel> allVoidModels = new Vector<OntModel>();
		for (VOIDIndividualOntology voidIndividualOntology : readModels) {
			allVoidModels.add(voidIndividualOntology.getOntModel());
		}
		return allVoidModels;
	}

}
