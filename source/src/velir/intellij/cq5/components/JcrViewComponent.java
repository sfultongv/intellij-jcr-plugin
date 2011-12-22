package velir.intellij.cq5.components;

import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.treeStructure.Tree;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.jcr2dav.Jcr2davRepositoryFactory;
import velir.intellij.cq5.listeners.JcrTreeExpansionListener;

import javax.jcr.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.HashMap;
import java.util.Map;

/**
 * A component that will present the jcr repository to the user.
 */
public class JcrViewComponent extends AbstractProjectComponent {
	/**
	 * Constructor creating a new jcr view component.
	 *
	 * @param project
	 */
	protected JcrViewComponent(Project project) {
		super(project);
	}

	@Override
	public void projectOpened() {
		super.projectOpened();
		setupToolWindow();
	}

	@Override
	public void initComponent() {
		super.initComponent();
	}

	private void setupToolWindow() {
		ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(myProject);
		JBScrollPane panel = createPanel();

		ToolWindow toolWindow = toolWindowManager.registerToolWindow("CRX Repository", true, ToolWindowAnchor.LEFT);

		ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();

		Content content = contentFactory.createContent(panel, "", false);

		toolWindow.getContentManager().addContent(content);
	}

	private JBScrollPane createPanel() {
		//create our repository factory and our session objects.
		Jcr2davRepositoryFactory factory = new Jcr2davRepositoryFactory();
		Repository rep = null;
		Session session = null;

		try {
			//get our repository from our factory.
			Map<String, String> parameters = new HashMap<String, String>();
			parameters.put(JcrUtils.REPOSITORY_URI, "http://localhost:4502/crx/server");
			rep = factory.getRepository(parameters);

			//login to our repository
			session = rep.login(new SimpleCredentials("admin", "admin".toCharArray()), "crx.default");

			//pull out our root node.
			Node rootNode = session.getRootNode();
			DefaultMutableTreeNode root = new DefaultMutableTreeNode("/");

			//go through each of our root children and create a tree node
			NodeIterator ni = rootNode.getNodes();
			while (ni.hasNext()) {
				Node childNode = ni.nextNode();
				DefaultMutableTreeNode child = new DefaultMutableTreeNode(childNode.getName());
				root.add(child);
			}

			//create our JTree
			Tree tree = new Tree(root);

			//show root handle
			tree.setShowsRootHandles(true);

			tree.addTreeExpansionListener(new JcrTreeExpansionListener());

			//create and return our panel.
			return new JBScrollPane(tree);
		} catch (Exception ex) {
		} finally {
			session.logout();
			session = null;
			rep = null;
		}

		return null;
	}
}