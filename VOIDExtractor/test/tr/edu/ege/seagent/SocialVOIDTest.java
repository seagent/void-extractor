package tr.edu.ege.seagent;

import static org.junit.Assert.*;

import org.junit.Test;

import tr.edu.ege.seagent.wodqa.query.WodqaEngine;

import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;

public class SocialVOIDTest {
	@Test
	public void testSocialVoids() throws Exception {
		Model bankModel = FileManager.get().loadModel("/home/etmen/bank.rdf");
		Model socialModel = FileManager.get().loadModel(
				"/home/etmen/social.rdf");
		Model mainModel = ModelFactory.createDefaultModel();
		mainModel.add(socialModel);
		mainModel.add(bankModel);
		WodqaEngine wodqaEngine = new WodqaEngine(true);
		String query = "SELECT  * WHERE { <http://seagentdev.ege.edu.tr:8180/resource/sociallinker/YjzgAq%2BTTmMnlV2szbtF5ANT> ?pf1 ?of1 . ?of1 ?pf2 <http://example.com/bank/resource/Person2> FILTER ( ( ( ( ( ?pf1 != <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ) && ( ?pf2 != <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ) ) && ( ! isLiteral(?of1) ) ) && ( ?of1 != <http://seagentdev.ege.edu.tr:8180/resource/sociallinker/YjzgAq%2BTTmMnlV2szbtF5ANT> ) ) && ( ?of1 != <http://example.com/bank/resource/Person2> ) ) }  LIMIT   10";
		String serializedRawQuery = QueryFactory.create(query).serialize();
		System.out.println(serializedRawQuery);
		String federatedQuery = wodqaEngine.federateQuery(mainModel, query,
				true);
		String serializedFederatedQuery = QueryFactory.create(federatedQuery)
				.serialize();
		System.out
				.println("\n\n###########################################################################################\n");
		System.out.println(serializedFederatedQuery);
	}

	@Test
	public void testName() throws Exception {
		String queryToSend = "SELECT * WHERE { SERVICE <http://seagentdev.ege.edu.tr:8180/sociallinker/service/distquery> { <http://seagentdev.ege.edu.tr:8180/resource/sociallinker/YjzgAq%2BTTmMnlV2szbtF5ANT> ?pf1 ?of1 . ?of1 ?pf2 ?of2 . ?of2 ?pf3 ?middle . <http://seagentdev.ege.edu.tr:8180/resource/sociallinker/ikwKiILQGNtLKni%2BhQFWXG6Z> ?ps1 ?middle . FILTER ((?pf1 != <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ) && (?pf2 != <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ) && (?pf3 != <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ) && (?ps1 != <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ) && (!isLiteral(?middle)) && (?middle != <http://seagentdev.ege.edu.tr:8180/resource/sociallinker/YjzgAq%2BTTmMnlV2szbtF5ANT> ) && (?middle != <http://seagentdev.ege.edu.tr:8180/resource/sociallinker/ikwKiILQGNtLKni%2BhQFWXG6Z> ) && (?middle != ?of1 ) && (?middle != ?of2 ) && (!isLiteral(?of1)) && (?of1 != <http://seagentdev.ege.edu.tr:8180/resource/sociallinker/YjzgAq%2BTTmMnlV2szbtF5ANT> ) && (?of1 != <http://seagentdev.ege.edu.tr:8180/resource/sociallinker/ikwKiILQGNtLKni%2BhQFWXG6Z> ) && (?of1 != ?middle ) && (?of1 != ?of2 ) && (!isLiteral(?of2)) && (?of2 != <http://seagentdev.ege.edu.tr:8180/resource/sociallinker/YjzgAq%2BTTmMnlV2szbtF5ANT> ) && (?of2 != <http://seagentdev.ege.edu.tr:8180/resource/sociallinker/ikwKiILQGNtLKni%2BhQFWXG6Z> ) && (?of2 != ?middle ) && (?of2 != ?of1 ) ). }} LIMIT 10";
		// boolean hasNext = QueryExecutionFactory
		// .sparqlService(
		// "http://seagentdev.ege.edu.tr:8180/sociallinker/sparql",
		// queryToSend).execSelect().hasNext();
		// assertFalse(hasNext);

		assertFalse(QueryExecutionFactory
				.create(queryToSend, ModelFactory.createDefaultModel())
				.execSelect().hasNext());
	}
}
