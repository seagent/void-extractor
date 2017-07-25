package tr.edu.ege.seagent.voidextractor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.hp.hpl.jena.ontology.OntModel;

public class FileOperations {

	public static final String LINKSETS_ITEM_FILENAME_WITHOUT_NUMBER = "/linksetsItem";
	public static final String TXT_EXTENSION = ".txt";
	public static final String VOID_DIRECTORY_NAME = "voids";
	public static final String SMALL_VOIDS_DIRECTORY_NAME = "allVoids/smallvoids";
	public static final String BIG_VOIDS_DIRECTORY_NAME = "allVoids/bigvoids";
	public static final String GIANT_VOIDS_DIRECTORY_NAME = "allVoids/giantvoids";
	public static final String FAILED_FILES_PATH = "/failedEndpoints.txt";
	public static final String BIG_DATASETS_ENDPOINTS_PATH = "/bigDatasetsEndpoint.txt";
	public static final String COMPLETED_ENDPOINTS_PATH = "/completedEndpoints.txt";
	public static final String LINKSET_ITEMS_FILE_FULL_NAME = LINKSETS_ITEM_FILENAME_WITHOUT_NUMBER
			+ TXT_EXTENSION;
	public static final String FULL_VOIDS = "fullvoids";
	public static final String DBPEDIA_DIR_NAME = "allVoids/giantvoids/dbpedia/";
	public static final String GEODATA_DIR_NAME = "allVoids/giantvoids/geodata/";
	public static final String COMPLETE_VOIDS = "allVoids/completevoids";
	public static final String BACKUP_VOIDS = "allVoids/backupVoids";
	public static final String AVAILABLE_VOIDS = "allVoids/availablevoids";
	public static final String MANUAL_CREATION_DIR = "allVoids/manualCreation";
	public static final String TEST_VOIDS = "allVoids/testvoids";
	public static final String VIRTUAL_LINKSET_VOIDS = "allVoids/testvoids/virtuallinksetvoids";
	public static final String REFRESHED_VOIDS = "allVoids/refreshedvoids";
	public static final String BROKEN_ENDPOINTS_PATH = "allVoids/refreshedvoids/brokenEndpoints.txt";
	public static final String NON_VIRTUAL_VOIDS = "voidsfortest/nonvirtualvoids";
	public static final String DEFECTED_VOIDS = "allVoids/defectedvoids";
	public static final String FIXED_VOIDS = "allVoids/fixedvoids";
	public static final String CLEANSED_73_VOIDS = "allVoids/cleansed/73datasets";
	public static final String CLEANSED_60_VOIDS = "allVoids/cleansed/60datasets";
	public static final String CLEANSED_50_VOIDS = "allVoids/cleansed/50datasets";
	public static final String CLEANSED_40_VOIDS = "allVoids/cleansed/40datasets";
	public static final String CLEANSED_30_VOIDS = "allVoids/cleansed/30datasets";
	public static final String CLEANSED_20_VOIDS = "allVoids/cleansed/20datasets";
	public static final String CLEANSED_9_VOIDS = "allVoids/cleansed/09datasets";

	/**
	 * It writes or apppends the given string to the file.
	 * 
	 * @param path
	 * @param append
	 * @param row
	 * @throws IOException
	 */
	public static void writeFile(String path, boolean append, String row)
			throws IOException {
		File file = new File(path);
		file.createNewFile();
		FileWriter writer = new FileWriter(file, append);
		writer.append(row + "\n");
		writer.close();
	}

	/**
	 * It deletes the given file.
	 * 
	 * @param path
	 */
	public static void deleteFile(String path) {
		File file = new File(path);
		file.delete();
	}

	/**
	 * It writes void model to a file.
	 * 
	 * @param ontmodel
	 * @param directoryName
	 * @throws IOException
	 */
	public static void writeVoidModelFile(OntModel ontmodel,
			int voidFileNumber, String directoryName) throws IOException {
		File file = new File(directoryName
				+ Vocabulary.VOID_FILE_LOCATION_PREFIX + voidFileNumber
				+ ".owl");
		file.createNewFile();
		ontmodel.write(new FileWriter(file));

	}

}
