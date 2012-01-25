package velir.intellij.cq5.jcr;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.jcr2dav.Jcr2davRepositoryFactory;

import javax.jcr.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Used to create a new connection to the jcr.
 */
@State(
		name = "CQ5.Project.Settings",
		storages = {@Storage(id = "default", file = "$PROJECT_FILE$")}
)
public class Connection implements PersistentStateComponent<Connection.State>{
	public static class State {
		public String url;
		public String username;
		public String password;
		public String workspace;
	}

	private static final String REPOSITORY_URL = "http://localhost:4502/crx/server";
	private static final String USERNAME = "admin";
	private static final String PASSWORD = "admin";
	private static final String WORKSPACE = "crx.default";

	private State state;

	private Connection () {
		state = new State();
		state.url = REPOSITORY_URL;
		state.username = USERNAME;
		state.password = PASSWORD;
		state.workspace = WORKSPACE;
	}

	public static Connection getInstance(Project project) {
		return ServiceManager.getService(project, Connection.class);
	}

	/**
	 * Will retrieve a repository factory for getting the crx repository.
	 *
	 * @return
	 */
	public RepositoryFactory getRepositoryFactory() {
		//return a new jcr2dav repository factory.
		return new Jcr2davRepositoryFactory();
	}

	/**
	 * Will return the crx repository.
	 *
	 * @return
	 */
	public Repository getRepository() throws RepositoryException {
		//get our repository factory
		RepositoryFactory factory = getRepositoryFactory();

		//create our parameters to pass into our factory
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put(JcrUtils.REPOSITORY_URI, state.url);

		//get our repository from our factory
		return factory.getRepository(parameters);
	}

	/**
	 * Will return our credentials to login to the repository.
	 *
	 * @return
	 */
	public Credentials getCredentials() {
		return new SimpleCredentials(state.username, state.password.toCharArray());
	}

	/**
	 * Will return a session to the crx repository.
	 *
	 * @return
	 */
	public Session getSession() throws RepositoryException {
		//get our repository
		Repository rep = getRepository();

		// abort if we couldn't get a repository
		if (rep == null) throw new RepositoryException("Could not get repository (velir code)");

		//login to our repository and return our session
		return rep.login(getCredentials(), state.workspace);
	}

	public State getState() {
		return state;
	}

	public void loadState(State state) {
		this.state = state;
	}
}
