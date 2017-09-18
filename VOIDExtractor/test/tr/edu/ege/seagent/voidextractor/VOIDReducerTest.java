package tr.edu.ege.seagent.voidextractor;

import static org.junit.Assert.assertEquals;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.util.FileUtils;

import tr.edu.ege.seagent.dataset.vocabulary.VOIDIndividualOntology;
import tr.edu.ege.seagent.dataset.vocabulary.VOIDOntologyVocabulary;

public class VOIDReducerTest {

	private List<OntModel> voids;

//	 @Before
	public void before() throws Exception {
		readVoidModels();
	}

	private void readVoidModels() throws Exception {
		voids = new ArrayList<OntModel>();
		List<VOIDIndividualOntology> readModels = new VOIDExtractor(null)
				.readFilesIntoModel(FileOperations.AVAILABLE_VOIDS);
		for (VOIDIndividualOntology voidIndividualOntology : readModels) {
			voids.add(voidIndividualOntology.getOntModel());
		}
	}

//	 @Test
	public void reduceVOIDs() throws Exception {

		VOIDReducer voidReducer = new VOIDReducer();
		voidReducer.reduceVOIDs(voids);
	}

	/**
	 * This test reduces uriSpaces of given dataset according to given uriSpace
	 * 
	 * @throws Exception
	 */
	@Test
	public void reduceVOIDUrispace() throws Exception {
		VOIDReducer voidReducer = new VOIDReducer();
		// uriSpace that is reduce base
		String uriSpace = "http://keithalexander.co.uk/pbac/source/";
		// void file name
		String fileName = "/datasets27.owl";
		// read void model
		OntModel datasetDescModel = readVoidModel(fileName);
		// reduce voids
		voidReducer.reduceVoid(datasetDescModel, uriSpace,
				VOIDOntologyVocabulary.DATASET_uriSpace_prp);
		// check whether reducing is successful
		checkForReduce(datasetDescModel, uriSpace,
				VOIDOntologyVocabulary.DATASET_uriSpace_prp);
		// write reduced model into new file
		datasetDescModel.write(new FileWriter(FileOperations.DEFECTED_VOIDS
				+ fileName));
		// assertTrue(datasetDescModel.contains(null,
		// VOIDOntologyVocabulary.DATASET_uriSpace_prp,
		// ResourceFactory.createPlainLiteral(uriSpace)));
	}

	/**
	 * This test reduces uriSpaces of given dataset according to hops in URI
	 * 
	 * @throws Exception
	 */
	@Test
	public void reduceVOIDAccordingToHops() throws Exception {
		VOIDReducer voidReducer = new VOIDReducer();
		// uriSpace that is reduce base
		String uriSpace = "http://www.usa.canon.com/";
		// void file name
		String fileName = "/datasets10.owl";
		// read void model
		OntModel datasetDescModel = readVoidModel(fileName);
		// reduce voids
		voidReducer.reduceVoid(datasetDescModel, uriSpace,
				VOIDOntologyVocabulary.DATASET_uriSpace_prp, 1);
		// check whether reducing is successful
		checkForReduce(datasetDescModel, uriSpace,
				VOIDOntologyVocabulary.DATASET_uriSpace_prp, 1);
		// write reduced model into new file
		datasetDescModel.write(new FileWriter(FileOperations.DEFECTED_VOIDS
				+ fileName));
		// assertTrue(datasetDescModel.contains(null,
		// VOIDOntologyVocabulary.DATASET_uriSpace_prp,
		// ResourceFactory.createPlainLiteral(uriSpace)));
	}

	private void checkForReduce(OntModel datasetDescModel, String URI,
			Property property, int hopCount) {
		// list statements
		List<Statement> stmtList = datasetDescModel.listStatements(null,
				property, (RDFNode) null).toList();
		int containingHopCount = 0;
		// iterate on statements
		for (Statement statement : stmtList) {
			// get urispace object of statement
			String UriContained = statement.getObject().asLiteral().getString();
			// check whether this urispace object is starting with given
			// reducedURISpace
			if (UriContained.startsWith(URI) && !UriContained.equals(URI)) {
				containingHopCount = countHops(URI, UriContained);
				assertEquals(hopCount, containingHopCount);
			}
		}
	}

	/**
	 * This method counts hops of remaining part
	 * 
	 * @param URI
	 * @param UriContained
	 * @return
	 */
	private int countHops(String URI, String UriContained) {
		int containingHopCount = 0;

		String remainingPart = UriContained.split(URI)[1];
		char[] charArray = remainingPart.toCharArray();
		for (char character : charArray) {
			if (character == '/' || character == '#') {
				containingHopCount++;
			}
		}
		return containingHopCount;
	}

	/**
	 * This test reduces vocabularies of given dataset according to given
	 * vocabulary
	 * 
	 * @throws Exception
	 */
	@Test
	public void reduceVOIDVocabulary() throws Exception {
		VOIDReducer voidReducer = new VOIDReducer();
		// uriSpace that is reduce base
		String vocabulary = "http://el.dbpedia.org/DAV/home/";
		// void file name
		String fileName = "/datasets55.owl";
		// read void model
		OntModel datasetDescModel = readVoidModel(fileName);
		// reduce voids
		voidReducer.reduceVoid(datasetDescModel, vocabulary,
				VOIDOntologyVocabulary.DATASET_vocabulary_prp);
		// check whether reducing is successful
		checkForReduce(datasetDescModel, vocabulary,
				VOIDOntologyVocabulary.DATASET_vocabulary_prp);
		// write reduced model into new file
		datasetDescModel.write(new FileWriter(FileOperations.DEFECTED_VOIDS
				+ fileName));
		// assertTrue(datasetDescModel.contains(null,
		// VOIDOntologyVocabulary.DATASET_uriSpace_prp,
		// ResourceFactory.createPlainLiteral(uriSpace)));
	}

	/**
	 * This method reads {@link OntModel} under the given file path
	 * 
	 * @param fileName
	 * @return
	 * @throws FileNotFoundException
	 */
	private OntModel readVoidModel(String fileName)
			throws FileNotFoundException {
		OntModel datasetDescModel = (OntModel) ModelFactory
				.createOntologyModel().read(
						new BufferedInputStream(new FileInputStream(
								FileOperations.DEFECTED_VOIDS + fileName)),
						FileUtils.langXMLAbbrev);
		return datasetDescModel;
	}

	/**
	 * It checks whether given uriSpace contained only once in given void and
	 * any other urispace is not starting with it.
	 * 
	 * @param datasetDescModel
	 * @param URI
	 * @param property
	 *            TODO
	 */
	private void checkForReduce(OntModel datasetDescModel, String URI,
			Property property) {
		// list statements
		List<Statement> stmtList = datasetDescModel.listStatements(null,
				property, (RDFNode) null).toList();
		int containingCount = 0;
		// iterate on statements
		for (Statement statement : stmtList) {
			// get urispace object of statement
			String UriContained = statement.getObject().asLiteral().getString();
			// check whether this urispace object is starting with given
			// reducedURISpace
			if (UriContained.startsWith(URI)) {
				containingCount++;
			}
		}
		// check this uriSpace is containing only once.
		assertEquals(1, containingCount);
	}

}
