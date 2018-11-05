package tr.edu.ege.seagent.voidextractor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import net.ricecode.similarity.JaroWinklerStrategy;
import tr.edu.ege.seagent.wodqa.voiddocument.VOIDOntologyVocabulary;

public class VOIDReducer {

	private Logger logger;

	public VOIDReducer() {
		this.logger = Logger.getLogger(VOIDReducer.class);
	}

	/**
	 * This method reduces urispaces of voids
	 * 
	 * @param voids
	 */
	public void reduceVOIDs(List<OntModel> voids) {
		for (int index = 0; index < voids.size(); index++) {
			logger.info(MessageFormat.format("Reducing VOID {0}...", index));
			try {
				OntModel reducedVOID = reduceURISpaces(voids.get(index));
				reducedVOID.write(new FileOutputStream(new File(
						FileOperations.FIXED_VOIDS + "/dataset" + index)));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			logger.info(MessageFormat.format("VOID {0} has been reduced.",
					index));
		}
	}

	/**
	 * It reduces URISpaces of given VOID model
	 * 
	 * @param VOID
	 * @throws FileNotFoundException
	 */
	private OntModel reduceURISpaces(OntModel VOID)
			throws FileNotFoundException {
		List<String> finalURISpaces = new ArrayList<String>();
		// get URISpace values
		List<String> uriSpaces = constructURISpaceList(VOID);
		Resource dataset = VOID
				.listStatements(null,
						VOIDOntologyVocabulary.DATASET_uriSpace_prp,
						(RDFNode) null).toList().get(0).getSubject();
		// reduce each urispace in the list
		if (!uriSpaces.isEmpty()) {
			for (int i = 0; i < uriSpaces.size(); i++) {
				String finalURISpace = reduceURISpace(uriSpaces, i, 0, 1,
						uriSpaces.get(i));
				logger.info(MessageFormat.format("Final URISpace is: \"{0}\"",
						finalURISpace));
				if (!finalURISpaces.contains(finalURISpace)) {
					finalURISpaces.add(finalURISpace);
				}
			}
		}

		VOID.removeAll(null, VOIDOntologyVocabulary.DATASET_uriSpace_prp,
				(RDFNode) null);
		for (String finalURISpace : finalURISpaces) {
			VOID.add(dataset, VOIDOntologyVocabulary.DATASET_uriSpace_prp,
					ResourceFactory.createPlainLiteral(finalURISpace));
		}
		return VOID;
	}

	/**
	 * This method constructs URISpace list from given VOID instance
	 * 
	 * @param VOID
	 * @return
	 */
	private List<String> constructURISpaceList(OntModel VOID) {
		List<String> uriSpaces = new ArrayList<String>();
		// list urispace statements
		StmtIterator stmtIter = VOID.listStatements(null,
				VOIDOntologyVocabulary.DATASET_uriSpace_prp, (RDFNode) null);
		if (stmtIter != null) {
			// fill urispace list with object values of statements
			List<Statement> stmtList = (List<Statement>) stmtIter.toList();
			for (Statement uriSpaceStmt : stmtList) {
				String uriSpace = uriSpaceStmt.getObject().asLiteral()
						.getString();
				uriSpaces.add(uriSpace);
			}
		}
		return uriSpaces;
	}

	/**
	 * This method reduces URISpace of given mainURISpace according seen count
	 * of mainURISpace
	 * 
	 * @param uriSpaces
	 * @param currentIndex
	 * @param includingPercent
	 * @param similarityPercent
	 * @param mainURISpace
	 * @return
	 */
	private String reduceURISpace(List<String> uriSpaces, int currentIndex,
			double includingPercent, double similarityPercent,
			String mainURISpace) {

		String reducedURISpace = mainURISpace;
		if (includingPercent < 0.3 && similarityPercent > 0.5) {
			reducedURISpace = reduceURI(mainURISpace);
			if (reducedURISpace.equals(mainURISpace)) {
				return reducedURISpace;
			}
			int includedCount = 0;
			int similarityCount = 0;
			JaroWinklerStrategy strategy = new JaroWinklerStrategy();
			for (int j = currentIndex + 1; j < uriSpaces.size(); j++) {
				String controlURISpace = uriSpaces.get(j);
				double similarity = strategy.score(controlURISpace,
						reducedURISpace);
				if (similarity > 0.5) {
					similarityCount++;
				}
				if (controlURISpace.contains(reducedURISpace)) {
					includedCount++;
				}
			}
			similarityPercent = new Double(similarityCount)
					/ new Double(uriSpaces.size());
			includingPercent = new Double(includedCount)
					/ new Double(uriSpaces.size());
			logger.info(MessageFormat
					.format("\"{0}\" URISpace has been reduced to \"{1}\" URISpace and its seen percent is: \"{2}\"",
							mainURISpace, reducedURISpace, includingPercent));
			reducedURISpace = reduceURISpace(uriSpaces, currentIndex,
					includingPercent, similarityPercent, reducedURISpace);
		}
		return reducedURISpace;
	}

	/**
	 * This method corps given uri according to construct a new urispace
	 * 
	 * @param URI
	 * @return
	 */
	private String reduceURI(String URI) {
		int finishIndex = 0;
		// set prefix of URI
		String prefix = "http://";
		if (URI.startsWith("https://")) {
			finishIndex = 8;
			prefix = "https://";
		} else {
			finishIndex = 7;
		}
		// reduce body of URI
		int lastIndex = URI.length() - 1;
		char[] charArray = URI.toCharArray();
		for (int i = lastIndex - 1; i > finishIndex - 1; i--) {
			if (charArray[i] == '#' || charArray[i] == '/') {
				return prefix + URI.substring(finishIndex, i + 1);
			}
		}
		return URI;
	}

	/**
	 * This method reduces datatype property of an void model
	 * 
	 * @param datasetDescModel
	 * @param uriSpace
	 * @param property
	 */
	public void reduceVoid(OntModel datasetDescModel, String uriSpace,
			Property property) {
		Resource subject = null;
		boolean readyToAdd = false;
		// list statements
		StmtIterator stmtIter = datasetDescModel.listStatements(null, property,
				(RDFNode) null);
		if (stmtIter != null) {
			List<Statement> stmts = stmtIter.toList();
			// iterate on statements
			for (int i = 0; i < stmts.size(); i++) {
				Statement statement = stmts.get(i);
				// get object value of statements
				String uriSpaceToBeControlled = statement.getObject()
						.asLiteral().getString();
				// get subject
				subject = statement.getSubject();
				// check whether object value contains given urispace, if so
				// remove the statement
				if (uriSpaceToBeControlled.startsWith(uriSpace)) {
					datasetDescModel.remove(statement);
					readyToAdd = true;
				}
			}
			// finally add contained core urispace to the model
			if (readyToAdd) {
				datasetDescModel.add(subject, property,
						ResourceFactory.createPlainLiteral(uriSpace));
			}
		}

	}

	/**
	 * This method reduces void according to hopcount and core urispace.
	 * 
	 * @param datasetDescModel
	 * @param uriSpace
	 * @param property
	 * @param hopCount
	 */
	public void reduceVoid(OntModel datasetDescModel, String uriSpace,
			Property property, int hopCount) {
		// list statements
		StmtIterator stmtIter = datasetDescModel.listStatements(null, property,
				(RDFNode) null);
		if (stmtIter != null) {
			List<Statement> stmts = stmtIter.toList();
			// iterate on statements
			for (int i = 0; i < stmts.size(); i++) {
				Statement statement = stmts.get(i);
				// get object value of statements
				String uriSpaceToBeControlled = statement.getObject()
						.asLiteral().getString();
				// check whether object value contains given urispace, if so
				// remove the statement and add the new one
				if (uriSpaceToBeControlled.startsWith(uriSpace)) {
					datasetDescModel.remove(statement);
					String remainingPart = getRemainingPart(
							uriSpaceToBeControlled, uriSpace, hopCount);
					String finalURISpace = uriSpace
							+ remainingPart;
					datasetDescModel.add(
							statement.getSubject(),
							property,
							ResourceFactory.createPlainLiteral(finalURISpace));
				}
			}
		}
	}

	/**
	 * This method gets urispace piece that will be added to the core urispace.
	 * 
	 * @param uriSpaceToBeControlled
	 * @param coreUriSpace
	 * @param hopCount
	 * @return
	 */
	private String getRemainingPart(String uriSpaceToBeControlled,
			String coreUriSpace, int hopCount) {
		String remainingPart = "";
		int count = 0;
		if (!uriSpaceToBeControlled.equals(coreUriSpace)) {
			// get remaining part of big URISpace
			String splittedPart = uriSpaceToBeControlled.split(coreUriSpace)[1];
			char[] charArray = splittedPart.toCharArray();
			for (int i = 0; i < charArray.length; i++) {
				if (charArray[i] == '/' || charArray[i] == '#') {
					remainingPart += charArray[i];
					count++;
					if (count == hopCount) {
						return remainingPart;
					}
				} else {
					remainingPart += charArray[i];
				}
			}
		}
		return remainingPart;
	}

}
