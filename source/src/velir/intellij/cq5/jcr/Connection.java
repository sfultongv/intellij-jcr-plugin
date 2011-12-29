package velir.intellij.cq5.jcr;

import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.jcr2dav.Jcr2davRepositoryFactory;

import javax.jcr.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Used to create a new connection to the jcr.
 */
public class Connection {
	private static final String REPOSITORY_URL = "http://localhost:4502/crx/server";
	private static final String USERNAME = "admin";
	private static final String PASSWORD = "admin";
	private static final String WORKSPACE = "crx.default";

	/**
	 * Will retrieve a repository factory for getting the crx repository.
	 *
	 * @return
	 */
	public static RepositoryFactory getRepositoryFactory() {
		//return a new jcr2dav repository factory.
		return new Jcr2davRepositoryFactory();
	}

	/**
	 * Will return the crx repository.
	 *
	 * @return
	 */
	public static Repository getRepository() throws RepositoryException {
		//get our repository factory
		RepositoryFactory factory = Connection.getRepositoryFactory();

		//create our parameters to pass into our factory
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put(JcrUtils.REPOSITORY_URI, Connection.REPOSITORY_URL);

		//get our repository from our factory
		return factory.getRepository(parameters);
	}

	/**
	 * Will return our credentials to login to the repository.
	 *
	 * @return
	 */
	public static Credentials getCredentials() {
		return new SimpleCredentials(Connection.USERNAME, Connection.PASSWORD.toCharArray());
	}

	/**
	 * Will return a session to the crx repository.
	 *
	 * @return
	 */
	public static Session getSession() throws RepositoryException {
		//get our repository
		Repository rep = Connection.getRepository();

		//login to our repository and return our session
		return rep.login(Connection.getCredentials(), Connection.WORKSPACE);
	}
}
