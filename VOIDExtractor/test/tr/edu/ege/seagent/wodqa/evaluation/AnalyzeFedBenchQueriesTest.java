package tr.edu.ege.seagent.wodqa.evaluation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;

import org.junit.AfterClass;
import org.junit.Test;

import com.hp.hpl.jena.query.QueryFactory;

import tr.edu.ege.seagent.voidextractor.Queries;
import tr.edu.ege.seagent.wodqa.query.WodqaEngine;

public class AnalyzeFedBenchQueriesTest extends AbstractEvaluationTest {

	private static String finalLogFederated = "";
	private static String finalLogRaw = "";

	@Test
	public void analyzeCrossDomain1() throws Exception {
		WodqaEngine wodqaEngine = new WodqaEngine();
		String federatedQuery = wodqaEngine.federateQuery(mainModel,
				Queries.CROSS_DOMAIN_QUERY_1, true);

		finalLogRaw += "Cross Domain Query-1:\n" + Queries.CROSS_DOMAIN_QUERY_1
				+ "\n\n\n";
		String log = MessageFormat.format(
				"Federated state of cross domain query-1 is \n \"{0}\" \n\n\n",
				federatedQuery);
		finalLogFederated += log;
		logger.info(log);
	}

	@Test
	public void analyzeCrossDomain2() throws Exception {
		WodqaEngine wodqaEngine = new WodqaEngine();
		String federatedQuery = wodqaEngine.federateQuery(mainModel,
				Queries.CROSS_DOMAIN_QUERY_2, true);
		finalLogRaw += "Cross Domain Query-2:\n" + Queries.CROSS_DOMAIN_QUERY_2
				+ "\n\n\n";
		String log = MessageFormat.format(
				"Federated state of cross domain query-2 is \n \"{0}\" \n\n\n",
				federatedQuery);
		finalLogFederated += log;
		logger.info(log);
	}

	@Test
	public void analyzeCrossDomain3() throws Exception {
		WodqaEngine wodqaEngine = new WodqaEngine();
		String federatedQuery = wodqaEngine.federateQuery(mainModel,
				Queries.CROSS_DOMAIN_QUERY_3, true);
		finalLogRaw += "Cross Domain Query-3:\n" + Queries.CROSS_DOMAIN_QUERY_3
				+ "\n\n\n";
		String log = MessageFormat.format(
				"Federated state of cross domain query-3 is \n \"{0}\" \n\n\n",
				federatedQuery);
		finalLogFederated += log;
		logger.info(log);
	}

	@Test
	public void analyzeCrossDomain4() throws Exception {
		WodqaEngine wodqaEngine = new WodqaEngine();
		String federatedQuery = wodqaEngine.federateQuery(mainModel,
				Queries.CROSS_DOMAIN_QUERY_4, true);
		finalLogRaw += "Cross Domain Query-4:\n" + Queries.CROSS_DOMAIN_QUERY_4
				+ "\n\n\n";
		String log = MessageFormat.format(
				"Federated state of cross domain query-4 is \n \"{0}\" \n\n\n",
				federatedQuery);
		finalLogFederated += log;
		logger.info(log);
	}

	@Test
	public void analyzeCrossDomain5() throws Exception {
		WodqaEngine wodqaEngine = new WodqaEngine();
		String federatedQuery = wodqaEngine.federateQuery(mainModel,
				Queries.CROSS_DOMAIN_QUERY_5, true);
		finalLogRaw += "Cross Domain Query-5:\n" + Queries.CROSS_DOMAIN_QUERY_5
				+ "\n\n\n";
		String log = MessageFormat.format(
				"Federated state of cross domain query-5 is \n \"{0}\" \n\n\n",
				federatedQuery);
		finalLogFederated += log;
		logger.info(log);
	}

	@Test
	public void analyzeCrossDomain6() throws Exception {
		WodqaEngine wodqaEngine = new WodqaEngine();
		String federatedQuery = wodqaEngine.federateQuery(mainModel,
				Queries.CROSS_DOMAIN_QUERY_6, true);
		finalLogRaw += "Cross Domain Query-6:\n" + Queries.CROSS_DOMAIN_QUERY_6
				+ "\n\n\n";
		String log = MessageFormat.format(
				"Federated state of cross domain query-6 is \n \"{0}\" \n\n\n",
				federatedQuery);
		finalLogFederated += log;
		logger.info(log);
	}

	@Test
	public void analyzeCrossDomain7() throws Exception {
		WodqaEngine wodqaEngine = new WodqaEngine();
		String federatedQuery = wodqaEngine.federateQuery(mainModel,
				Queries.CROSS_DOMAIN_QUERY_7, true);
		finalLogRaw += "Cross Domain Query-7:\n" + Queries.CROSS_DOMAIN_QUERY_7
				+ "\n\n\n";
		String log = MessageFormat.format(
				"Federated state of cross domain query-7 is \n \"{0}\" \n\n\n",
				federatedQuery);
		finalLogFederated += log;
		logger.info(log);
	}

	@Test
	public void analyzeLifeSciences1() throws Exception {
		WodqaEngine wodqaEngine = new WodqaEngine();
		String federatedQuery = wodqaEngine.federateQuery(mainModel,
				Queries.LIFE_SCIENCES_QUERY_1, true);
		finalLogRaw += "Life Sciences Query-1:\n"
				+ Queries.LIFE_SCIENCES_QUERY_1 + "\n\n\n";
		String log = MessageFormat
				.format("Federated state of life sciences query-1 is \n \"{0}\" \n\n\n",
						federatedQuery);
		finalLogFederated += log;
		logger.info(log);
	}

	@Test
	public void analyzeLifeSciences2() throws Exception {
		WodqaEngine wodqaEngine = new WodqaEngine();
		String federatedQuery = wodqaEngine.federateQuery(mainModel,
				Queries.LIFE_SCIENCES_QUERY_2, true);
		finalLogRaw += "Life Sciences Query-2:\n"
				+ Queries.LIFE_SCIENCES_QUERY_2 + "\n\n\n";
		String log = MessageFormat
				.format("Federated state of life sciences query-2 is \n \"{0}\" \n\n\n",
						federatedQuery);
		finalLogFederated += log;
		logger.info(log);
	}

	@Test
	public void analyzeLifeSciences3() throws Exception {
		WodqaEngine wodqaEngine = new WodqaEngine();
		String federatedQuery = wodqaEngine.federateQuery(mainModel,
				Queries.LIFE_SCIENCES_QUERY_3, true);
		finalLogRaw += "Life Sciences Query-3:\n"
				+ Queries.LIFE_SCIENCES_QUERY_3 + "\n\n\n";
		String log = MessageFormat
				.format("Federated state of life sciences query-3 is \n \"{0}\" \n\n\n",
						federatedQuery);
		System.out.println(QueryFactory.create(federatedQuery));
		finalLogFederated += log;
		logger.info(log);
	}

	@Test
	public void analyzeLifeSciences4() throws Exception {
		WodqaEngine wodqaEngine = new WodqaEngine();
		String federatedQuery = wodqaEngine.federateQuery(mainModel,
				Queries.LIFE_SCIENCES_QUERY_4, true);
		finalLogRaw += "Life Sciences Query-4:\n"
				+ Queries.LIFE_SCIENCES_QUERY_4 + "\n\n\n";
		String log = MessageFormat
				.format("Federated state of life sciences query-4 is \n \"{0}\" \n\n\n",
						federatedQuery);
		finalLogFederated += log;
		logger.info(log);
	}

	@Test
	public void analyzeLifeSciences5() throws Exception {
		WodqaEngine wodqaEngine = new WodqaEngine();
		String federatedQuery = wodqaEngine.federateQuery(mainModel,
				Queries.LIFE_SCIENCES_QUERY_5, true);
		finalLogRaw += "Life Sciences Query-5:\n"
				+ Queries.LIFE_SCIENCES_QUERY_5 + "\n\n\n";
		String log = MessageFormat
				.format("Federated state of life sciences query-5 is \n \"{0}\" \n\n\n",
						federatedQuery);
		finalLogFederated += log;
		logger.info(log);
	}

	@Test
	public void analyzeLifeSciences6() throws Exception {
		WodqaEngine wodqaEngine = new WodqaEngine();
		String federatedQuery = wodqaEngine.federateQuery(mainModel,
				Queries.LIFE_SCIENCES_QUERY_6, true);
		finalLogRaw += "Life Sciences Query-6:\n"
				+ Queries.LIFE_SCIENCES_QUERY_6 + "\n\n\n";
		String log = MessageFormat
				.format("Federated state of life sciences query-6 is \n \"{0}\" \n\n\n",
						federatedQuery);
		finalLogFederated += log;
		logger.info(log);
	}

	@Test
	public void analyzeLifeSciences7() throws Exception {
		WodqaEngine wodqaEngine = new WodqaEngine();
		String federatedQuery = wodqaEngine.federateQuery(mainModel,
				Queries.LIFE_SCIENCES_QUERY_7, true);
		finalLogRaw += "Life Sciences Query-7:\n"
				+ Queries.LIFE_SCIENCES_QUERY_7 + "\n\n\n";
		String log = MessageFormat
				.format("Federated state of life sciences query-7 is \n \"{0}\" \n\n\n",
						federatedQuery);
		finalLogFederated += log;
		logger.info(log);
	}

//	@AfterClass
	public static void calculateFinalResults() throws IOException {
		FileWriter fileWriterFed = new FileWriter(new File(
				"/home/etmen/federatedQueries"));
		fileWriterFed.write(finalLogFederated);
		fileWriterFed.close();
		FileWriter fileWriter = new FileWriter(new File(
				"/home/etmen/rawQueries"));
		fileWriter.write(finalLogRaw);
		fileWriter.close();
	}

}
