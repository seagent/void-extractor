package tr.edu.ege.seagent.wodqa.evaluation;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;

import tr.edu.ege.seagent.voidextractor.Queries;

public class EvaluationTest extends AbstractEvaluationTest {

	/**
	 * QUERY TIMES: 21954, 44567, 15236, 13831, 13350, 13070, 13333, 13128,
	 * 12884, 44328, (ASK, OPT) AVG: 13,5
	 * 
	 * QUERY TIMES : 17739, 14934, 28072, 15772, 14284, 12926, 12496, 12668,
	 * 13082, 13059 (ASK) AVG: 13,3
	 * 
	 * QUERY TIMES : 19980, 25760, 19968, 16384, 16297, 18726, 14921, 17736,
	 * 14021, 13344, (OPT) AVG:15,9
	 * 
	 * QUERY TIMES : 22117, 20147, 16160, 15415, 25307, 15172, 15334, 16605,
	 * 16691, 66694, AVG :15,8 ()
	 * 
	 * @throws Exception
	 */
	@Test
	public void crossDomainQuery1Test() throws Exception {
		// getRandomVoidModels(60);
		executeQuery(Queries.CROSS_DOMAIN_QUERY_1, "CROSS DOMAIN QUERY-1",
				true, 10);
	}

	/**
	 * QUERY TIMES : 10487, 8644, 8098, 6952, 10298, 7599, 7395, 6951, 8562,
	 * 6890, AVG: 7,6 (ASK, OPT)
	 * 
	 * 
	 * QUERY TIMES : 15550, 9219, 9979, 9370, 9276, 11247, 17502, 9274, 9005,
	 * 11954 AVG: 9,6 (ASK)
	 * 
	 * 
	 * QUERY TIMES : 5164, 4468, 4661, 4163, 4540, 5300, 4095, 4969, 6389, 6674,
	 * AVG: 5,0 (OPT)
	 * 
	 * QUERY TIMES : 6920, 6507, 5970, 6182, 6121, 6075, 6446, 6224, 5959, 6249,
	 * AVG: 6,2 ()
	 * 
	 * @throws Exception
	 */
	@Test
	public void crossDomainQuery2Test() throws Exception {
		// getRandomVoidModels(60);
		executeQuery(Queries.CROSS_DOMAIN_QUERY_2, "CROSS DOMAIN QUERY-2",
				true, 1);
	}

	/**
	 * QUERY TIMES : 9821, 9480, 14399, 11752, 8741, 14831, 10857, 10893, 19577,
	 * 9557 AVG: 8,8 (ASK,OPT)
	 * 
	 * QUERY TIMES : 6457, 5559, 6650, 7075, 5857, 8875, 6201, 7614, 8514, 6424
	 * AVG :6,4 (ASK)
	 * 
	 * 
	 * timeout(OPT)
	 * 
	 * timeout ()
	 * 
	 * 
	 * @throws Exception
	 */
	@Test
	public void crossDomainQuery3Test() throws Exception {
		// getRandomVoidModels(60);
		executeQuery(Queries.CROSS_DOMAIN_QUERY_3, "CROSS DOMAIN QUERY-3",
				true, 10);
	}

	/**
	 * QUERY TIMES : 58516, 43891, 33454, 34377, 39405, 30403, 33186, 33245,
	 * 29421, 37510 AVG:37.3 (ASK, OPT)
	 * 
	 * QUERY TIMES : 23736, 18127, 16278, 18302, 17001, 17019, 17997, 18051,
	 * 17776, 16605 AVG:18,0 (OPT)
	 * 
	 * QUERY TIMES : 19540, 18432, 16850, 17219, 16178, 16680, 16548, 16410,
	 * 16601, 16106 AVG: 17,0 ()
	 * 
	 * QUERY TIMES : 35080, 38135, 43435, 34459, 32577, 30889, 31850, 33474,
	 * 32578, 28307 AVG: 34,0 (ASK)
	 * 
	 * @throws Exception
	 */
	@Test
	public void crossDomainQuery4Test() throws Exception {
		// getRandomVoidModels(60);
		executeQuery(Queries.CROSS_DOMAIN_QUERY_4, "CROSS DOMAIN QUERY-4",
				true, 10);
	}

	/**
	 * QUERY TIMES: timeout(ASK, OPT)
	 * 
	 * QUERY TIMES : 16978, 15857, 14329, 14842 (ASK) AVG:15,501
	 * 
	 * QUERY TIMES : timeout (OPT)
	 * 
	 * QUERY TIMES : 11249,14022, 14364, 13671 () AVG: 13,326
	 * 
	 * @throws Exception
	 */
	@Test
	public void crossDomainQuery5Test() throws Exception {
		// getRandomVoidModels(60);
		executeQuery(Queries.CROSS_DOMAIN_QUERY_5, "CROSS DOMAIN QUERY-5",
				false, 10);
	}

	@Test
	public void crossDomainQuery6Test() throws Exception {
		// getRandomVoidModels(60);
		executeQuery(Queries.CROSS_DOMAIN_QUERY_6, "CROSS DOMAIN QUERY-6",
				true, 10);
	}

	@Test
	public void crossDomainQuery7Test() throws Exception {
		// getRandomVoidModels(60);
		executeQuery(Queries.CROSS_DOMAIN_QUERY_7, "CROSS DOMAIN QUERY-7",
				true, 10);
	}

	@Test
	public void lifeSciencesQuery1Test() throws Exception {
		// getRandomVoidModels(60);
		executeQuery(Queries.LIFE_SCIENCES_QUERY_1, "LIFE SCIENCES QUERY-1",
				true, 10);
	}

	@Test
	public void lifeSciencesQuery2Test() throws Exception {
		// getRandomVoidModels(60);
		executeQuery(Queries.LIFE_SCIENCES_QUERY_2, "LIFE SCIENCES QUERY-2",
				true, 10);
	}

	@Test
	public void lifeSciencesQuery3Test() throws Exception {
		// getRandomVoidModels(60);
		executeQuery(Queries.LIFE_SCIENCES_QUERY_3, "LIFE SCIENCE QUERY-3",
				true, 10);
	}

	@Test
	public void lifeSciencesQuery4Test() throws Exception {
		// getRandomVoidModels(60);
		executeQuery(Queries.LIFE_SCIENCES_QUERY_4, "LIFE SCIENCES QUERY-4",
				true, 10);
	}

	@Test
	public void lifeSciencesQuery5Test() throws Exception {
		// getRandomVoidModels(60);
		executeQuery(Queries.LIFE_SCIENCES_QUERY_5, "LIFE SCIENCES QUERY-5",
				true, 10);
	}

	@Test
	public void lifeSciencesQuery6Test() throws Exception {
		// getRandomVoidModels(60);
		executeQuery(Queries.LIFE_SCIENCES_QUERY_6, "LIFE SCIENCES QUERY-6",
				true, 10);
	}

	@Test
	public void lifeSciencesQuery7Test() throws Exception {
		// Logger.getRootLogger().setLevel(Level.TRACE);
		// getRandomVoidModels(60);
		executeQuery(Queries.LIFE_SCIENCES_QUERY_7, "LIFE SCIENCES QUERY-7",
				true, 10);
	}

}
