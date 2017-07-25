package tr.edu.ege.seagent.voidextractor;

import java.net.MalformedURLException;
import java.net.URI;

public class URISpaceObject {

	private String predicate;
	private String object;
	private String hostURL;

	public String getHostURL() {
		return hostURL;
	}

	public URISpaceObject(String predicate, String object) {
		super();
		this.predicate = predicate;
		this.object = object;
		URI uri = URI.create(object);
		try {
			this.hostURL = uri.toURL().getProtocol() + "://" + uri.getHost();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public String getPredicate() {
		return predicate;
	}

	public String getObject() {
		return object;
	}

}
