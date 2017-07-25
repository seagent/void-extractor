package tr.edu.ege.seagent.wodqa.evaluation;

import org.junit.Test;

import tr.edu.ege.seagent.boundarq.unionbound.QueryEngineUnion;
import tr.edu.ege.seagent.boundarq.util.Constants;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class FourStoreTest {

	@Test
	public void fourStoreNytimesTest() throws Exception {
		String query = "SELECT * WHERE {"
				+ "SERVICE<http://localhost:8000/sparql/>{"
				+ "?s <http://www.w3.org/2002/07/owl#sameAs> ?o" + "}"
				+ "SERVICE<http://localhost:8000/sparql/>{"
				+ "?s <http://www.w3.org/2004/02/skos/core#prefLabel> ?q" + "}"
				+ "}";
		QueryEngineUnion.register();
		QueryExecution queryExecution = QueryExecutionFactory.create(query,
				ModelFactory.createDefaultModel());
		queryExecution.getContext().put(Constants.UNION_SIZE_SYMBOL, 1000);
		ResultSet resultSet = queryExecution.execSelect();
		long timeMillisBefore = System.currentTimeMillis();
		while (resultSet.hasNext()) {
			QuerySolution querySolution = resultSet.next();
			System.out.println(querySolution);
		}
		long timeMillisAfter = System.currentTimeMillis();
		System.out.println(timeMillisAfter - timeMillisBefore);
	}
	@Test
	public void readFile() throws Exception {
		OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		model.read("allVoids/cleansed/09datasets/datasets64.owl");
		model.write(System.out);
	}
}
