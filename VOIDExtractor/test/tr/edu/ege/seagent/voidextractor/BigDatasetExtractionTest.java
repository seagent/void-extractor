package tr.edu.ege.seagent.voidextractor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Vector;

import org.junit.Test;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.tdb.TDBFactory;

import tr.edu.ege.seagent.wodqa.voiddocument.VOIDCreator;
import tr.edu.ege.seagent.wodqa.voiddocument.VOIDIndividualOntology;
import tr.edu.ege.seagent.wodqa.voiddocument.VOIDOntologyVocabulary;

public class BigDatasetExtractionTest {
	@Test
	public void getDBpediaLinksetInfosTest() throws Exception {
		// open file
		File file = new File(FileOperations.DBPEDIA_DIR_NAME
				+ "dbpedia_props.txt");
		BufferedReader in = new BufferedReader(new FileReader(
				file.getAbsoluteFile()));
		String str;
		while ((str = in.readLine()) != null) {
			if (str.indexOf(Vocabulary.RDF_PREFIX) == -1) {
				Query query = QueryFactory
						.create("SELECT DISTINCT ?o WHERE {?s <" + str
								+ "> ?o FILTER isIRI(?o)  }");
				QueryExecution execution = QueryExecutionFactory.sparqlService(
						ExampleVocabulary.DBPEDIA_SPARQL_ENDPOINT, query);
				ResultSet set = execution.execSelect();
				while (set.hasNext()) {
					QuerySolution next = set.next();
					String row = str + " " + next.getResource("o").getURI()
							+ "\n";
					System.out.println(row + " eklendi");
					FileOperations.writeFile(FileOperations.DBPEDIA_DIR_NAME
							+ "dbpediaLinkset.txt", true, row);
				}
				execution.close();
			}
		}
		in.close();
	}

	@Test
	public void getAllPredicatesFromTDBTest() throws Exception {
		String directory = "/home/galaksiya-1/Desktop/TDBdumps/";
		Model model = TDBFactory.createModel(directory);
		Query query = QueryFactory
				.create("SELECT DISTINCT ?p WHERE {?s ?p ?o}");
		QueryExecution exec = QueryExecutionFactory.create(query, model);
		ResultSet set = exec.execSelect();
		while (set.hasNext()) {
			String row = set.next().getResource("p").getURI() + "\n";
			FileOperations.writeFile(FileOperations.GEODATA_DIR_NAME
					+ "geodataPredicates.txt", true, row);
		}
		exec.close();
		model.close();
	}

	@Test
	public void getAllRFDTypeIndexesFromTDBTest() throws Exception {
		String directory = "/home/galaksiya-1/Desktop/TDBdumps/";
		Model model = TDBFactory.createModel(directory);
		Query query = QueryFactory
				.create("prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT DISTINCT ?o WHERE {?s rdf:type ?o}");
		QueryExecution exec = QueryExecutionFactory.create(query, model);
		ResultSet set = exec.execSelect();
		while (set.hasNext()) {
			String row = set.next().get("o").asNode().getURI() + "\n";
			FileOperations.writeFile(FileOperations.GEODATA_DIR_NAME
					+ "geodataPredicates.txt", true, row);
		}
		exec.close();
		model.close();
	}

	@Test
	public void createGeodataVOIDTest() throws Exception {
		VOIDExtractor extractor = new VOIDExtractor(null);
		String ontURI = Vocabulary.VOID_ONTOLOGY_URI_PREFIX + "geodata";
		VOIDIndividualOntology indvOnt = new VOIDIndividualOntology(ontURI,
				ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM), null);
		// create dataset with endpoint...
		Individual geodataDataset = indvOnt.createDataset(
				"http://linkedgeodata.org/sparql", null, null);
		// get vocabularies
		File file = new File(FileOperations.GEODATA_DIR_NAME
				+ "geodataPredicates.txt");
		BufferedReader in = new BufferedReader(new FileReader(
				file.getAbsoluteFile()));
		String str;
		List<String> vocabularies = new Vector<String>();
		while ((str = in.readLine()) != null) {
			str = VOIDExtractor.getPrefixOfURI(str);
			if (!vocabularies.contains(str)) {
				vocabularies.add(str);
				indvOnt.addDatasetVocabularyProperty(geodataDataset, 

str);
			}
		}
		in.close();
		// add urispace properties...
		indvOnt.addDatasetUriSpace(geodataDataset, ResourceFactory
				.createPlainLiteral("http://linkedgeodata.org/ontology/"));
		indvOnt.addDatasetUriSpace(geodataDataset, ResourceFactory
				.createPlainLiteral("http://linkedgeodata.org/triplify/"));
		FileOperations.writeVoidModelFile(indvOnt.getOntModel(), 0,
				FileOperations.GEODATA_DIR_NAME);
		// create linksets
		List<VOIDIndividualOntology> indvOntList = new Vector<VOIDIndividualOntology>();
		List<String> linksetObjects = new Vector<String>();
		linksetObjects.add("http://dbpedia.org/resource/");
		linksetObjects.add("http://sws.geonames.org/");
		List<String> linkPredicateList = new Vector<String>();
		String linkPredicate = "http://www.w3.org/2002/07/owl#sameAs";
		linkPredicateList.add(linkPredicate);
		linkPredicateList.add(linkPredicate);
		// read small datasets' void files.
		indvOntList.addAll(extractor.readFilesIntoModel(
				FileOperations.SMALL_VOIDS_DIRECTORY_NAME));
		// read big datasets' void files.
		indvOntList.addAll(extractor.readFilesIntoModel(
				FileOperations.BIG_VOIDS_DIRECTORY_NAME));

		// find dataset that includes targeted urispace...
		List<LinkedDataset> linkedDatasets = extractor
				.searchDatasetsIncludeGivenUrispaces(indvOntList,
						linksetObjects, linkPredicateList);
		extractor.createLinksetIndividuals(geodataDataset, linkedDatasets,
				ontURI);
		// add linksets to the void model...
		FileOperations.writeVoidModelFile(indvOnt.getOntModel(), 0,
				FileOperations.GEODATA_DIR_NAME);

		assertTrue(new File("geodata/datasets0.owl").exists());
	}

	@Test
	public void createLinkedMDBVOIDTest() throws Exception {
		String linkedMDBontURI = Vocabulary.VOID_ONTOLOGY_URI_PREFIX
				+ "linkedMdb";
		VOIDIndividualOntology linkedMDBindvOnt = new VOIDIndividualOntology(
				linkedMDBontURI,
				ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM), null);
		// create dataset with endpoint...
		Individual linkedMDBDataset = linkedMDBindvOnt.createDataset(
				"http://data.linkedmdb.org/sparql", null, null);


		// manual addied vocabularies
		linkedMDBindvOnt.addDatasetVocabularyProperty(linkedMDBDataset,
				ExampleVocabulary.LINKED_MDB_VOC);
		linkedMDBindvOnt.addDatasetUriSpace(linkedMDBDataset, ResourceFactory
				.createPlainLiteral(ExampleVocabulary.LINKED_MDB_URI_SPACE));

		// create linksets...
		VOIDIndividualOntology dbpediaIndvOnt = getModelOfOWLFile("64");
		String dbpediaOntURI = dbpediaIndvOnt.getOntModel().listOntologies()
				.toList().get(0).getURI();

		VOIDCreator.createLinksets(linkedMDBindvOnt.getOntModel(),
				dbpediaIndvOnt.getOntModel(), ExampleVocabulary.OWL_SAMEAS_RSC,
				linkedMDBontURI);
		VOIDCreator.createLinksets(dbpediaIndvOnt.getOntModel(),
				linkedMDBindvOnt.getOntModel(),
				ExampleVocabulary.OWL_SAMEAS_RSC, dbpediaOntURI);
		// write model to file...
		FileOperations.writeVoidModelFile(linkedMDBindvOnt.getOntModel(), 0,
				FileOperations.MANUAL_CREATION_DIR);
		FileOperations.writeVoidModelFile(dbpediaIndvOnt.getOntModel(), 1,
				FileOperations.GIANT_VOIDS_DIRECTORY_NAME);

		assertTrue(new File(FileOperations.MANUAL_CREATION_DIR
				+ "/datasets0.owl").exists());

	}

	@Test
	public void createGeonamesVOIDTest() throws Exception {
		String geonamesOntURI = Vocabulary.VOID_ONTOLOGY_URI_PREFIX
				+ "geonames";
		VOIDIndividualOntology geonamesIndvOnt = new VOIDIndividualOntology(
				geonamesOntURI,
				ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM), null);
		// create dataset with endpoint...
		Individual linkedMDBDataset = geonamesIndvOnt.createDataset(
				"http://155.223.24.47:8895/geonames/sparql", null, null);
		// manual addied vocabularies
		geonamesIndvOnt.addDatasetVocabularyProperty(linkedMDBDataset,
				ExampleVocabulary.GEONAMES_VOC);
		geonamesIndvOnt.addDatasetVocabularyProperty(linkedMDBDataset,
				ExampleVocabulary.GEOPOSITION_VOC);
		geonamesIndvOnt.addDatasetVocabularyProperty(linkedMDBDataset,
				ExampleVocabulary.CREATIVE_COMMONS_VOC);
		geonamesIndvOnt.addDatasetVocabularyProperty(linkedMDBDataset,
				ExampleVocabulary.DC_TERMS_VOC);
		geonamesIndvOnt.addDatasetVocabularyProperty(linkedMDBDataset,
				FOAF.getURI());
		geonamesIndvOnt.addDatasetUriSpace(linkedMDBDataset, ResourceFactory
				.createPlainLiteral(ExampleVocabulary.GEONAMES_URI_SPACE));

		// create linksets...
		VOIDIndividualOntology dbpediaIndvOnt = getModelOfOWLFile("64");
		// String dbpediaOntURI = dbpediaIndvOnt.getOntModel().listOntologies()
		// .toList().get(0).getURI();

		VOIDCreator.createLinksets(geonamesIndvOnt.getOntModel(),
				dbpediaIndvOnt.getOntModel(), ExampleVocabulary.OWL_SAMEAS_RSC,
				geonamesOntURI);
		// VOIDCreator.createLinksets(dbpediaIndvOnt.getOntModel(),
		// geonamesIndvOnt.getOntModel(),
		// ExampleVocabulary.OWL_SAMEAS_RSC, dbpediaOntURI);
		// write model to file...
		FileOperations.writeVoidModelFile(geonamesIndvOnt.getOntModel(), 70,
				FileOperations.MANUAL_CREATION_DIR);
		// FileOperations.writeVoidModelFile(dbpediaIndvOnt.getOntModel(), 1,
		// FileOperations.GIANT_VOIDS_DIRECTORY_NAME);

		assertTrue(new File(FileOperations.MANUAL_CREATION_DIR
				+ "/datasets70.owl").exists());

	}

	@Test
	public void createDbpediaVOIDTest() throws Exception {
		VOIDExtractor extractor = new VOIDExtractor(null);
		String ontURI = Vocabulary.VOID_ONTOLOGY_URI_PREFIX + "dbpedia";
		VOIDIndividualOntology indvOnt = new VOIDIndividualOntology(ontURI,
				ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM), null);
		// create dataset with endpoint...
		Individual dbpediaDataset = indvOnt.createDataset(
				"http://dbpedia.org/sparql", null, null);
		// get vocabularies
		File file = new File(FileOperations.DBPEDIA_DIR_NAME
				+ "dbpedia_props.txt");
		BufferedReader in = new BufferedReader(new FileReader(
				file.getAbsoluteFile()));
		String str;
		List<String> vocabularies = new Vector<String>();
		while ((str = in.readLine()) != null) {
			str = VOIDExtractor.getPrefixOfURI(str);
			if (!vocabularies.contains(str)
					&& str.indexOf(ExampleVocabulary.DBPEDIA_ONTOLOGY_PROP) == -1
					&& str.indexOf(ExampleVocabulary.DBPEDIA_PROPERTY_PROP) == -1) {
				vocabularies.add(str);
				indvOnt.addDatasetVocabularyProperty(dbpediaDataset, str);
			}
		}
		// manual addied vocabularies
		indvOnt.addDatasetVocabularyProperty(dbpediaDataset,
				ExampleVocabulary.DBPEDIA_ONTOLOGY_PROP);
		indvOnt.addDatasetVocabularyProperty(dbpediaDataset,
				ExampleVocabulary.DBPEDIA_PROPERTY_PROP);
		vocabularies.add(ExampleVocabulary.DBPEDIA_ONTOLOGY_PROP);
		vocabularies.add(ExampleVocabulary.DBPEDIA_PROPERTY_PROP);
		in.close();
		// add urispace properties...
		indvOnt.addDatasetUriSpace(dbpediaDataset, ResourceFactory
				.createPlainLiteral(ExampleVocabulary.DBPEDIA_ONTOLOGY_PROP));
		indvOnt.addDatasetUriSpace(dbpediaDataset, ResourceFactory
				.createPlainLiteral(ExampleVocabulary.DBPEDIA_PROPERTY_PROP));
		indvOnt.addDatasetUriSpace(dbpediaDataset, ResourceFactory
				.createPlainLiteral(ExampleVocabulary.DBPEDIA_URISPACE));
		indvOnt.addDatasetUriSpace(dbpediaDataset, ResourceFactory
				.createPlainLiteral("http://mpii.de/yago/resource/"));
		indvOnt.addDatasetUriSpace(
				dbpediaDataset,
				ResourceFactory
						.createPlainLiteral("http://www4.wiwiss.fu-berlin.de/flickrwrappr/photos/"));
		FileOperations.writeVoidModelFile(indvOnt.getOntModel(), 1,
				FileOperations.DBPEDIA_DIR_NAME);

		// get referenced objects from dbpediaLinkset.txt for creating linksets.
		// Format of each row is "predicate object".
		in = new BufferedReader(
				new FileReader(new File(FileOperations.DBPEDIA_DIR_NAME
						+ "dbpediaLinkset.txt").getAbsolutePath()));
		List<String> referencedObjects = new Vector<String>();
		List<String> referencedPredicates = new Vector<String>();
		while ((str = in.readLine()) != null) {
			String[] split = str.split(" ");
			String predicate = split[0];
			String object = split[1];
			object = VOIDExtractor.getPrefixOfURI(object);
			// set object for geodata
			if (object.indexOf(ExampleVocabulary.GEODATA_EXAMPLE_RESOURCE_URI) > -1)
				object = ExampleVocabulary.GEODATA_EXAMPLE_RESOURCE_URI;
			// set object for geodata
			if (object.indexOf(ExampleVocabulary.DBPEDIA_URISPACE) > -1)
				object = ExampleVocabulary.DBPEDIA_URISPACE;
			// set object for geodata
			if (object.indexOf(ExampleVocabulary.WIKIMEDIA_URISPACE) > -1)
				object = ExampleVocabulary.WIKIMEDIA_URISPACE;

			if (!referencedObjects.contains(object)) {
				try {
					ResourceFactory.createProperty(predicate);
					referencedObjects.add(object);
					referencedPredicates.add(predicate);
				} catch (Exception e) {
					System.out.println(predicate + " is invalid property!");
				}
			} else {
				try {
					ResourceFactory.createProperty(predicate);
					int index = referencedObjects.indexOf(object);
					if (referencedPredicates.get(index).indexOf(predicate) == -1)
						referencedPredicates.set(index,
								referencedPredicates.get(index) + " "
										+ predicate);
				} catch (Exception e) {
					System.out.println(predicate + " is invalid property!");
				}
			}
		}
		in.close();

		// create linksets
		List<VOIDIndividualOntology> indvOntList = new Vector<VOIDIndividualOntology>();
		// read small datasets' void files.
		indvOntList.addAll(extractor.readFilesIntoModel(
				FileOperations.SMALL_VOIDS_DIRECTORY_NAME));
		// read big datasets' void files.
		indvOntList.addAll(extractor.readFilesIntoModel(
				FileOperations.BIG_VOIDS_DIRECTORY_NAME));

		// find dataset that includes targeted urispace...
		List<LinkedDataset> linkedDatasets = extractor
				.searchDatasetsIncludeGivenUrispaces(indvOntList,
						referencedObjects, referencedPredicates);
		// create linksets for dbpedia dataset
		extractor.createLinksetIndividuals(dbpediaDataset, linkedDatasets,
				ontURI);
		// add linksets to the void model...
		FileOperations.writeVoidModelFile(indvOnt.getOntModel(), 1,
				FileOperations.DBPEDIA_DIR_NAME);

		assertTrue(new File(FileOperations.DBPEDIA_DIR_NAME + "datasets0.owl")
				.exists());

	}

	@Test
	public void readFilesFromModelTest() throws Exception {
		List<VOIDIndividualOntology> readFilesIntoModel = new VOIDExtractor(
				null).readFilesIntoModel(
				FileOperations.SMALL_VOIDS_DIRECTORY_NAME);
		System.out.println(readFilesIntoModel.size());

	}

	@Test
	public void getLinksetItemsListTest() throws Exception {
		// get linkset infos for smallvoids and bigvoids
		VOIDExtractor extractor = new VOIDExtractor(null);
		List<List<String>> linksetItemList = extractor.getLinksetItemList(
				FileOperations.SMALL_VOIDS_DIRECTORY_NAME, true, -1);
		assertEquals(46, linksetItemList.size());
		linksetItemList = extractor.getLinksetItemList(
				FileOperations.BIG_VOIDS_DIRECTORY_NAME, false, 17);
		assertEquals(17, linksetItemList.size());
	}

	/**
	 * It creates all voids' linksets into their ontmodels. Urispaces,
	 * vocabularies and linkset infos should be extracted before.
	 * 
	 * @throws Exception
	 */
	@Test
	public void createLinksetsForAllDatasetsTest() throws Exception {
		List<VOIDIndividualOntology> allvoids = new Vector<VOIDIndividualOntology>();
		// read void models from small and big voids and
		VOIDExtractor extractor = new VOIDExtractor(null);
		allvoids.addAll(extractor.readFilesIntoModel(
				FileOperations.SMALL_VOIDS_DIRECTORY_NAME));
		allvoids.addAll(extractor.readFilesIntoModel(
				FileOperations.BIG_VOIDS_DIRECTORY_NAME));
		allvoids.addAll(extractor.readFilesIntoModel(
				FileOperations.GIANT_VOIDS_DIRECTORY_NAME));

		// get linkset infos for smallvoids and bigvoids
		List<List<String>> linksetItemList = new Vector<List<String>>();
		linksetItemList.addAll(extractor.getLinksetItemList(
				FileOperations.SMALL_VOIDS_DIRECTORY_NAME, true, -1));
		linksetItemList.addAll(extractor.getLinksetItemList(
				FileOperations.BIG_VOIDS_DIRECTORY_NAME, false, 17));
		// add these for dbpedia and geodata.
		linksetItemList.add(new Vector<String>());
		linksetItemList.add(new Vector<String>());
		System.out.println(allvoids.size());
		System.out.println(linksetItemList.size());
		extractor.extractLinksetDescriptions(linksetItemList, allvoids,
				FileOperations.COMPLETE_VOIDS);
	}

	/**
	 * It creates linksets between geodata and dbpedia.
	 * 
	 * @throws Exception
	 */
	@Test
	public void createLinksetBetweenDBpediaAndGeodata() throws Exception {
		// get dbpedia model...
		VOIDIndividualOntology dbpediaIndvOnt = getModelOfOWLFile("64");
		VOIDIndividualOntology geodataIndvOnt = getModelOfOWLFile("63");
		Individual dbpediaDataset = dbpediaIndvOnt.listDatasets().get(0);
		String dbpediaOntURI = dbpediaDataset.getOntModel().listOntologies()
				.toList().get(0).getURI();
		Individual geodataDataset = geodataIndvOnt.listDatasets().get(0);
		String geodataOntURI = geodataDataset.getOntModel().listOntologies()
				.toList().get(0).getURI();
		// create linksets
		List<LinkedDataset> linkedDatasets = new Vector<LinkedDataset>();
		// create linkset from dbpedia to geodata into dbpedia.
		linkedDatasets.add(new LinkedDataset(geodataDataset,
				ExampleVocabulary.OWL_SAMEAS_RSC));
		new VOIDExtractor(null).createLinksetIndividuals(dbpediaDataset,
				linkedDatasets, dbpediaOntURI);
		FileOperations.writeVoidModelFile(dbpediaIndvOnt.getOntModel(), 64,
				FileOperations.COMPLETE_VOIDS);
		linkedDatasets.clear();

		// create linkset from geodata to dbpedia into geodata.
		linkedDatasets.add(new LinkedDataset(dbpediaDataset,
				ExampleVocabulary.OWL_SAMEAS_RSC));
		new VOIDExtractor(null).createLinksetIndividuals(geodataDataset,
				linkedDatasets, geodataOntURI);
		FileOperations.writeVoidModelFile(geodataIndvOnt.getOntModel(), 63,
				FileOperations.COMPLETE_VOIDS);

	}

	@Test
	public void addTripleCountToAllVoidsTest() throws Exception {
		VOIDExtractor extractor = new VOIDExtractor(null);
		List<VOIDIndividualOntology> voidIndvOntList = extractor
				.readFilesIntoModel(FileOperations.COMPLETE_VOIDS);
		assertEquals(67, voidIndvOntList.size());
		addTripleCountToGivenModels(voidIndvOntList);
		List<Statement> tripleCountStatements = voidIndvOntList
				.get(0)
				.getOntModel()
				.listStatements(null,
						VOIDOntologyVocabulary.DATASET_triples_prp,
						(RDFNode) null).toList();
		assertTrue(tripleCountStatements.get(0).getObject().asLiteral()
				.getLong() > 50000);
		for (int i = 0; i < voidIndvOntList.size(); i++) {
			FileOperations.writeVoidModelFile(voidIndvOntList.get(i)
					.getOntModel(), i, FileOperations.COMPLETE_VOIDS);
		}

	}

	private void addTripleCountToGivenModels(
			List<VOIDIndividualOntology> voidIndvOntList) {
		for (VOIDIndividualOntology voidIndividualOntology : voidIndvOntList) {
			Statement statement = voidIndividualOntology
					.getOntModel()
					.listStatements(null,
							VOIDOntologyVocabulary.DATASET_sparqlEndpoint_prp,
							(RDFNode) null).toList().get(0);
			Individual dataset = statement.getSubject().as(Individual.class);
			String sparqlEndpoint = statement.getObject().asLiteral()
					.getString().toString();
			Query query = QueryFactory
					.create("prefix void: <http://rdfs.org/ns/void#> SELECT ?count WHERE {?dataset void:sparqlEndpoint <"
							+ sparqlEndpoint
							+ ">. ?dataset void:triples ?count.}");
			QueryExecution exec = QueryExecutionFactory.sparqlService(
					OnlineVoidStores.DSI_LOD, query);
			ResultSet result = exec.execSelect();
			if (result.hasNext()) {
				long tripleCount = result.next().getLiteral("count").getLong();
				voidIndividualOntology.setDatasetTriples(dataset, tripleCount);
				System.out.println("It is added: " + sparqlEndpoint + " "
						+ tripleCount);
			} else {
				System.out.println(sparqlEndpoint + " has not triple count on "
						+ OnlineVoidStores.DSI_LOD);
				voidIndividualOntology.setDatasetTriples(dataset, 100000);
			}
			exec.close();
		}
	}

	/**
	 * It transforms owl file into ontmodel.
	 * 
	 * @param voidIndex
	 * @return
	 * @throws MalformedURLException
	 */
	private VOIDIndividualOntology getModelOfOWLFile(String voidIndex)
			throws MalformedURLException {
		String filePath = FileOperations.COMPLETE_VOIDS + "/datasets"
				+ voidIndex + ".owl";
		File voidFile = new File(filePath);
		OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		model.read(voidFile.toURI().toURL().toString());
		String ontURI = model.listOntologies().toList().get(0).getURI();
		return new VOIDIndividualOntology(ontURI, model);
	}

	@Test
	public void endpointAvailableTest() throws Exception {
		Query query = QueryFactory.create("SELECT * WHERE {?s ?p ?o} LIMIT 1");
		QueryExecution exec = QueryExecutionFactory.sparqlService(
				"http://sparql.linkedopendata.it/musei", query);
		ResultSetFormatter.out(exec.execSelect());
	}

	@Test
	public void importVoidTest() throws Exception {
		VOIDIndividualOntology modelOfOWLFile = getModelOfOWLFile("63");
		List<Individual> list = modelOfOWLFile.getOntModel()
				.listIndividuals(VOIDOntologyVocabulary.DATASET_rsc).toList();
		System.out.println(list);
	}
}
