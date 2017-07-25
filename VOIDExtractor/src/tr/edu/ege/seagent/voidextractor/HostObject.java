package tr.edu.ege.seagent.voidextractor;

public class HostObject {
	private String hostURL;
	private int count;
	private double countPercent;

	public HostObject(String hostURL) {
		super();
		this.hostURL = hostURL;
		this.count = 0;
		this.countPercent = 0;
	}

	public String getHostURL() {
		return hostURL;
	}

	public int getCount() {
		return count;
	}

	public void increaseHostCount() {
		count++;
	}

	public double getCountPercent() {
		return countPercent;
	}

	public void setCountPercent(double countPercent) {
		this.countPercent = countPercent;
	}

}
