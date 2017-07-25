package tr.edu.ege.seagent.wodqa.evaluation;

import org.junit.Test;

import tr.edu.ege.seagent.boundarq.filterbound.QueryEngineFilter;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class StarShapeTest {

	@Test
	public void starShapeQueryExecutionC1() throws Exception {
		QueryEngineFilter.register();
		String query = "SELECT DISTINCT  ?drug ?enzyme ?reaction WHERE {SERVICE <http://localhost:7000/sparql/> {?drug5 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/Drug>. } SERVICE <http://localhost:8000/sparql/> {?drug1 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/drugCategory> <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugcategory/antibiotics>. ?I1 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/interactionDrug2> ?drug1. ?I1 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/interactionDrug1> ?drug. ?drug <http://www.w3.org/2002/07/owl#sameAs> ?drug5. ?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/keggCompoundId> ?cpd. } SERVICE <http://localhost:8000/sparql/> { ?drug2 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/drugCategory> <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugcategory/antiviralAgents>. ?I2 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/interactionDrug1> ?drug. ?I2 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/interactionDrug2> ?drug2. } SERVICE <http://localhost:8000/sparql/> { ?drug3 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/drugCategory> <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugcategory/antihypertensiveAgents>. ?I3 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/interactionDrug2> ?drug3. ?I3 <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/interactionDrug1> ?drug. } SERVICE <http://localhost:4000/sparql/> {?enzyme <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://bio2rdf.org/ns/kegg#Enzyme>. ?enzyme <http://bio2rdf.org/ns/kegg#xSubstrate> ?cpd. ?reaction <http://bio2rdf.org/ns/kegg#xEnzyme> ?enzyme. ?reaction <http://bio2rdf.org/ns/kegg#equation> ?equation. }}";
		QueryExecution queryExecution = QueryExecutionFactory.create(query,
				ModelFactory.createDefaultModel());
		ResultSet resultSet = queryExecution.execSelect();
		ResultSetFormatter.out(resultSet);
	}
}
