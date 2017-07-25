package tr.edu.ege.seagent.wodqa.evaluation;

import static org.junit.Assert.assertFalse;

import java.io.FileWriter;
import java.net.MalformedURLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import tr.edu.ege.seagent.voidextractor.FileOperations;
import tr.edu.ege.seagent.wodqa.exception.InactiveEndpointException;
import tr.edu.ege.seagent.wodqa.exception.QueryHeaderException;
import tr.edu.ege.seagent.wodqa.query.WodqaEngine;
import tr.edu.ege.seagent.wodqa.voiddocument.VoidModelConstructor;

import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sparql.core.DatasetImpl;

public abstract class AbstractEvaluationTest {

	public VoidModelConstructor constructor;

	private static final int UNION_COUNT = 23;

	protected static WodqaEngine wodqaEngine;

	protected static Model mainModel;

	protected static Logger logger;

	private static String queryResults = "";
	private static int VOID_COUNT = 9;
	private static final String FILE_NAME = "/home/burak/complexEvalResultsHH"
			+ VOID_COUNT;

	private static List<String> avreageAnalyisTimes;
	private static List<String> avreageExecutionTimes;

	@BeforeClass
	public static void beforeClass() throws MalformedURLException {

		logger = Logger.getLogger(WoDQAEvaluation.class);
		logger.setLevel(Level.DEBUG);

		// initialize average analyis and execution times...
		avreageAnalyisTimes = new ArrayList<String>();
		avreageExecutionTimes = new ArrayList<String>();

		// Register bound join query engine...
		// QueryEngineUnion.register();
		// QueryEngineFilter.register();

		mainModel = VoidModelConstructor
				.constructVOIDSpaceModel(FileOperations.CLEANSED_9_VOIDS);
		wodqaEngine = new WodqaEngine(false, false);
	}

	/**
	 * It executes the given query.
	 * 
	 * @param query
	 * @param queryName
	 * @param askOpt
	 * @param executionCount
	 * @throws Exception
	 * @throws QueryHeaderException
	 * @throws InactiveEndpointException
	 */
	protected String executeQuery(String query, String queryName,
			boolean askOpt, int executionCount) throws Exception,
			QueryHeaderException, InactiveEndpointException {
		String subResult = "";
		String executionTimes = "";
		String analyzeTimes = "";
		String totalTimes = "";
		List<Long> totalAnalysisTimes = new ArrayList<Long>();
		List<Long> totalExecutionTimes = new ArrayList<Long>();
		List<Long> totalQueryTimes = new ArrayList<Long>();
		for (int count = 0; count < executionCount; count++) {
			long analysisStartTime = System.currentTimeMillis();
			// QueryExecution execution =
			// wodqaEngine.prepareToExecute(mainModel,
			// query, askOpt);
			String federatedQuery = wodqaEngine.federateQuery(mainModel, query,
					false);
			// System.out.println(QueryFactory.create(federatedQuery));
			// String federatedQuery = executor.reorganizeQuery(mainModel,
			// query,
			// askOpt);
			long analysisTime = System.currentTimeMillis() - analysisStartTime;
			analyzeTimes += analysisTime + ", ";
			// logger.info(MessageFormat.format("Federated query is: {0}",
			// federatedQuery));

			// QueryResult res =
			// executor.executeGenericQuery(federatedQuery);
			// execute queries with bound join...
			// QueryExecution execution = QueryExecutionFactory.create(
			// QueryFactory.create(federatedQuery),
			// ModelFactory.createDefaultModel());
			// execution.getContext()
			// .put(Constants.UNION_SIZE_SYMBOL, UNION_COUNT);
			// execution.getContext().put(Constants.FILTER_TYPE,
			// Constants.OR_FILTER);

			// ResultSet res = wodqaEngine.executeSelect(federatedQuery);
			long executionTime = 0;
			long executionStartTime = System.currentTimeMillis();
			// // if (!res.hasNext() && executionCount == -1)
			// // count--;
			// // int solutionCount = 0;
			// if (res.hasNext()) {
			// // solutionCount++;
			// // res.next();
			// executionTime = System.currentTimeMillis() - executionStartTime;
			// }
			// while (res.hasNext()) {
			// res.next();
			// // System.out.println(solution);
			// // System.out.println(solutionCount);
			// }
			executionTime = System.currentTimeMillis() - executionStartTime;
			// System.out.println("Solution count: " + solutionCount);
			executionTimes += executionTime + ", ";
			logger.info(MessageFormat.format("Anaysis time: {0}", analysisTime));
			logger.info(MessageFormat.format("Execution time: {0}",
					executionTime));
			long totalTime = analysisTime + executionTime;
			totalTimes += totalTime + ", ";
			logger.info(MessageFormat.format("Total time: {0}", totalTime));
			totalAnalysisTimes.add(analysisTime);
			totalExecutionTimes.add(executionTime);
			totalQueryTimes.add(totalTime);

			// ResultSetFormatter.out(res);
			// System.out.println("Sonuc var mi? " + res.hasNext());
		}

		subResult += "\n#####################################" + queryName
				+ "#####################################";

		long averageAnalysisTime = calculateAverageTime(totalAnalysisTimes);
		subResult += "\n All Analysis Times: " + analyzeTimes
				+ " Average Analysis Time: " + averageAnalysisTime;
		long averageExecutionTime = calculateAverageTime(totalExecutionTimes);
		subResult += "\n All Execution Times: " + executionTimes
				+ ", Average Execution Time: " + averageExecutionTime;
		subResult += "\n All Total Times: " + totalTimes
				+ " Average Total Time: "
				+ calculateAverageTime(totalQueryTimes);
		subResult += "\n##############################################################################################";

		logger.debug(subResult);
		queryResults += subResult;

		avreageAnalyisTimes.add(queryName + ": " + averageAnalysisTime);
		avreageExecutionTimes.add(queryName + ": " + averageExecutionTime);

		return analyzeTimes;
	}

	/**
	 * This method calculates average time except from two maximum time.
	 * 
	 * @param totalTimes
	 * @return
	 */
	private long calculateAverageTime(List<Long> totalTimes) {
		long totalTime = 0;
		if (totalTimes.size() > 2) {
			// remove max two element
			int maxIndex = findMaxTimeIndex(totalTimes);
			totalTimes.remove(maxIndex);
			maxIndex = findMaxTimeIndex(totalTimes);
			totalTimes.remove(maxIndex);
		}
		// calculate total time
		for (long time : totalTimes) {
			totalTime += time;
		}
		// return average time
		return totalTime / totalTimes.size();
	}

	/**
	 * Find index of maximum element contained in total times.
	 * 
	 * @param totalTimes
	 * @return
	 */
	private int findMaxTimeIndex(List<Long> totalTimes) {
		long maxTime = 0;
		int maxIndex = 0;
		for (int i = 0; i < totalTimes.size(); i++) {
			long time = totalTimes.get(i);
			if (time > maxTime) {
				maxTime = time;
				maxIndex = i;
			}
		}
		return maxIndex;
	}

	@AfterClass
	public static void afterClass() throws Exception {

		String averageAnalysisTimeResults = "\n Average Analysis Times: ";
		averageAnalysisTimeResults += "\n****************************************\n";
		String averageExecutionTimeResults = "\n Average Execution Times: ";
		averageExecutionTimeResults += "\n****************************************\n";

		for (String averageAnalysisTime : avreageAnalyisTimes) {
			averageAnalysisTimeResults += averageAnalysisTime + "\n";
		}
		averageAnalysisTimeResults += "****************************************\n";

		for (String averageExecutionTime : avreageExecutionTimes) {
			averageExecutionTimeResults += averageExecutionTime + "\n";
		}
		averageExecutionTimeResults += "****************************************\n";

		queryResults += averageAnalysisTimeResults;
		queryResults += averageExecutionTimeResults;

		logger.info(queryResults);
		FileWriter fileWriter = new FileWriter(FILE_NAME);
		fileWriter.write(queryResults);
		fileWriter.close();
	}

}
