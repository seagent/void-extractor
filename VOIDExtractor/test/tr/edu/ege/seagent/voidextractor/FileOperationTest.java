package tr.edu.ege.seagent.voidextractor;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileWriter;

import org.junit.Test;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.tdb.TDBFactory;

public class FileOperationTest {

	@Test
	public void appendFileTest() throws Exception {
		File file = new File(FileOperations.VOID_DIRECTORY_NAME
				+ Vocabulary.VOID_FILE_LOCATION_PREFIX + "fail.txt");
		System.out.println(file.length());
		file.delete();
		file.createNewFile();
		FileWriter writer = new FileWriter(file, true);
		writer.append("dbpedia\n");
		writer.append("dbpedia\n");
		writer.close();
		FileWriter writer2 = new FileWriter(file, true);
		writer2.append("c");
		writer2.close();
		assertEquals(17, file.length());

	}

	@Test
	public void readBigDumpTest() throws Exception {
		// String directory =
		// "/home/galaksiya-1/Desktop/TDB/TDB-0.8.10/sampledump/";
		String directory = "/home/galaksiya-1/Desktop/TDBdumps/";
		Model model = TDBFactory.createModel(directory);
		// OntModel ontModel = ModelFactory
		// .createOntologyModel(OntModelSpec.OWL_MEM);
		// ontModel.add(model);
		Query query = QueryFactory
				.create("prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
						+ "Select * WHERE {?s rdf:type ?o FILTER NOT EXISTS{?s rdf:type ?o FILTER regex(str(?o),\"http://linkedgeodata.org/ontology\" )}"
						+ "} LIMIT 1");
		QueryExecution exec = QueryExecutionFactory.create(query, model);
		ResultSet set = exec.execSelect();
		ResultSetFormatter.out(set);
		exec.close();
		model.close();
	}

}
