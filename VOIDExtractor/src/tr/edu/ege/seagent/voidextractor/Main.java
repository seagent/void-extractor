package tr.edu.ege.seagent.voidextractor;

import java.util.List;

import tr.edu.ege.seagent.wodqa.voiddocument.ExampleVocabulary;
import tr.edu.ege.seagent.wodqa.voiddocument.VOIDIndividualOntology;

public class Main {

	private static String[] uriSpaces = { ExampleVocabulary.NYTIMES_URI_SPACE, ExampleVocabulary.DRUGBANK_URI_SPACE,
			ExampleVocabulary.DBPEDIA_URISPACE, ExampleVocabulary.SW_DOGFOOD_URI_SPACE,
			ExampleVocabulary.JAMENDO_URI_SPACE, ExampleVocabulary.KEGG_URI_SPACE, ExampleVocabulary.CHEBI_URI_SPACE,
			ExampleVocabulary.GEONAMES_URI_SPACE, ExampleVocabulary.LINKED_MDB_URI_SPACE,
			ExampleVocabulary.SP2B_URI_SPACE };

	private static String[] endpoints = { "http://localhost:8000/sparql/", "http://localhost:7000/sparql/",
			"http://localhost:5500/sparql/", "http://localhost:5000/sparql/", "http://localhost:4000/sparql/",
			"http://localhost:3000/sparql/", "http://localhost:2000/sparql/", "http://localhost:2500/sparql/",
			"http://localhost:3500/sparql/" };

	public static void main(String[] args) throws Exception {
//		String query = "SELECT DISTINCT ?o WHERE {?s <"+OWL.sameAs.getURI()
//				+ "> ?o. FILTER REGEX (str(?o),\"^http://data.linkedmdb.org/resource/\")}";
//		ResultSet resultSet = QueryExecutionFactory.sparqlService("http://localhost:7000/sparql/", query).execSelect();
//		ResultSetFormatter.out(resultSet);

		// FIXME: SELECT * WHERE {?s ?p ?o } LIMIT 100000 OFFSET 14200000 sorgusunda
		// sıkıntı var
		// VOIDExtractor voidExtractor = new VOIDExtractor(Arrays.asList(endpoints));
		// voidExtractor.performVOIDAnalyzeAllScenario("experimental");
		updateLinkPredicatesOfAllfDatasets();
	}

	public static void updateLinkPredicatesOfAllfDatasets() throws Exception {
		List<VOIDIndividualOntology> allVoidIndvModels = new VOIDExtractor(null).readFilesIntoModel("allVoids/cleansed/09datasets");
		LinkPredicateExtractor linkPredicateExtractor = new LinkPredicateExtractor();
		int i = 9;
		// for (int i = 0; i < allVoidIndvModels.size(); i++) {

		VOIDIndividualOntology voidIndividualOntology = allVoidIndvModels.get(i);
		linkPredicateExtractor.createLinksetForGivenIndividualOntology(allVoidIndvModels, uriSpaces, i);

		// write updated void model into file after all.
		FileOperations.writeVoidModelFile(voidIndividualOntology.getOntModel(), i, "exp-linked");
		// }
	}

	// public static void extractLinksetsForAllEndpoint(VOIDExtractor extractor)
	// throws Exception {
	// /**
	// * Extract void of given dataset
	// */
	// // define endpoint of dataset that its void to be extracted.
	// String endpoint = "http://155.223.24.47:8896/jamendo/sparql";
	// long firstTime = System.currentTimeMillis();
	// List<String> endpointList = new Vector<String>();
	// // add endpoint to list
	// endpointList.add(endpoint);
	// extractor.setEndpointList(endpointList);
	// // generate void of given endpoint
	// extractor.extractOneEndpoint(endpointList, false,
	// FileOperations.MANUAL_CREATION_DIR, 0, 72);
	// // check contructed void size
	// assertEquals(1, extractor.getVocabulariesList().size());
	// System.out.println("Time :" + (System.currentTimeMillis() - firstTime));
	//
	// /**
	// * extract linksets...
	// */
	// List<VOIDIndividualOntology> indvOntListOnManual = extractor
	// .readFilesIntoModel(FileOperations.MANUAL_CREATION_DIR);
	// VOIDIndividualOntology nyTimesIndvOnt = indvOntListOnManual.get(0);
	//
	// // create linksets
	// List<VOIDIndividualOntology> indvOntList = new
	// Vector<VOIDIndividualOntology>();
	// // read small datasets' void files.
	// indvOntList.addAll(extractor
	// .readFilesIntoModel(FileOperations.COMPLETE_VOIDS));
	// System.out.println(indvOntList.size());
	//
	// List<String> givenUrispaces = new Vector<String>();
	// List<String> linkpredicateList = new Vector<String>();
	//
	// for (String string : extractor.getLinksetsList().get(0)) {
	// String object = string.substring(string.lastIndexOf("**") + 2);
	// String predicate = string.substring(string.indexOf("**") + 2,
	// string.lastIndexOf("**"));
	// if (!givenUrispaces.contains(object)) {
	// givenUrispaces.add(object);
	// linkpredicateList.add(predicate + " ");
	// } else {
	// int index = givenUrispaces.indexOf(object);
	// linkpredicateList.set(index, linkpredicateList.get(index)
	// + predicate + " ");
	// }
	// }
	//
	// List<LinkedDataset> linkedDatasets = extractor
	// .searchDatasetsIncludeGivenUrispaces(indvOntList,
	// givenUrispaces, linkpredicateList);
	// System.out.println(linkedDatasets.size());
	// extractor.createLinksetIndividuals(
	// nyTimesIndvOnt.listDatasets().get(0), linkedDatasets,
	// nyTimesIndvOnt.getOntModel().listOntologies().toList().get(0)
	// .getURI());
	// FileOperations.writeVoidModelFile(nyTimesIndvOnt.getOntModel(), 250,
	// FileOperations.MANUAL_CREATION_DIR);
	//
	// }

}
