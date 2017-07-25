package tr.edu.ege.seagent.voidextractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.log4j.Logger;

import tr.edu.ege.seagent.dataset.vocabulary.VOIDIndividualOntology;
import tr.edu.ege.seagent.dataset.vocabulary.VOIDOntologyVocabulary;
import tr.edu.ege.seagent.voidextractor.exception.NoDatasetException;
import tr.edu.ege.seagent.voidextractor.thread.EndpointTimeoutThread;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;


public class VOIDExtractor {

	private static final String SLASH = "/";

	private static final Property SPARQL_ENDPOINT_PRP = ResourceFactory
			.createProperty("http://rdfs.org/ns/void#sparqlEndpoint");

	private static int MAX_VOID_SIZE = 73;

	private List<String> bigDatasetList = new Vector<String>();

	/**
	 * It holds available dataset endpoints.
	 */
	private List<String> endpointList;

	/**
	 * It holds linkset description elements for a dataset.
	 */
	private List<String> linksetItemList = new Vector<String>();

	/**
	 * It holds linkset item list.
	 */
	private List<List<String>> linksetsList = new Vector<List<String>>();

	public Logger logger = Logger.getLogger(VOIDExtractor.class);

	/**
	 * It holds urispaces for a dataset.
	 */
	private List<String> urispaceList = new Vector<String>();

	/**
	 * It holds uripsaces for each dataset.
	 */
	private List<List<String>> urispacesList = new Vector<List<String>>();

	/**
	 * It holds the vocabulary list for each dataset.
	 */
	private List<List<String>> vocabulariesList = new Vector<List<String>>();

	/**
	 * It holds the vocabulary list for a dataset.
	 */
	private List<String> vocList = new Vector<String>();

	/**
	 * It holds the created voids for each dataset.
	 */
	private List<VOIDIndividualOntology> voidIndividualOntList = new Vector<VOIDIndividualOntology>();

	public VOIDExtractor(List<String> endpointList) {
		this.endpointList = endpointList;
	}

	public VOIDExtractor() {
		this(null);
	}

	/**
	 * This method generates endpoint list from endpoint property contained in
	 * given voidModels
	 * 
	 * @param voidModels
	 *            {@link OntModel} instances which contains endpoint property in
	 *            it.
	 * @return {@link List} of enpoints
	 */
	private List<String> generateEndpointListFromVOIDModels(
			List<OntModel> voidModels) {
		List<String> endpointList = new ArrayList<String>();
		for (OntModel voidModel : voidModels) {
			// get unique sparqlEndpoint property value and add it to the
			// endpoint list.
			endpointList.add(voidModel
					.listObjectsOfProperty(SPARQL_ENDPOINT_PRP).toList().get(0)
					.asLiteral().getString());
		}
		return endpointList;
	}

	/**
	 * It asks to endpoints to understand datasets are available.
	 * 
	 * @param endpointList
	 * @throws NoDatasetException
	 * @throws InterruptedException
	 * @return
	 */
	public List<String> checkAvailabilityOfEndpoints(List<String> endpointList)
			throws NoDatasetException, InterruptedException {
		List<EndpointTimeoutThread> threadList = new Vector<EndpointTimeoutThread>();
		// create a thread for each endpoint to check whose availability...
		createAskQueryThreadList(endpointList, threadList);
		logger.info("There are "
				+ endpointList.size()
				+ " endpoints and they will be checked to determine availability.");
		// start each thread...
		for (EndpointTimeoutThread endpointTimeoutThread : threadList) {
			System.out.println(endpointTimeoutThread + " is started.");
			endpointTimeoutThread.start();
		}

		// wait 3 second for each thread...
		removeUnavailableEndpoints(endpointList, threadList);
		logger.info("There are " + endpointList.size() + "available endpoints.");
		if (endpointList.size() <= 0)
			throw new NoDatasetException(
					"There is no dataset whose endpoint is available");
		return endpointList;
	}

	/**
	 * It is for creating a void file after basic dataset extraction.
	 * 
	 * @param analyzedIndex
	 * @param endpointList
	 * @param directoryName
	 * @param voidFileNumber
	 * @return
	 * @throws IOException
	 */
	public VOIDIndividualOntology createVOID(int analyzedIndex,
			List<String> endpointList, String directoryName, int voidFileNumber)
			throws IOException {
		String ontURI = Vocabulary.VOID_ONTOLOGY_URI_PREFIX + analyzedIndex;
		VOIDIndividualOntology indvOnt = new VOIDIndividualOntology(ontURI,
				ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM), null);
		// create dataset with endpoint...
		Individual dataset = indvOnt.createDataset(
				endpointList.get(analyzedIndex), null, null);
		// add vocabulary properties...
		if (vocabulariesList.size() > 0) {
			for (String vocabulary : vocabulariesList.get(analyzedIndex)) {
				indvOnt.addDatasetVocabularyProperty(dataset, vocabulary);
			}
		}
		// add urispace properties...
		for (String urispace : urispacesList.get(analyzedIndex)) {
			indvOnt.addDatasetUriSpace(dataset,
					ResourceFactory.createPlainLiteral(urispace));
		}
		FileOperations.writeVoidModelFile(indvOnt.getOntModel(),
				voidFileNumber, directoryName);
		return indvOnt;
	}

	/**
	 * It extracts vocabularies of datasets that has the given endpoints and
	 * coids are created into the given ddirectory name.
	 * 
	 * @param endpointList
	 * @param directoryName
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<String> extractBasicPropertiesOfDatasets(
			List<String> endpointList, Boolean bigDatasetsEndpoint,
			String directoryName) throws Exception {
		// all datasets are analyzed for extracting vocabulary and urispace
		// properties of datasets.
		for (int i = 0; i < endpointList.size(); i++) {
			i = extractOneEndpoint(endpointList, bigDatasetsEndpoint,
					directoryName, i, i);
		}
		return endpointList;
	}

	/**
	 * It extracts the VOID linkset descriptions into the given directory name.
	 * 
	 * @param linksetsItemsList
	 * @param indvOntList
	 * @param directoryName
	 * @throws Exception
	 */
	public void extractLinksetDescriptions(
			List<List<String>> linksetsItemsList,
			List<VOIDIndividualOntology> indvOntList, String directoryName)
			throws Exception {
		int index = -1;
		for (List<String> linksetItemList : linksetsItemsList) {
			index++;
			VOIDIndividualOntology indvOnt = indvOntList.get(index);
			if (linksetItemList.size() > 0) {
				List<String> linkedObjectUrispaces = new Vector<String>();
				List<String> linkPredicates = new Vector<String>();
				for (String linksetItem : linksetItemList) {
					try {
						String objectTarget = linksetItem.substring(linksetItem
								.lastIndexOf("**") + 2);
						String linkPredicate = linksetItem.substring(
								linksetItem.indexOf("**") + 2,
								linksetItem.lastIndexOf("**"));
						linkedObjectUrispaces.add(objectTarget);
						linkPredicates.add(linkPredicate);
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println(linksetItem);
						continue;
					}
				}
				List<LinkedDataset> linkedDatasetList = searchDatasetsIncludeGivenUrispaces(
						indvOntList, linkedObjectUrispaces, linkPredicates);
				// create linkset description in referer dataset.
				String ontURI = indvOnt.getOntModel().listOntologies().toList()
						.get(0).getURI();
				createLinksetIndividuals(indvOnt.listDatasets().get(0),
						linkedDatasetList, ontURI);
			}
			// add linksets to the void model...
			FileOperations.writeVoidModelFile(indvOnt.getOntModel(), index,
					directoryName);

		}

	}

	/**
	 * It extracts just one endpoint.Void file number is used to give a dataset
	 * file number.
	 * 
	 * @param endpointList
	 * @param bigDatasetsEndpoint
	 * @param directoryName
	 * @param index
	 * @param voidFileNumber
	 * @return
	 * @throws IOException
	 */
	public int extractOneEndpoint(List<String> endpointList,
			Boolean bigDatasetsEndpoint, String directoryName, int index,
			int voidFileNumber) throws IOException {
		String endpoint = endpointList.get(index);
		try {
			// big dataset checking
			// if (!bigDatasetsEndpoint) {
			// // query for triple count of dataset...
			// // long tripleCount = 0;
			// long tripleCount = QueryOperation.getTripleCount(endpoint);
			// if (tripleCount > 1000000) {
			// endpointList.remove(endpoint);
			// bigDatasetList.add(endpoint);
			// index--;
			// logger.info(endpoint + " is moved to big datasets list.");
			// return index;
			// }
			// }
			// query for basic void properties...
			queryAnEndpointForBasicPropertiesOfDataset(endpoint, directoryName);
			// check rdf, rdfs, etc properties...
			remeoveUnnecessaryPredicates(vocList);
			// check rdf, rdfs, etc objects...
			removeUnnecessaryLinksetObjects(linksetItemList);

			// add properties to table...
			vocabulariesList.add(index, vocList);
			urispacesList.add(index, urispaceList);
			linksetsList.add(index, linksetItemList);
			vocList = new Vector<String>();
			urispaceList = new Vector<String>();
			linksetItemList = new Vector<String>();
			// create VOID file for dataset...
			logger.info(index + ": VOID is extracted from " + endpoint + ".");
			// basic void model is created.
			VOIDIndividualOntology newIndvOnt = createVOID(index, endpointList,
					directoryName, voidFileNumber);
			voidIndividualOntList.add(index, newIndvOnt);
		} catch (Exception e) {
			// write failed endpoint to the file...
			FileOperations.writeFile(directoryName
					+ FileOperations.FAILED_FILES_PATH, true, endpoint
					+ " Exception: " + e.getMessage() + ".\n");
			endpointList.remove(endpoint);
			logger.error(endpoint + " is not extracted!");
			index--;
			e.printStackTrace();
		}
		return index;
	}

	public List<String> getBigDatasetList() {
		return bigDatasetList;
	}

	public List<String> getEndpointList() {
		return endpointList;
	}

	public List<List<String>> getLinksetsList() {
		return linksetsList;
	}

	public List<List<String>> getUrispacesList() {
		return urispacesList;
	}

	public List<List<String>> getVocabulariesList() {
		return vocabulariesList;
	}

	public List<VOIDIndividualOntology> getVoidIndvOntList() {
		return voidIndividualOntList;
	}

	/**
	 * It extracts all VOIDs for datasets.
	 * 
	 * @param directoryName
	 * 
	 * @throws Exception
	 */
	public void performVOIDAnalyzeAllScenario(String directoryName)
			throws Exception {
		deleteAllInfoFiles(directoryName);
		// it checks the availability of found endpoints...
		checkAvailabilityOfEndpoints(endpointList);
		// "vocabularies", "urispaces" and "who links who by which" are
		// determined.
		extractBasicPropertiesOfDatasets(endpointList, false, directoryName);

		// write bigdatasets' endpoints to a discrete file
		writeFileBigDatasetEndpoints(directoryName);

		// extract big datasets
		extractBasicPropertiesOfDatasets(bigDatasetList, true, directoryName);
		// add big datasets' endpoints to endpointlist
		for (String endpoint : bigDatasetList) {
			endpointList.add(endpoint);
		}
		// "who links who by which" is used to extract linksets.
		extractLinksetDescriptions(linksetsList, voidIndividualOntList,
				directoryName);
	}

	public void setEndpointList(List<String> endpointList) {
		this.endpointList = endpointList;
	}

	/**
	 * 
	 * TODO Bu metot böyle işlemez, büyüklerin linksets.rxt'si farklı alınır...
	 * It extracts linksets by using info files. It writes full voids into the
	 * fullVoids directory.
	 * 
	 * @throws Exception
	 */
	public void startLinksetsExtractionPartially() throws Exception {
		List<VOIDIndividualOntology> indvOntList = new Vector<VOIDIndividualOntology>();
		List<List<String>> linksetLists = new Vector<List<String>>();
		// read small datasets' void files.
		indvOntList
				.addAll(readFilesIntoModel(FileOperations.SMALL_VOIDS_DIRECTORY_NAME));
		// read big datasets' void files.
		indvOntList
				.addAll(readFilesIntoModel(FileOperations.BIG_VOIDS_DIRECTORY_NAME));
		// get linkset items list from small datasets
		linksetLists.addAll(getLinksetItemList(
				FileOperations.SMALL_VOIDS_DIRECTORY_NAME, true, -1));
		// get linkset items from big datasets
		linksetLists.addAll(getLinksetItemList(
				FileOperations.BIG_VOIDS_DIRECTORY_NAME, false, 17));
		// extract linkset descriptions
		extractLinksetDescriptions(linksetLists, indvOntList,
				FileOperations.FULL_VOIDS);

	}

	/**
	 * It performs VOID extraction without linksets. It just constructs basic
	 * VOID files with urispaces and vocabularies. Also it constructs a file
	 * which includes referenced objects, referer resources and linkpredicates
	 * for each dataset.
	 * 
	 * @throws Exception
	 */
	public List<String> startVOIDExtractionPartially(List<String> endpointList,
			String directoryName) throws Exception {
		// delete all lists;
		clearAllLists();
		// delete all info files
		deleteAllInfoFiles(directoryName);
		// it checks the availability of found endpoints...
		List<String> filteredEndpoints = checkAvailabilityOfEndpoints(endpointList);
		// extract basic voids
		List<String> lastEndpointList = extractBasicPropertiesOfDatasets(
				filteredEndpoints, true, directoryName);
		// VOIDS were written and now write linksets to a file
		writeLinksetSPOForSmallDatasets(directoryName, linksetsList);
		return lastEndpointList;
	}

	/**
	 * It adds the prefix of the given URI if there is not exist.
	 * 
	 * @param list
	 * @param uri
	 */
	private void addIntoGivenList(List<String> list, String addedPrefix) {
		if (!list.contains(addedPrefix)) {
			if (!addedPrefix.equals("")) {
				list.add(addedPrefix);
			}
		}
	}

	/**
	 * Adds the subjectUriSpace, predicate uri and object urispace for a triple.
	 * 
	 * @param linksetItemList
	 * @param objectUriPrefix
	 * @param subjectUriPrefix
	 * @param predicateUri
	 */
	private void addLinksetItems(List<String> linksetItemList,
			String objectUriPrefix, String subjectUriPrefix, String predicateUri) {
		if (!(subjectUriPrefix.equals("") || objectUriPrefix.equals(""))) {
			String addedLinksetItem = subjectUriPrefix + "**" + predicateUri
					+ "**" + objectUriPrefix;
			if (!linksetItemList.contains(addedLinksetItem))
				linksetItemList.add(addedLinksetItem);
		}

	}

	/**
	 * Adds the given vocabulary to vocabularylist if doesn't exist.
	 * 
	 * @param vocList
	 * @param predicateUri
	 * @param objectUriPrefix
	 */
	private void addVocabulary(List<String> vocList, String predicateUri,
			String objectUriPrefix) {
		if (predicateUri.equals(Vocabulary.RDF_TYPE_URI)
				&& objectUriPrefix != null) {
			addIntoGivenList(vocList, objectUriPrefix);
		} else {
			addIntoGivenList(vocList, getPrefixOfURI(predicateUri));
		}
	}

	/**
	 * It clears all lists.
	 */
	private void clearAllLists() {
		if (vocabulariesList != null || vocabulariesList.size() > 0)
			vocabulariesList.clear();
		if (urispacesList != null || urispacesList.size() > 0)
			urispacesList.clear();
		if (linksetsList != null || linksetsList.size() > 0)
			linksetsList.clear();

	}

	/**
	 * Creates a thread for each endpoint to execute ask query on it.
	 * 
	 * @param endpointList
	 * @param threadList
	 */
	private void createAskQueryThreadList(List<String> endpointList,
			List<EndpointTimeoutThread> threadList) {
		for (int i = 0; i < endpointList.size(); i++) {
			Query queryAsk = QueryFactory.create("ASK {?s ?p ?o}");
			QueryExecution exec = QueryExecutionFactory.sparqlService(
					endpointList.get(i), queryAsk);
			// create thread
			EndpointTimeoutThread listenerThread = new EndpointTimeoutThread(
					exec);
			threadList.add(listenerThread);
		}
	}

	/**
	 * It creates linkset individuals with the given parameters.
	 * 
	 * @param refererDataset
	 * @param targetedList
	 * @param ontURI
	 */
	public void createLinksetIndividuals(Individual refererDataset,
			List<LinkedDataset> targetedList, String ontURI) {
		VOIDIndividualOntology indvOnt = new VOIDIndividualOntology(ontURI,
				refererDataset.getOntModel());
		for (LinkedDataset linkedDataset : targetedList) {
			indvOnt.createLinkset(linkedDataset.getLinkPredicate(),
					linkedDataset.getLinkedDataset(), refererDataset);
		}
	}

	private void deleteAllInfoFiles(String directoryName) {
		FileOperations.deleteFile(directoryName
				+ FileOperations.BIG_DATASETS_ENDPOINTS_PATH);
		FileOperations.deleteFile(directoryName
				+ FileOperations.FAILED_FILES_PATH);
		FileOperations.deleteFile(directoryName
				+ FileOperations.COMPLETED_ENDPOINTS_PATH);
	}

	/**
	 * It transforms linkset text file into a table.
	 * 
	 * @param directoryName
	 * @param readFromOneFile
	 *            TODO
	 * @return
	 * @throws IOException
	 */
	public List<List<String>> getLinksetItemList(String directoryName,
			boolean readFromOneFile, int multipleFileCount) throws IOException {
		List<List<String>> linksetsList = new Vector<List<String>>();
		int fileNumber = 0;
		int processedFileCount = 0;
		File linksetItemFile;
		while (true) {
			if (readFromOneFile)
				linksetItemFile = new File(directoryName
						+ FileOperations.LINKSET_ITEMS_FILE_FULL_NAME);
			else
				linksetItemFile = new File(directoryName
						+ FileOperations.LINKSETS_ITEM_FILENAME_WITHOUT_NUMBER
						+ fileNumber + FileOperations.TXT_EXTENSION);
			fileNumber++;
			if (linksetItemFile.exists()) {
				processedFileCount++;
			} else {
				if (processedFileCount == multipleFileCount)
					break;
			}
			if (linksetItemFile.exists()) {
				BufferedReader in = new BufferedReader(new FileReader(
						linksetItemFile.getAbsoluteFile()));
				String str = "";
				while ((str = in.readLine()) != null) {
					String[] aRow = str.split(" ");
					List<String> newItemList = new Vector<String>();
					linksetsList.add(newItemList);
					for (String item : aRow) {
						linksetsList.get(processedFileCount - 1).add(item);
					}
				}
				if (linksetsList.size() == processedFileCount - 1)
					linksetsList.add(new Vector<String>());
				in.close();
			}
			if (readFromOneFile)
				break;

		}
		return linksetsList;
	}

	// /**
	// * Returns the prefix of predicate.
	// *
	// * @param URIstr
	// * @return
	// */
	// public static String reduceURI(String URIstr) {
	// URI uri = URI.create(URIstr);
	// try {
	// String protocol = uri.toURL().getProtocol();
	// String host = uri.getHost();
	// String path = uri.getPath();
	//
	// if (path != null && !path.equals("")) {
	// if (path.endsWith("/")) {
	// // remove last slash
	// path = path.substring(0, path.length() - 1);
	// }
	// path = path.substring(0, path.lastIndexOf("/") + 1);
	// return protocol + "://" + host + path;
	// }
	// } catch (MalformedURLException e) {
	// e.printStackTrace();
	// }
	// return URIstr;
	// }

	/**
	 * It searches all vocabularies on whole dataset.
	 * 
	 * @param endpoint
	 * @return
	 * @throws Exception
	 */
	private void queryAnEndpointForBasicPropertiesOfDataset(String endpoint,
			String directoryName) throws Exception {
		int offsetIncValue = 10000;
		boolean continueBlockQuery = true;
		long offsetIndex = 0;
		while (continueBlockQuery) {
			Query query = QueryFactory
					.create("SELECT ?s ?p ?o WHERE {?s ?p ?o} LIMIT 10000 OFFSET "
							+ offsetIndex);
			QueryExecution execution = null;
			ResultSet set = null;

			int tryingCount = 0;
			boolean queryAgain = true;

			// if there is an exception when querying it is tried to execute 5
			// times...
			while (queryAgain && tryingCount < 5) {
				try {
					execution = QueryExecutionFactory.sparqlService(endpoint,
							query);
					ResultSet execSelect = null;
					execSelect = execution.execSelect();
					set = execSelect;

					// process each row...
					while (set.hasNext()) {
						QuerySolution solution = set.next();
						// get subject
						String subjectUriPrefix = null;
						Resource subjectResource = solution.getResource("s");
						if (subjectResource.isURIResource()) {
							subjectUriPrefix = getPrefixOfURI(subjectResource
									.getURI());
							// add urispaces...
							addIntoGivenList(urispaceList, subjectUriPrefix);
						}

						// get predicate
						String predicateUri = solution.getResource("p")
								.getURI();

						// get object
						String objectUriPrefix = null;
						if (!predicateUri.equals(Vocabulary.RDF_TYPE_URI)
								&& solution.get("o").isURIResource()
								&& solution.get("s").isURIResource()) {
							objectUriPrefix = getPrefixOfURI(solution
									.getResource("o").getURI());
							// add linkset properties...
							addLinksetItems(linksetItemList, objectUriPrefix,
									subjectUriPrefix, predicateUri);
						}
						// add vocabularies...
						addVocabulary(vocList, predicateUri, objectUriPrefix);

					}
					queryAgain = false;
				} catch (Exception e) {
					// execution.close();
					// throw new IllegalDataException(
					// "There is some illegal data on dataset : " + endpoint);
					logger.info("Query tried to execute " + (tryingCount + 1)
							+ " times, but failed!");
					execution.close();
					if (tryingCount == 4) {
						// throw new WstxEOFException(
						// "Execution failed due to unexpected EOF file",
						// null);
						throw new Exception("This endpoint falls" + endpoint,
								e.getCause());
					}
				}
				tryingCount++;
			}

			// If getting row count slower than 20001 Query finish log...
			if (set.getRowNumber() < ((offsetIncValue * 2) + 1)) {
				System.out.println("Query is completed");
				continueBlockQuery = false;
				// write completed results
				FileOperations.writeFile(directoryName
						+ FileOperations.COMPLETED_ENDPOINTS_PATH, true,
						endpoint + ", "
								+ (offsetIndex + (set.getRowNumber() / 2))
								+ " row retrieved.\n");
			}
			System.out.println("Seeked row " + (offsetIndex));
			// increment offset for another execution..
			offsetIndex += offsetIncValue;
			execution.close();
		}
	}

	/**
	 * This method reads files under the given path and creates a list with
	 * {@link VOIDIndividualOntology} instances
	 * 
	 * @param directoryName
	 *            directory whic contains VOID files
	 * @return {@link VOIDIndividualOntology} list
	 * @throws MalformedURLException
	 */
	public List<VOIDIndividualOntology> readFilesIntoModel(String directoryName)
			throws MalformedURLException {
		List<VOIDIndividualOntology> indvOntList = new Vector<VOIDIndividualOntology>();
		// Iterate until the deadline number and get all void models.
		for (int index = 0; index < MAX_VOID_SIZE; index++) {
			// create a file under given directory and consisting given index
			// value
			File voidFile = new File(generateAbsoluteFilePath(directoryName,
					index));
			// check whether the file is exist and read model if so.
			if (voidFile.exists()) {
				OntModel model = ModelFactory
						.createOntologyModel(OntModelSpec.OWL_MEM);
				model.read(voidFile.toURI().toURL().toString());
				String ontURI = model.listOntologies().toList().get(0).getURI();
				VOIDIndividualOntology indvOnt = new VOIDIndividualOntology(
						ontURI, model);
				// add read model into list
				indvOntList.add(indvOnt);
				logger.info(MessageFormat.format(
						"VOID file <{0}> is read and added in the list",
						voidFile.toURI().toURL().toString()));
			} else {
				logger.warn(MessageFormat
						.format("VOID file <{0}> cannot be read because it doesn't exist under the path",
								voidFile.toURI().toURL().toString()));
			}
		}
		return indvOntList;
	}

	/**
	 * This method generates absolute file path, for the file with given index
	 * 
	 * @param directoryName
	 *            directory of file
	 * @param index
	 *            index number of file
	 * @return absolute file path
	 */
	private String generateAbsoluteFilePath(String directoryName, int index) {
		return directoryName + "/datasets" + index + ".owl";
	}

	/**
	 * It removes the rdf,rdfs and etc vocabbularies.
	 * 
	 * @param list
	 */
	private void remeoveUnnecessaryPredicates(List<String> list) {
		if (list.contains(Vocabulary.RDF_SCHEMA_PREFIX))
			list.remove(Vocabulary.RDF_SCHEMA_PREFIX);
		if (list.contains(Vocabulary.RDF_PREFIX))
			list.remove(Vocabulary.RDF_PREFIX);
		if (list.contains(Vocabulary.OWL_PREFIX))
			list.remove(Vocabulary.OWL_PREFIX);
		if (list.contains(Vocabulary.XML_SCHEMA_PREFIX))
			list.remove(Vocabulary.XML_SCHEMA_PREFIX);

	}

	/**
	 * It checks each ask query thread and if it is alive or there is an
	 * exception endpoint is accepted unavailable.
	 * 
	 * @param endpointList
	 * @param threadList
	 * @throws InterruptedException
	 */
	private void removeUnavailableEndpoints(List<String> endpointList,
			List<EndpointTimeoutThread> threadList) throws InterruptedException {
		int index = -1;
		for (int i = 0; i < threadList.size(); i++) {
			index++;
			Thread.sleep(3000);
			// remove if thread is alive or there is an exception when ask
			// query...
			if (threadList.get(i).isAlive()
					|| threadList.get(i).getExec() == null) {
				System.err.println(threadList.get(i)
						+ " is not alive and killed.");
				endpointList.remove(index);
				index--;
			} else
				System.out.println(threadList.get(i) + " is valid");
		}
	}

	private void removeUnnecessaryLinksetObjects(List<String> list) {
		for (int i = 0; i < list.size(); i++) {
			String row = list.get(i);
			String object = row.substring(row.lastIndexOf("**") + 2);
			if (object.equals(Vocabulary.RDF_SCHEMA_PREFIX)) {
				list.remove(Vocabulary.RDF_SCHEMA_PREFIX);
				list.remove(row);
				i--;
			} else if (object.equals(Vocabulary.RDF_PREFIX)) {
				list.remove(Vocabulary.RDF_PREFIX);
				list.remove(row);
				i--;
			} else if (object.equals(Vocabulary.OWL_PREFIX)) {
				list.remove(Vocabulary.OWL_PREFIX);
				list.remove(row);
				i--;
			} else if (object.equals(Vocabulary.XML_SCHEMA_PREFIX)) {
				list.remove(Vocabulary.XML_SCHEMA_PREFIX);
				list.remove(row);
				i--;
			}
		}
	}

	/**
	 * It searches urispaces excluding referer dataset's urispaces.
	 * 
	 * @param objectTarget
	 * @return
	 */
	public List<LinkedDataset> searchObjecTargetInAllUrispaces(
			String objectTarget, String linkPredicate, int index) {
		List<LinkedDataset> targetedDatasetList = new Vector<LinkedDataset>();
		for (int i = 0; i < urispacesList.size(); i++) {
			if (i == index)
				continue;
			else {
				// set object target dataset and link predicate
				if (urispacesList.get(i).contains(objectTarget)) {
					targetedDatasetList.add(new LinkedDataset(
							voidIndividualOntList.get(i).listDatasets().get(0),
							ResourceFactory.createProperty(linkPredicate)));
				}
			}

		}
		return targetedDatasetList;
	}

	/**
	 * It searches given urispaces in the given individual ontologies.
	 * 
	 * @param objectTarget
	 * @return
	 */
	public List<LinkedDataset> searchDatasetsIncludeGivenUrispaces(
			List<VOIDIndividualOntology> indvOntList,
			List<String> givenUrispaces, List<String> linkPredicateList) {
		List<LinkedDataset> linkedDatasetList = new Vector<LinkedDataset>();
		for (VOIDIndividualOntology indvOnt : indvOntList) {
			List<Individual> datasets = indvOnt.listDatasets();
			for (Individual dataset : datasets) {
				List<Statement> stmtList = dataset
						.getOntModel()
						.listStatements(dataset,
								VOIDOntologyVocabulary.DATASET_uriSpace_prp,
								(RDFNode) null).toList();
				for (Statement statement : stmtList) {
					String foundUrispace = statement.getObject().asLiteral()
							.getString().toString();
					if (givenUrispaces.contains(foundUrispace)) {
						int datasetIndex = givenUrispaces
								.indexOf(foundUrispace);
						String[] linkPredicates = linkPredicateList.get(
								datasetIndex).split(" ");
						// create linkedDatasets
						for (String string : linkPredicates) {
							linkedDatasetList.add(new LinkedDataset(dataset,
									ResourceFactory.createProperty(string)));
						}
						break;
					}
				}
			}
		}
		return linkedDatasetList;
	}

	private void writeFileBigDatasetEndpoints(String directoryName)
			throws IOException {
		String block = "";
		for (String endpoint : bigDatasetList) {
			block += endpoint + "\n";
		}
		if (bigDatasetList != null && bigDatasetList.size() > 0)
			FileOperations.writeFile(directoryName
					+ FileOperations.BIG_DATASETS_ENDPOINTS_PATH, true, block);

	}

	/**
	 * Writes extracted linkset info to a file.
	 * 
	 * @param directoryName
	 * @param linksetsList
	 * @throws IOException
	 */
	private void writeLinksetSPOForSmallDatasets(String directoryName,
			List<List<String>> linksetsList) throws IOException {
		File linksetItemFile = new File(directoryName
				+ FileOperations.LINKSET_ITEMS_FILE_FULL_NAME);
		linksetItemFile.createNewFile();
		FileWriter writer = new FileWriter(linksetItemFile, true);
		for (List<String> linksetRow : linksetsList) {
			String aRow = "";
			for (String linksetItemOfDataset : linksetRow) {
				aRow += linksetItemOfDataset + " ";
			}
			writer.append(aRow + "\n");
		}
		writer.close();
	}

	/**
	 * Writes extracted linkset info to a file.
	 * 
	 * @param directoryName
	 * @param linksetsList
	 * @throws IOException
	 */
	public void writeLinksetSPOForBigDatasets(String directoryName,
			List<String> linksetItemList, int fileNumber) throws IOException {
		File linksetItemFile = new File(directoryName
				+ FileOperations.LINKSETS_ITEM_FILENAME_WITHOUT_NUMBER
				+ fileNumber + FileOperations.TXT_EXTENSION);
		linksetItemFile.createNewFile();
		FileWriter writer = new FileWriter(linksetItemFile, true);
		String aRow = "";
		for (String linksetItemOfDataset : linksetItemList) {
			aRow += linksetItemOfDataset + " ";
		}
		writer.append(aRow + "\n");
		writer.close();
	}

	public List<String> extractUriSpacesManuallyForBigDataset(Model model,
			String firstUriSpace) {
		List<String> uriSpaces = new Vector<String>();
		uriSpaces.add(firstUriSpace);
		String query = setQuery("SELECT * WHERE {?s ?p ?o } LIMIT 1",
				firstUriSpace);
		while (true) {
			// QueryExecution execution = QueryExecutionFactory.sparqlService(
			// endpoint, query);
			QueryExecution execution = QueryExecutionFactory.create(query,
					model);
			try {
				ResultSet set = execution.execSelect();
				if (set.hasNext()) {
					Resource newResource = set.next().getResource("s");
					String newUrispaceStr = getPrefixOfURI(newResource.getURI());
					uriSpaces.add(newUrispaceStr);
					logger.info(newUrispaceStr + " is found as new urispace");
					query = setQuery(query, newUrispaceStr);
				} else {
					logger.info("Manual extraction is completed!");
					execution.close();
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.info("Manual extraction is completed!");
				execution.close();
				break;
			}
			execution.close();
		}
		return uriSpaces;
	}

	private String setQuery(String initialQuery, String uriSpace) {
		String queryStr = initialQuery;
		queryStr = queryStr.substring(0, queryStr.length() - 9)
				+ " FILTER NOT EXISTS{?s ?p ?o  FILTER regex(str(?s), \""
				+ uriSpace + "\" )}" + "} LIMIT 1";
		return queryStr;
	}

	/**
	 * This method searchs for extracting virtual linkset descriptions for given
	 * void models.
	 * 
	 * @param voidModels
	 *            {@link OntModel} instances whose virtual linkset may be
	 *            extracted.
	 * @param allVOIDModels
	 */
	public void extractVirtualLinksetDescriptions(List<OntModel> voidModels,
			List<OntModel> allVOIDModels) {
		// generate endpoint list
		List<String> consideredEndpoints = generateEndpointListFromVOIDModels(voidModels);
		for (String endpointURI : consideredEndpoints) {
			// execute query for retrieving all object prefixes from given
			// endpoint.
			List<URISpaceObject> uRISpaceObjects = getObjectResourcePrefixes(endpointURI);
			List<URISpaceObject> reducedPredicateObjects = reduceObjectURIPrefixes(
					uRISpaceObjects, allVOIDModels);
			logger.info(MessageFormat.format("Object URI prefix count: {0}",
					reducedPredicateObjects.size()));
			// get owner void ontology model of given endpoint
			OntModel endpointOwnerVOID = getVOIDOfEndpoint(voidModels,
					endpointURI);
			String ontologyURI = getOntologyURIOfModel(endpointOwnerVOID);
			// search all prefixes one by one whether it is contained in
			// URISpace property value position in any VOID.
			int linkPredicateCount = 0;
			for (URISpaceObject uriSpaceObject : reducedPredicateObjects) {

				/**
				 * add virtual dataset and linkset description to owner dataset
				 */
				// create a void individual ontology using void ontology
				// model.
				VOIDIndividualOntology indvOntOfOwnerVOID = new VOIDIndividualOntology(
						ontologyURI, endpointOwnerVOID);
				// create a virtual dataset with prefix that has no owner
				// dataset.
				Individual virtualDatasetIndv = indvOntOfOwnerVOID
						.createDataset(null, null, uriSpaceObject.getObject());
				// get main dataset individual
				Individual mainDataset = getMainDataset(indvOntOfOwnerVOID);
				Property linkPredicate = ResourceFactory
						.createProperty(uriSpaceObject.getPredicate());
				linkPredicateCount++;
				indvOntOfOwnerVOID.createLinkset(linkPredicate,
						virtualDatasetIndv, mainDataset);
				logger.info(MessageFormat
						.format("Linkset has been created with values  subjectsTarget: {0} ## linkPredicate: {1} ## objectsTarget: {2}",
								mainDataset, linkPredicate, virtualDatasetIndv));
			}
			logger.info(MessageFormat.format("Linkpredicate count: {0}",
					linkPredicateCount));
		}
	}

	/**
	 * This method reduces URISpace-predicate pairs until the endurance point
	 * and generates new redurced
	 * 
	 * @param uriSpaceObjects
	 * @param allVOIDModels
	 * @return
	 */
	private List<URISpaceObject> reduceObjectURIPrefixes(
			List<URISpaceObject> uriSpaceObjects, List<OntModel> allVOIDModels) {

		// generate URISpaceObjects that are not contained in any VOID model.
		List<URISpaceObject> eliminatedURISpaceObjects = generateNotContainedURISpaces(
				uriSpaceObjects, allVOIDModels);

		// define reduced URI space objects
		List<URISpaceObject> reducedURISpaceObjects = new ArrayList<URISpaceObject>();
		// find host objects with their count and percent values
		List<HostObject> hostObjects = generateHostObject(eliminatedURISpaceObjects);
		for (URISpaceObject uriSpaceObject : eliminatedURISpaceObjects) {
			// find host object of URISpaceObject
			HostObject foundHostObject = findHostObject(hostObjects,
					uriSpaceObject.getHostURL());
			// check whether count percent of host object is greater than
			// given percent
			if ((foundHostObject.getCountPercent() > 0.3)) {

				// explore real URI space
				String finalURISpace = exploreFinalURISpace(
						eliminatedURISpaceObjects, uriSpaceObject);

				// check whether same URISpace-predicate is found before
				if (!containsPrefix(reducedURISpaceObjects, finalURISpace,
						uriSpaceObject.getPredicate())) {
					// create new reduced URISpaceObject and add to list
					reducedURISpaceObjects.add(new URISpaceObject(
							uriSpaceObject.getPredicate(), finalURISpace));
				}
			} else {
				// if count percent smaller than given percent create same
				// URISpaceObject
				reducedURISpaceObjects.add(new URISpaceObject(uriSpaceObject
						.getPredicate(), uriSpaceObject.getObject()));
			}
		}

		return reducedURISpaceObjects;
	}

	/**
	 * This method finds real URI space of given URI
	 * 
	 * @param eliminatedURISpaceObjects
	 *            all current {@link URISpaceObject} instances to calculate
	 *            percent
	 * @param uriSpaceObject
	 * @return
	 */
	private String exploreFinalURISpace(
			List<URISpaceObject> eliminatedURISpaceObjects,
			URISpaceObject uriSpaceObject) {

		// first get URI path
		URI uri = URI.create(uriSpaceObject.getObject());
		String path = uri.getPath();

		// check whether path is null and initialize if it is null
		if (path == null) {
			path = "";
		}
		// create a tokenizer using path and cut according to Slashes
		StringTokenizer tokenizer = new StringTokenizer(path, SLASH);
		// define final URISpace
		String finalURISpace = null;
		// define a control flag
		boolean controlPercent = true;
		// define and initialize controlling URI before
		if (uriSpaceObject.getObject().endsWith("#")) {
			return uriSpaceObject.getObject();
		}
		String controllingURIBefore = uriSpaceObject.getHostURL();

		// check whether tokenizer has no token, means that path contains no "/"
		// character
		if (!tokenizer.hasMoreTokens()) {
			finalURISpace = uriSpaceObject.getObject();
		}

		// iterate while tokenizer has more tokens and contorlPercent is not
		// satisfied
		while (tokenizer.hasMoreTokens() && controlPercent) {

			// calculate URISpace number of current URISpace
			int uriSpaceNumberBefore = calculateURISpaceNumber(
					eliminatedURISpaceObjects, controllingURIBefore);

			// calculate URISpace number after adding one token
			String controllingURIAfter = controllingURIBefore + SLASH
					+ tokenizer.nextToken();
			int uriSpaceNumberAfter = calculateURISpaceNumber(
					eliminatedURISpaceObjects, controllingURIAfter);

			// calculate percent
			double decisionPoint = new Double(uriSpaceNumberAfter)
					/ new Double(uriSpaceNumberBefore);

			// control whether token is a critical point for URISpace
			if (decisionPoint < 0.1) {
				// break loop
				controlPercent = false;
				// assign final URISpace
				finalURISpace = controllingURIBefore + SLASH;
			}

			controllingURIBefore = controllingURIAfter;
		}
		return finalURISpace;
	}

	/**
	 * This method calculates count of URISpaces that starts with given
	 * objectURI
	 * 
	 * @param eliminatedURISpaceObjects
	 *            {@link URISpaceObject} list for controlling
	 * @param objectURI
	 *            object URI to be controlled
	 * @return count of contained object URI
	 */
	private int calculateURISpaceNumber(
			List<URISpaceObject> eliminatedURISpaceObjects, String objectURI) {
		int count = 0;
		for (URISpaceObject uriSpaceObject : eliminatedURISpaceObjects) {
			if (uriSpaceObject.getObject().startsWith(objectURI)) {
				count++;
			}
		}
		return count;
	}

	/**
	 * This method generates URISpaceObject that are not contained in any
	 * voidModel
	 * 
	 * @param uriSpaceObjects
	 *            {@link URISpaceObject} list to be controlled
	 * @param allVOIDModels
	 *            VOID {@link OntModel} list
	 * @return not contained {@link URISpaceObject} list
	 */
	private List<URISpaceObject> generateNotContainedURISpaces(
			List<URISpaceObject> uriSpaceObjects, List<OntModel> allVOIDModels) {
		List<URISpaceObject> notContainedInAnyVOIDModels = new ArrayList<URISpaceObject>();

		// if prefix has no owner void dataset add virtual dataset and
		// linkset description to owner dataset
		for (URISpaceObject uriSpaceObject : uriSpaceObjects) {
			if (!isPrefixContainedInAnyVOID(allVOIDModels,
					uriSpaceObject.getObject())) {
				notContainedInAnyVOIDModels.add(uriSpaceObject);
			}
		}
		return notContainedInAnyVOIDModels;
	}

	private String cutLastURIPart(URISpaceObject uriSpaceObject) {
		// if so, reduce the URISpace
		String uriSpace = uriSpaceObject.getObject();

		URI uri = URI.create(uriSpace);
		try {
			String protocol = uri.toURL().getProtocol();
			String host = uri.getHost();
			int port = uri.getPort();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		if (uriSpace.endsWith(SLASH)) {
			uriSpace = uriSpace.substring(0, uriSpace.length() - 1);
		}
		String reducedURISpace = getPrefixOfURI(uriSpace);
		return reducedURISpace;
	}

	/**
	 * Returns the prefiz of predicate.
	 * 
	 * @param URI
	 * @return
	 */
	public static String getPrefixOfURI(String URI) {
		if (URI.contains("#"))
			return URI.substring(0, URI.indexOf("#") + 1);
		else
			return URI.substring(0, URI.lastIndexOf(SLASH) + 1);
	}

	/**
	 * This method finds host object from given hostURL in given hostObject list
	 * 
	 * @param hostObjects
	 * @param hostURL
	 * @return
	 */
	private HostObject findHostObject(List<HostObject> hostObjects,
			String hostURL) {
		for (HostObject hostObject : hostObjects) {
			if (hostObject.getHostURL().equals(hostURL)) {
				return hostObject;
			}
		}
		return null;
	}

	/**
	 * This method generates host objects from given {@link URISpaceObject} list
	 * 
	 * @param uriSpaceObjects
	 * @return
	 */
	private List<HostObject> generateHostObject(
			List<URISpaceObject> uriSpaceObjects) {
		// define host object list
		List<HostObject> hostObjects = new ArrayList<HostObject>();
		for (URISpaceObject uriSpaceObject : uriSpaceObjects) {
			HostObject retrievedHostObject = findHostObject(hostObjects,
					uriSpaceObject.getHostURL());
			if (retrievedHostObject != null) {
				retrievedHostObject.increaseHostCount();
			} else {
				hostObjects.add(new HostObject(uriSpaceObject.getHostURL()));
			}
		}
		calculateCountPercents(hostObjects);
		return hostObjects;
	}

	/**
	 * This method calculates count percents of given {@link HostObject} list
	 * 
	 * @param hostObjects
	 */
	private void calculateCountPercents(List<HostObject> hostObjects) {
		// calculate total count
		int totalCount = 0;
		for (HostObject hostObject : hostObjects) {
			totalCount += hostObject.getCount();
		}
		// calculate count percents
		for (HostObject hostObject : hostObjects) {
			double countPercent = new Double(hostObject.getCount())
					/ new Double(totalCount);
			hostObject.setCountPercent(countPercent);
		}
	}

	/**
	 * This method finds ontologyURI of given {@link OntModel} instance
	 * 
	 * @param endpointOwnerVOID
	 * @return
	 */
	private String getOntologyURIOfModel(OntModel endpointOwnerVOID) {
		String indiviualURI = endpointOwnerVOID
				.listResourcesWithProperty(
						RDF.type,
						ResourceFactory
								.createResource(VOIDOntologyVocabulary.DATASET))
				.toList().get(0).asResource().getURI();
		String prefixOfURI = getPrefixOfURI(indiviualURI);
		if (!prefixOfURI.equals("")) {
			prefixOfURI = prefixOfURI.substring(0, prefixOfURI.length() - 1);
		}
		return prefixOfURI;
	}

	/**
	 * This method looks for dataset that is not same as with given virtual
	 * dataset individual and returns if finds any.
	 * 
	 * @param indvOntOfOwnerVOID
	 * @return
	 */
	private Individual getMainDataset(VOIDIndividualOntology indvOntOfOwnerVOID) {
		return indvOntOfOwnerVOID.getOntModel()
				.listSubjectsWithProperty(SPARQL_ENDPOINT_PRP).toList().get(0)
				.as(Individual.class);
	}

	/**
	 * This method gets first dataset resource has URISpace property with given
	 * prefix value
	 * 
	 * @param uriSpace
	 * @param indvOntOfOwnerVOID
	 * @return
	 */
	private Individual getDatasetWithURISpace(String uriSpace,
			VOIDIndividualOntology indvOntOfOwnerVOID) {
		List<Resource> datasetList = indvOntOfOwnerVOID
				.getOntModel()
				.listResourcesWithProperty(
						VOIDOntologyVocabulary.DATASET_uriSpace_prp, uriSpace)
				.toList();
		if (datasetList != null && !datasetList.isEmpty()) {
			return datasetList.get(0).as(Individual.class);
		}
		return null;
	}

	/**
	 * This method looks for whether given prefix is contained in given VOID
	 * models in position of URISpace.
	 * 
	 * @param voidModels
	 *            void models contain URISpace property
	 * @param prefix
	 *            URISpace property value that is looked for whether VOID models
	 *            contain it.
	 */
	private boolean isPrefixContainedInAnyVOID(List<OntModel> voidModels,
			String prefix) {
		for (OntModel voidModel : voidModels) {
			// check whether any void model contains this prefix as its URISpace
			// property value.
			if (voidModel.contains(null,
					VOIDOntologyVocabulary.DATASET_uriSpace_prp, prefix)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This method searches endpoint value in given void models and returns who
	 * owns this endpoint property value
	 * 
	 * @param voidModels
	 *            void models contain dataset sparql endpoint property
	 * @param endpointValue
	 *            endpoint value for key
	 * @return void model that has endpoint property with given endpoint value
	 */
	private OntModel getVOIDOfEndpoint(List<OntModel> voidModels,
			String endpointValue) {
		// search value on all void models
		for (OntModel voidModel : voidModels) {
			// check whether this model contains this value
			if (voidModel.contains(null,
					VOIDOntologyVocabulary.DATASET_sparqlEndpoint_prp,
					endpointValue)) {
				// return if this model contains that void model
				return voidModel;
			}
		}
		return null;
	}

	/**
	 * This method retrieves all object resource prefixes URIs from a given
	 * endpoint
	 * 
	 * @param endpointValue
	 *            endpoint value whose object URI prefixes will be retrieved.
	 */
	private List<URISpaceObject> getObjectResourcePrefixes(String endpointValue) {
		// flag indicates that to continue fetching data from given endpoint
		boolean continueBlockQuery = true;
		// offset increment value means how many rows will be retrieved once
		// from given endpoint
		int offsetIncValue = 10000;
		// offset index indicates at which row we are
		int offsetIndex = 0;
		// found object prefix list
		List<URISpaceObject> foundPredicateObjects = new ArrayList<URISpaceObject>();
		// continue while continue block query flag is active
		while (continueBlockQuery) {
			// create query
			Query query = QueryFactory
					.create("SELECT ?p ?o WHERE {?s ?p ?o} LIMIT 10000 OFFSET "
							+ offsetIndex);
			// execute query on given endpoint
			QueryExecution queryExecution = QueryExecutionFactory
					.sparqlService(endpointValue, query);
			// get resultset
			ResultSet resultSet = queryExecution.execSelect();
			// fill object URI prefix list
			fillObjectURIPrefixes(foundPredicateObjects, resultSet);

			// If getting row count slower than 20001 Query finish log...
			if (resultSet.getRowNumber() < ((offsetIncValue * 2) + 1)) {
				logger.info("Query is completed");
				continueBlockQuery = false;
			}
			logger.info(MessageFormat.format("Seeked row: {0}", offsetIndex));
			offsetIndex += offsetIncValue;
		}
		return foundPredicateObjects;
	}

	/**
	 * This method iterates on query resultset and fills found object URI prefix
	 * list given
	 * 
	 * @param foundPredicateObjects
	 *            object URI prefix list
	 * @param resultSet
	 *            resultset that contains object values.
	 */
	private void fillObjectURIPrefixes(
			List<URISpaceObject> foundPredicateObjects, ResultSet resultSet) {
		// iterate on object results
		while (resultSet.hasNext()) {
			// get object value
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			RDFNode predicateNode = querySolution.get("p");
			RDFNode objectNode = querySolution.get("o");
			// check whether retrived object node is a resource URI
			if (objectNode.isResource() && predicateNode.isResource()
					&& isAppropriatePredicate(predicateNode.as(Property.class))) {
				// get prefix of this resource
				String prefixOfURI = getPrefixOfURI(objectNode.asResource()
						.getURI());
				String predicateURI = predicateNode.as(Property.class).getURI();
				// add this prefix to list if it is not found before
				if (!containsPrefix(foundPredicateObjects, prefixOfURI,
						predicateURI)) {
					logger.info(MessageFormat.format(
							"Found object prefix: {0} with property value {1}",
							prefixOfURI, predicateURI));
					foundPredicateObjects.add(new URISpaceObject(predicateURI,
							prefixOfURI));
				}
			}
		}
	}

	private boolean isAppropriatePredicate(Property predicate) {
		String predicateURI = predicate.getURI();
		if (predicateURI.startsWith(RDFS.getURI()))
			return false;
		if (predicateURI.startsWith(RDF.getURI()))
			return false;
		if (predicateURI.startsWith(OWL.getURI()))
			return false;
		if (predicateURI.startsWith(XSD.getURI()))
			return false;
		return true;
	}

	/**
	 * This method checks whether given prefix contained by
	 * {@link URISpaceObject} list
	 * 
	 * @param uriSpaceObjects
	 * @param prefix
	 * @param predicateURI
	 * @return
	 */
	private boolean containsPrefix(List<URISpaceObject> uriSpaceObjects,
			String prefix, String predicateURI) {
		for (URISpaceObject uriSpaceObject : uriSpaceObjects) {
			if (uriSpaceObject.getObject().equals(prefix)
					&& uriSpaceObject.getPredicate().equals(predicateURI)) {
				return true;
			}
		}
		return false;

	}
}
