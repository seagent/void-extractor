package tr.edu.ege.seagent.voidextractor.thread;

import com.hp.hpl.jena.query.QueryExecution;

public class EndpointTimeoutThread extends Thread {
	private QueryExecution exec;

	public QueryExecution getExec() {
		return exec;
	}

	public EndpointTimeoutThread(QueryExecution exec) {
		this.exec = exec;
	}

	private int askResult = -1;

	public void setAskResult(int askResult) {
		this.askResult = askResult;
	}

	public int getAskResult() {
		return askResult;
	}

	@Override
	public void run() {
		boolean isValid = false;
		try {
			isValid = exec.execAsk();
			if (!isValid) {
				System.out.println("Thread" + this.getId()
						+ " is not valid and will be removed.");
				exec.close();
				setExec(null);
				askResult = 0;
			} else {
				// set ask true
				askResult = 1;
				exec.close();
			}
		} catch (Exception e) {
			System.out.println("Thread" + this.getId()
					+ " is not valid and will be removed.");
			exec.close();
			setExec(null);
		}
	}

	private void setExec(Object object) {
		this.exec = null;

	}
}
