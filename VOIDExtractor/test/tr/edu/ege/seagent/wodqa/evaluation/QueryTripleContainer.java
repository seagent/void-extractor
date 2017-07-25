package tr.edu.ege.seagent.wodqa.evaluation;

public class QueryTripleContainer {

	public static final String[] TRIPLES_OF_CD1 = {
			"<http://dbpedia.org/resource/Barack_Obama> ?predicate ?object",
			"?subject <http://www.w3.org/2002/07/owl#sameAs> <http://dbpedia.org/resource/Barack_Obama>",
			"?subject ?predicate ?object" };
	public static final String[] TRIPLES_OF_CD2 = {
			"<http://dbpedia.org/resource/Barack_Obama> <http://dbpedia.org/ontology/party> ?party",
			"?x <http://data.nytimes.com/elements/topicPage> ?page",
			"?x <http://www.w3.org/2002/07/owl#sameAs> <http://dbpedia.org/resource/Barack_Obama>" };
	public static final String[] TRIPLES_OF_CD3 = {
			"?president <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/President>",
			"?president <http://dbpedia.org/ontology/nationality> <http://dbpedia.org/resource/United_States>",
			"?president <http://dbpedia.org/ontology/party> ?party",
			"?x <http://data.nytimes.com/elements/topicPage> ?page",
			"?x <http://www.w3.org/2002/07/owl#sameAs> ?president" };
	public static final String[] TRIPLES_OF_CD4 = {
			"?film <http://purl.org/dc/terms/title> 'Tarzan'",
			"?film <http://data.linkedmdb.org/resource/movie/actor> ?actor",
			"?actor <http://www.w3.org/2002/07/owl#sameAs> ?x",
			"?y <http://www.w3.org/2002/07/owl#sameAs> ?x",
			"?y <http://data.nytimes.com/elements/topicPage> ?news" };
	public static final String[] TRIPLES_OF_CD5 = {
			"?film <http://dbpedia.org/ontology/director>  ?director",
			"?director <http://dbpedia.org/ontology/nationality> <http://dbpedia.org/resource/Italy>",
			"?x <http://www.w3.org/2002/07/owl#sameAs> ?film",
			"?x <http://data.linkedmdb.org/resource/movie/genre> ?genre" };
	public static final String[] TRIPLES_OF_CD6 = {
			"?artist <http://xmlns.com/foaf/0.1/name> ?name",
			"?artist <http://xmlns.com/foaf/0.1/based_near> ?location",
			"?location <http://www.geonames.org/ontology#parentFeature> ?germany",
			"?germany <http://www.geonames.org/ontology#name> 'Federal Republic of Germany'" };
	public static final String[] TRIPLES_OF_CD7 = {
			"?location <http://www.geonames.org/ontology#parentFeature> ?parent",
			"?parent <http://www.geonames.org/ontology#name> 'California'",
			"?y <http://www.w3.org/2002/07/owl#sameAs> ?location",
			"?y <http://data.nytimes.com/elements/topicPage> ?news" };
	public static final String[] TRIPLES_OF_LS1 = {
			"?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/meltingPoint> ?melt",
			"?drug <http://dbpedia.org/ontology/Drug/meltingPoint> ?melt" };
	public static final String[] TRIPLES_OF_LS2 = {
			"<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB00201> ?predicate ?object",
			"<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB00201> <http://www.w3.org/2002/07/owl#sameAs> ?caff",
			"?caff ?predicate ?object" };
	public static final String[] TRIPLES_OF_LS3 = {
			"?Drug <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/Drug>",
			"?y <http://www.w3.org/2002/07/owl#sameAs> ?Drug",
			"?Int <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/interactionDrug1> ?y",
			"?Int <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/interactionDrug2> ?IntDrug",
			"?Int <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/text> ?IntEffect" };
	public static final String[] TRIPLES_OF_LS4 = {
			"?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/drugCategory> <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugcategory/cathartics>",
			"?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/keggCompoundId> ?cpd",
			"?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/description> ?drugDesc",
			"?enzyme <http://bio2rdf.org/ns/kegg#xSubstrate> ?cpd",
			"?enzyme <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://bio2rdf.org/ns/kegg#Enzyme>",
			"?reaction <http://bio2rdf.org/ns/kegg#xEnzyme> ?enzyme",
			"?reaction <http://bio2rdf.org/ns/kegg#equation> ?equation" };
	public static final String[] TRIPLES_OF_LS5 = {
			"?drug <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/drugs>",
			"?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/keggCompoundId> ?keggDrug",
			"?keggDrug <http://bio2rdf.org/ns/bio2rdf#url> ?keggUrl",
			"?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/genericName> ?drugBankName",
			"?chebiDrug <http://purl.org/dc/elements/1.1/title> ?drugBankName",
			"?chebiDrug <http://bio2rdf.org/ns/bio2rdf#image> ?chebiImage" };
	public static final String[] TRIPLES_OF_LS6 = {
			"?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/drugCategory> <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugcategory/micronutrient>",
			"?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/casRegistryNumber> ?id",
			"?keggDrug <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://bio2rdf.org/ns/kegg#Drug>",
			"?keggDrug <http://bio2rdf.org/ns/bio2rdf#xRef> ?id",
			"?keggDrug <http://purl.org/dc/elements/1.1/title> ?title" };
	public static final String[] TRIPLES_OF_LS7 = {
			"?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/affectedOrganism>  'Humans and other mammals'",
			"?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/casRegistryNumber> ?cas",
			"?keggDrug <http://bio2rdf.org/ns/bio2rdf#xRef> ?cas",
			"?keggDrug <http://bio2rdf.org/ns/bio2rdf#mass> ?mass",
			"OPTIONAL { ?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/biotransformation> ?transform . } " };

}
