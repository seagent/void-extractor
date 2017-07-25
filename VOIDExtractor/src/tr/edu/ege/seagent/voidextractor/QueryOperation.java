package tr.edu.ege.seagent.voidextractor;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

public class QueryOperation {

	public static Logger logger = Logger.getLogger(QueryOperation.class);

	/**
	 * It calculates the triple count from the given endpoint.
	 * 
	 * @param endpoint
	 * @return
	 * @throws Exception
	 */
	public static long getTripleCount(String endpoint) throws Exception {
		try {
			Query countQuery = QueryFactory
					.create("SELECT (COUNT(*) AS ?no) WHERE {?s ?p ?o}");
			QueryExecution execution = QueryExecutionFactory.sparqlService(
					endpoint, countQuery);
			ResultSet countResult = execution.execSelect();

			QuerySolution solution = countResult.next();
			return Long.parseLong(solution.get("no").asLiteral().getString()
					.toString());
		} catch (Exception e) {
			logger.error("Dataset that is the owner of " + endpoint
					+ " count doesn't calculated");
			e.printStackTrace();
			throw new Exception("Dataset that is the owner of " + endpoint
					+ " count doesn't calculated");
		}
	}

}
