package tr.edu.ege.seagent.wodqa.evaluation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.net.MalformedURLException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import tr.edu.ege.seagent.voidextractor.FileOperations;
import tr.edu.ege.seagent.voidextractor.VOIDExtractor;
import tr.edu.ege.seagent.wodqa.query.analyzer.QueryAnalyzer;
import tr.edu.ege.seagent.wodqa.query.analyzer.RelevantType;
import tr.edu.ege.seagent.wodqa.voiddocument.VOIDIndividualOntology;

public class AnalyzeWithAskOptimizationTest {

	private Model mainModel;

	// sample problematic query for ask optimization
	private static final String simpleQuery = "PREFIX foaf:" + "<"
			+ FOAF.getURI()
			+ ">"
			+ "PREFIX rdf:"
			+ "<"
			+ RDF.getURI()
			+ ">"
			+ "PREFIX owl:"
			+ "<"
			+ OWL.getURI()
			+ ">"
			+ "PREFIX rdfs:"
			+ "<"
			+ RDFS.getURI()
			+ ">"
			+ "SELECT "
			+ "?movieLabelLMDB "
			+ "?directorLMDB "
			+ "?dbpMovie "
			+ "WHERE { "
			+ "{<http://dbpedia.org/resource/Amy_Irving> <http://dbpedia.org/property/spouse> ?directorDBP. "
			+ "?directorDBP owl:sameAs ?directorLMDB. "
			+ "?lmdbMovie <http://data.linkedmdb.org/resource/movie/producer> ?directorLMDB. "
			+ "?lmdbMovie rdfs:label ?movieLabelLMDB."
			+ "?dbpMovie <http://dbpedia.org/ontology/director> ?directorDBP."
			+ "?dbpMovie foaf:name '?movieLabelLMDB'@en."
			+ "?lmdbMovie <http://data.linkedmdb.org/resource/movie/runtime> ?runtime."
			+ "FILTER (?runtime>100)."
			+ "OPTIONAL {?dbpMovie <http://dbpedia.org/ontology/editing> ?directorDBP.}"
			+ "}" + "}";

	/**
	 * This method initialize allVoidModels used in tests
	 * 
	 * @throws MalformedURLException
	 */
	@Before
	public void before() throws MalformedURLException {
		List<VOIDIndividualOntology> readModels = new VOIDExtractor(null)
				.readFilesIntoModel(FileOperations.TEST_VOIDS);
		for (VOIDIndividualOntology voidIndividualOntology : readModels) {
			mainModel.add(voidIndividualOntology.getOntModel());
		}
	}

	/**
	 * This method controls analyzing given problematic query correctly.
	 * 
	 * @throws Exception
	 */
	@Test
	public void anaylzeQueryWithAskOptimization() throws Exception {
		// creating a new QueryAnalyzer to analyze query
		QueryAnalyzer queryAnalyzer = new QueryAnalyzer(mainModel);
		// analyze query with optmization
		queryAnalyzer.analyze(simpleQuery);
		// check problematic triple pattern setted empty after anaylsis.
		checkTripleProblematicPattern(queryAnalyzer, 5);
	}

	/**
	 * This method checks problematic triple pattern is set empty after
	 * analyzing with ASK optimization.
	 * 
	 * @param queryAnalyzer
	 * @param triplePatternIndex
	 */
	private void checkTripleProblematicPattern(QueryAnalyzer queryAnalyzer,
			int triplePatternIndex) {
		// check triplePackList
		for (int i = 0; i < queryAnalyzer.getTriplePackList()
				.get(triplePatternIndex).getCurrentRelevantDatasets().size(); i++) {
			assertNull(queryAnalyzer.getTriplePackList()
					.get(triplePatternIndex).getCurrentRelevantDatasets()
					.get(i).getURI());
			assertEquals(
					queryAnalyzer.getTriplePackList().get(triplePatternIndex)
							.getCurrentRelevantTypes().get(i),
					RelevantType.EMPTY);
		}
		// check voidPathSolutions
		queryAnalyzer.getVoidPathSolutions().get(triplePatternIndex).isEmpty();
	}

}
