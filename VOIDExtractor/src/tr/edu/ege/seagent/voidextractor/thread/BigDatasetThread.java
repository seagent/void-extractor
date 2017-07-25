package tr.edu.ege.seagent.voidextractor.thread;

import java.util.List;
import java.util.Vector;

import tr.edu.ege.seagent.voidextractor.FileOperations;
import tr.edu.ege.seagent.voidextractor.VOIDExtractor;


public class BigDatasetThread extends Thread {

	private int voidFileNumber;
	private List<String> bigEndpoints;
	private VOIDExtractor voidExtractor;

	public BigDatasetThread(String endpoint, int voidFileNumber,
			VOIDExtractor voidExtractor) {
		this.voidExtractor = voidExtractor;
		this.bigEndpoints = new Vector<String>();
		this.bigEndpoints.add(endpoint);
		this.voidFileNumber = voidFileNumber;
	}

	@Override
	public void run() {
		try {
			int isExtracted = voidExtractor.extractOneEndpoint(bigEndpoints,
					true, FileOperations.BIG_VOIDS_DIRECTORY_NAME, 0,
					voidFileNumber);
			// write big dataset linkset item list for each endpoint...
			if (isExtracted != -1)
				voidExtractor.writeLinksetSPOForBigDatasets(
						FileOperations.BIG_VOIDS_DIRECTORY_NAME, voidExtractor
								.getLinksetsList().get(0), voidFileNumber);
		} catch (Exception e) {

			e.printStackTrace();
		}
	}
}
