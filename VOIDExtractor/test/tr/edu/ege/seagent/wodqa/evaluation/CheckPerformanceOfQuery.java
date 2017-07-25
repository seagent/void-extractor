package tr.edu.ege.seagent.wodqa.evaluation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.OpAsQuery;
import com.hp.hpl.jena.sparql.core.DatasetGraphFactory;
import com.hp.hpl.jena.sparql.expr.E_OneOf;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.ElementVisitorBase;
import com.hp.hpl.jena.sparql.syntax.ElementWalker;
import com.hp.hpl.jena.util.FileManager;

public class CheckPerformanceOfQuery {

	private Logger logger = Logger.getLogger(CheckPerformanceOfQuery.class);

	@Test
	public void checkExecutionTime() throws Exception {
		String queryToSplitted = "SELECT  * WHERE { "
				+ "{"
				+ "?y <http://www.w3.org/2002/07/owl#sameAs> ?location . "
				+ "FILTER ( ?location IN (<http://sws.geonames.org/5393068/>, <http://sws.geonames.org/5374376/>, <http://sws.geonames.org/5392967/>, <http://sws.geonames.org/5392427/>, <http://sws.geonames.org/5379524/>, <http://sws.geonames.org/5397100/>, <http://sws.geonames.org/5359604/>, <http://sws.geonames.org/5368381/>, <http://sws.geonames.org/5322745/>, <http://sws.geonames.org/5372163/>, <http://sws.geonames.org/5370468/>, <http://sws.geonames.org/5562484/>, <http://sws.geonames.org/5391726/>, <http://sws.geonames.org/5383537/>, <http://sws.geonames.org/5393021/>, <http://sws.geonames.org/5376101/>, <http://sws.geonames.org/5391997/>, <http://sws.geonames.org/5387890/>, <http://sws.geonames.org/5370594/>, <http://sws.geonames.org/5405889/>, <http://sws.geonames.org/5392329/>, <http://sws.geonames.org/5565500/>, <http://sws.geonames.org/5391832/>, <http://sws.geonames.org/5392126/>, <http://sws.geonames.org/5389519/>, <http://sws.geonames.org/5374091/>, <http://sws.geonames.org/5323622/>, <http://sws.geonames.org/5372259/>, <http://sws.geonames.org/5363385/>, <http://sws.geonames.org/5403789/>, <http://sws.geonames.org/5398597/>, <http://sws.geonames.org/5411026/>, <http://sws.geonames.org/5400390/>, <http://sws.geonames.org/5369578/>, <http://sws.geonames.org/5395582/>, <http://sws.geonames.org/5396987/>, <http://sws.geonames.org/5364466/>, <http://sws.geonames.org/5403973/>, <http://sws.geonames.org/5339268/>, <http://sws.geonames.org/5323414/>, <http://sws.geonames.org/5376509/>, <http://sws.geonames.org/5350964/>, <http://sws.geonames.org/5338872/>, <http://sws.geonames.org/5410882/>, <http://sws.geonames.org/5345659/>, <http://sws.geonames.org/5362932/>, <http://sws.geonames.org/5391692/>, <http://sws.geonames.org/5332628/>, <http://sws.geonames.org/5359067/>, <http://sws.geonames.org/5383832/>, <http://sws.geonames.org/5332191/>, <http://sws.geonames.org/5352462/>, <http://sws.geonames.org/5553701/>, <http://sws.geonames.org/5566544/>, <http://sws.geonames.org/5572575/>, <http://sws.geonames.org/5387397/>, <http://sws.geonames.org/5571369/>, <http://sws.geonames.org/5568120/>, <http://sws.geonames.org/5571096/>, <http://sws.geonames.org/5566798/>, <http://sws.geonames.org/5568823/>, <http://sws.geonames.org/6284976/>, <http://sws.geonames.org/6285747/>, <http://sws.geonames.org/6295625/>, <http://sws.geonames.org/6295626/>, <http://sws.geonames.org/6297439/>, <http://sws.geonames.org/6297441/>, <http://sws.geonames.org/6297519/>, <http://sws.geonames.org/6297577/>, <http://sws.geonames.org/6297583/>, <http://sws.geonames.org/6297594/>, <http://sws.geonames.org/6297645/>, <http://sws.geonames.org/6297670/>, <http://sws.geonames.org/6297676/>, <http://sws.geonames.org/6297842/>, <http://sws.geonames.org/6298056/>, <http://sws.geonames.org/6298514/>, <http://sws.geonames.org/6298516/>, <http://sws.geonames.org/6298643/>, <http://sws.geonames.org/6284975/>, <http://sws.geonames.org/6297509/>, <http://sws.geonames.org/6297523/>, <http://sws.geonames.org/6297560/>, <http://sws.geonames.org/6297565/>, <http://sws.geonames.org/6297579/>, <http://sws.geonames.org/6297581/>, <http://sws.geonames.org/6297586/>, <http://sws.geonames.org/6297640/>, <http://sws.geonames.org/6297643/>, <http://sws.geonames.org/6297668/>, <http://sws.geonames.org/6297678/>, <http://sws.geonames.org/6297680/>, <http://sws.geonames.org/6297685/>, <http://sws.geonames.org/6297686/>, <http://sws.geonames.org/6297898/>, <http://sws.geonames.org/6298512/>, <http://sws.geonames.org/6298519/>, <http://sws.geonames.org/6298752/>, <http://sws.geonames.org/6298771/>, <http://sws.geonames.org/6298779/>, <http://sws.geonames.org/6299143/>, <http://sws.geonames.org/6301561/>, <http://sws.geonames.org/6301624/>, <http://sws.geonames.org/6301667/>, <http://sws.geonames.org/6301674/>, <http://sws.geonames.org/6324763/>, <http://sws.geonames.org/6297397/>, <http://sws.geonames.org/6297479/>, <http://sws.geonames.org/6297481/>, <http://sws.geonames.org/6297572/>, <http://sws.geonames.org/6297647/>, <http://sws.geonames.org/6297649/>, <http://sws.geonames.org/6297651/>, <http://sws.geonames.org/6297666/>, <http://sws.geonames.org/6297672/>, <http://sws.geonames.org/6297674/>, <http://sws.geonames.org/6297877/>, <http://sws.geonames.org/6298010/>, <http://sws.geonames.org/6298020/>, <http://sws.geonames.org/6298039/>, <http://sws.geonames.org/6298045/>, <http://sws.geonames.org/6298515/>, <http://sws.geonames.org/6298517/>, <http://sws.geonames.org/6298518/>, <http://sws.geonames.org/6298719/>, <http://sws.geonames.org/6298765/>, <http://sws.geonames.org/6298855/>, <http://sws.geonames.org/6299052/>, <http://sws.geonames.org/6299116/>, <http://sws.geonames.org/6299257/>, <http://sws.geonames.org/6301568/>, <http://sws.geonames.org/6301596/>, <http://sws.geonames.org/6301626/>, <http://sws.geonames.org/6301665/>, <http://sws.geonames.org/6301685/>, <http://sws.geonames.org/6301760/>, <http://sws.geonames.org/6301897/>, <http://sws.geonames.org/6324762/>, <http://sws.geonames.org/6298730/>, <http://sws.geonames.org/6298769/>, <http://sws.geonames.org/6298772/>, <http://sws.geonames.org/6298775/>, <http://sws.geonames.org/6298780/>, <http://sws.geonames.org/6301403/>, <http://sws.geonames.org/6301573/>, <http://sws.geonames.org/6301625/>, <http://sws.geonames.org/6301627/>, <http://sws.geonames.org/6301628/>, <http://sws.geonames.org/6301631/>, <http://sws.geonames.org/6301679/>, <http://sws.geonames.org/6301680/>, <http://sws.geonames.org/6301743/>, <http://sws.geonames.org/6462006/>, <http://sws.geonames.org/6462760/>, <http://sws.geonames.org/6461960/>, <http://sws.geonames.org/6465307/>, <http://sws.geonames.org/6465980/>, <http://sws.geonames.org/6466173/>, <http://sws.geonames.org/6466174/>, <http://sws.geonames.org/6466232/>, <http://sws.geonames.org/6466300/>, <http://sws.geonames.org/6468846/>, <http://sws.geonames.org/6470772/>, <http://sws.geonames.org/6462391/>, <http://sws.geonames.org/6464302/>, <http://sws.geonames.org/6465424/>, <http://sws.geonames.org/6465813/>, <http://sws.geonames.org/6465950/>, <http://sws.geonames.org/6466838/>, <http://sws.geonames.org/6467321/>, <http://sws.geonames.org/6467682/>, <http://sws.geonames.org/6472322/>, <http://sws.geonames.org/6465537/>, <http://sws.geonames.org/6465977/>, <http://sws.geonames.org/6466203/>, <http://sws.geonames.org/6466233/>, <http://sws.geonames.org/6466295/>, <http://sws.geonames.org/6466593/>, <http://sws.geonames.org/6466779/>, <http://sws.geonames.org/6467027/>, <http://sws.geonames.org/6467327/>, <http://sws.geonames.org/6467333/>, <http://sws.geonames.org/6467433/>, <http://sws.geonames.org/6470430/>, <http://sws.geonames.org/6472267/>, <http://sws.geonames.org/6474921/>, <http://sws.geonames.org/6480054/>, <http://sws.geonames.org/6479400/>, <http://sws.geonames.org/6479474/>, <http://sws.geonames.org/6483728/>, <http://sws.geonames.org/6483918/>, <http://sws.geonames.org/6486302/>, <http://sws.geonames.org/6487141/>, <http://sws.geonames.org/6493406/>, <http://sws.geonames.org/6476391/>, <http://sws.geonames.org/6477217/>, <http://sws.geonames.org/6478873/>, <http://sws.geonames.org/6479260/>, <http://sws.geonames.org/6484280/>, <http://sws.geonames.org/6486471/>, <http://sws.geonames.org/6486665/>, <http://sws.geonames.org/6489116/>, <http://sws.geonames.org/6490159/>, <http://sws.geonames.org/6501645/>, <http://sws.geonames.org/6489778/>, <http://sws.geonames.org/6492047/>, <http://sws.geonames.org/6501667/>, <http://sws.geonames.org/6504359/>, <http://sws.geonames.org/6505377/>, <http://sws.geonames.org/6511003/>, <http://sws.geonames.org/6518504/>, <http://sws.geonames.org/6520106/>, <http://sws.geonames.org/6503985/>, <http://sws.geonames.org/6507256/>, <http://sws.geonames.org/6512692/>, <http://sws.geonames.org/6512697/>, <http://sws.geonames.org/6526373/>, <http://sws.geonames.org/6526906/>, <http://sws.geonames.org/6515691/>, <http://sws.geonames.org/6525084/>, <http://sws.geonames.org/6526155/>, <http://sws.geonames.org/6528302/>, <http://sws.geonames.org/6532059/>, <http://sws.geonames.org/6533183/>, <http://sws.geonames.org/6620476/>, <http://sws.geonames.org/6620477/>, <http://sws.geonames.org/6620478/>, <http://sws.geonames.org/6620487/>, <http://sws.geonames.org/6620686/>, <http://sws.geonames.org/6692750/>, <http://sws.geonames.org/6524975/>, <http://sws.geonames.org/6529665/>, <http://sws.geonames.org/6530569/>, <http://sws.geonames.org/6544206/>, <http://sws.geonames.org/6612085/>, <http://sws.geonames.org/6620473/>, <http://sws.geonames.org/6620474/>, <http://sws.geonames.org/6620475/>, <http://sws.geonames.org/6620485/>, <http://sws.geonames.org/6620486/>, <http://sws.geonames.org/6527783/>, <http://sws.geonames.org/6528117/>, <http://sws.geonames.org/6529875/>, <http://sws.geonames.org/6531504/>, <http://sws.geonames.org/6620471/>, <http://sws.geonames.org/6620479/>, <http://sws.geonames.org/6620480/>, <http://sws.geonames.org/6620481/>, <http://sws.geonames.org/6620482/>, <http://sws.geonames.org/6620483/>, <http://sws.geonames.org/6620484/>, <http://sws.geonames.org/6620687/>, <http://sws.geonames.org/6620688/>, <http://sws.geonames.org/6691651/>, <http://sws.geonames.org/7316029/>) )."
				+ "} "
				+ "?y <http://data.nytimes.com/elements/topicPage> ?news."
				+ "}";
		Query query = QueryFactory.create(queryToSplitted);

		// long before = System.currentTimeMillis();
		// QueryExecution queryExecution = QueryExecutionFactory.sparqlService(
		// "http://localhost:9000/sparql/", query);
		// ResultSet resultSet = queryExecution.execSelect();
		// while (resultSet.hasNext()) {
		// QuerySolution querySolution = (QuerySolution) resultSet.next();
		// System.out.println(querySolution);
		// }
		// long after = System.currentTimeMillis();
		// System.out.println(after - before);

		// Op op = QueryExecutionFactory.createPlan(QueryFactory.create(query),
		// DatasetGraphFactory.createMem(), null).getOp();
		// System.out.println(OpAsQuery.asQuery(op));
		int filterSize = 0;
		while (filterSize < 256) {
			final List<String> uriList = new ArrayList<String>();
			filterSize += 5;
			if (filterSize > 256) {
				filterSize = 256;
			}
			fillURIList(query, uriList, filterSize);
			analyzeFilterSize(uriList, filterSize);
		}
	}

	private void analyzeFilterSize(final List<String> uriList, int filterSize) {
		String queryText = "SELECT * WHERE { "
				+ "?y <http://www.w3.org/2002/07/owl#sameAs> ?location . "
				+ "?y <http://data.nytimes.com/elements/topicPage> ?news."
				+ "FILTER (?location IN (" + constructInnerFilterPart(uriList)
				+ "))}";

		QueryExecution queryExecution = QueryExecutionFactory.sparqlService(
				"http://155.223.24.47:8891/nytimes/sparql", queryText);
		System.out
				.println("*********************************************************************************");
		logger.info(MessageFormat.format("Query with \"{0}\" is executing",
				filterSize));
		long before = System.currentTimeMillis();
		ResultSet resultSet = queryExecution.execSelect();
		while (resultSet.hasNext()) {
			QuerySolution querySolution = resultSet.next();
			System.out.println(querySolution);
		}
		long after = System.currentTimeMillis();
		logger.info(MessageFormat.format(
				"Query with \"{0}\" has been executed in \"{1}\" miliseconds",
				filterSize, after - before));
		System.out
				.println("*********************************************************************************");
	}

	private String constructInnerFilterPart(List<String> uriList) {
		String part = "";
		for (int i = 0; i < uriList.size(); i++) {
			if (i == 0) {
				part += "<" + uriList.get(i) + "> ";
			} else {
				part += ",<" + uriList.get(i) + "> ";
			}
		}
		return part;
	}

	private void fillURIList(Query query, final List<String> uriList,
			final int filterSize) {
		ElementVisitorBase tripleVisitor = new ElementVisitorBase() {

			@Override
			public void visit(ElementFilter el) {
				E_OneOf expr = (E_OneOf) el.getExpr();
				List<Expr> args = expr.getArgs();
				for (int i = 1; i < filterSize; i++) {
					Expr exprArg = args.get(i);
					uriList.add(exprArg.getConstant().getNode().getURI());
				}
				super.visit(el);
			}
		};
		ElementWalker.walk(query.getQueryPattern(), tripleVisitor);
	}

	private List<String> getURIsInFilter(Query query) {
		final List<String> uriList = new ArrayList<String>();
		ElementVisitorBase tripleVisitor = new ElementVisitorBase() {

			@Override
			public void visit(ElementFilter el) {
				E_OneOf expr = (E_OneOf) el.getExpr();
				List<Expr> args = expr.getArgs();
				for (int i = 1; i < args.size(); i++) {
					Expr exprArg = args.get(i);
					uriList.add(exprArg.getConstant().getNode().getURI());
				}
				super.visit(el);
			}
		};
		ElementWalker.walk(query.getQueryPattern(), tripleVisitor);
		return uriList;
	}

	@Test
	public void evaluateLS3SecondPart() throws Exception {

		String query = "SELECT  * WHERE { "
				+ "?y <http://www.w3.org/2002/07/owl#sameAs> ?Drug. "
				+ "?Int <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/interactionDrug1> ?y. "
				+ "?Int <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/interactionDrug2> ?IntDrug. "
				+ "?Int <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/text> ?IntEffect. "
				+ "} LIMIT 9054";
		QueryExecution queryExecution = QueryExecutionFactory.sparqlService(
				"http://localhost:8000/sparql/", query);
		ResultSet resultSet = queryExecution.execSelect();
		FileWriter fileWriter = new FileWriter(
				"/home/etmen/Desktop/ls3secondPartParsetimes");
		int i = 0;
		long before = System.currentTimeMillis();
		while (resultSet.hasNext()) {
			i++;
			resultSet.next();
			long after = System.currentTimeMillis();
			fileWriter.append("Süre " + i + ": " + (after - before) + "\n");
			before = System.currentTimeMillis();
		}
		fileWriter.close();

	}

	@Test
	public void countQuery() throws Exception {
		String query = "SELECT (count(*) AS ?c)  WHERE { ?y <http://www.w3.org/2002/07/owl#sameAs> ?Drug. ?Int <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/interactionDrug1> ?y. ?Int <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/interactionDrug2> ?IntDrug.?Int <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/text> ?IntEffect.}";
		QueryExecution queryExecution = QueryExecutionFactory.sparqlService(
				"http://localhost:8000/sparql/", query);
		ResultSet resultSet = queryExecution.execSelect();
		long before = System.currentTimeMillis();
		while (resultSet.hasNext()) {
			QuerySolution solution = resultSet.next();
			System.out.println("Count: " + solution.getLiteral("c"));
		}
		long after = System.currentTimeMillis();
		System.out.println(after - before);
	}

	private String readFile(String filePath) throws FileNotFoundException,
			IOException {
		String query = "";
		File file = new File(filePath);
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line = bufferedReader.readLine();
		while (line != null) {
			query += line;
			line = bufferedReader.readLine();
		}
		return query;
	}

	@Test
	public void inMemoryLs3Test() throws Exception {

		Model model = ModelFactory.createDefaultModel();
		InputStream inputStream = FileManager.get().open(
				"/home/etmen/Desktop/DatasetDumps/edited_drugbank_dump.nt");
		model.read(inputStream, null, "N-TRIPLE");

		String query = readFile("/home/etmen/Desktop/LS3SubQuery");
		QueryExecution queryExecution = QueryExecutionFactory.create(query,
				model);
		ResultSet resultSet = queryExecution.execSelect();
		long before = System.currentTimeMillis();
		while (resultSet.hasNext()) {
			resultSet.next();
		}
		long after = System.currentTimeMillis();
		System.out.println(after - before);

	}

	@Test
	public void executeOrValuedLS3() throws Exception {
		String queryOneOfFilter = readFile("/home/etmen/Desktop/LS3SubQuery");
		List<String> uriList = getURIsInFilter(QueryFactory
				.create(queryOneOfFilter));
		String queryOrFilter = "SELECT  * WHERE { "
				+ "?y <http://www.w3.org/2002/07/owl#sameAs> ?Drug. "
				+ "?Int <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/interactionDrug1> ?y. "
				+ "?Int <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/interactionDrug2> ?IntDrug. "
				+ "?Int <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/text> ?IntEffect. "
				+ generateOrFilterBlock(uriList, "Drug") + "}";
		Op op = QueryExecutionFactory.createPlan(
				QueryFactory.create(queryOrFilter),
				DatasetGraphFactory.createMem(), null).getOp();
		Query query = OpAsQuery.asQuery(op);
//		System.out.println(query);
		QueryExecution queryExecution = QueryExecutionFactory.sparqlService(
				"http://localhost:8000/sparql/", query);
		ResultSet resultSet = queryExecution.execSelect();
		List<QuerySolution> solutions = new ArrayList<QuerySolution>();
		long before = System.currentTimeMillis();
		while (resultSet.hasNext()) {
			QuerySolution querySolution = resultSet.next();
			solutions.add(querySolution);
		}
		long after = System.currentTimeMillis();
		System.out.println(after - before);

	}

	private String generateOrFilterBlock(List<String> uriList, String variable) {
		String block = "FILTER(";
		for (int i = 0; i < uriList.size(); i++) {
			if (i > 0) {
				block += " || ";
			}
			block += "?" + variable + " = <" + uriList.get(i) + ">";
		}
		block += ")";
		return block;
	}
}
