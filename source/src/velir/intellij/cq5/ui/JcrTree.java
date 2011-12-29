package velir.intellij.cq5.ui;

import com.intellij.ui.treeStructure.Tree;
import velir.intellij.cq5.jcr.LightNode;

import javax.jcr.Session;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 * Represents a jcr tree in the intellij ui.
 */
public class JcrTree extends Tree {
	/**
	 * Will create a new jcr tree with the node passed as the root.
	 *
	 * @param rootNode The node to set as the root.
	 */
	public JcrTree(LightNode rootNode) {
		super();

		//don't display root node for this tree
		this.setShowsRootHandles(true);
		this.setRootVisible(false);

		//create our tree node from the node passed
		JcrTreeNode root = new JcrTreeNode(rootNode);

		//populate our root node
		root.populateChildren(true);

		//set our root node to our tree
		this.setModel(new DefaultTreeModel(root));

		//set our listeners for expand and collapse events
		this.addTreeExpansionListener(new JcrTreeExpansionHandler());
	}

	/**
	 * Will return the jcr path for a TreePath provided.
	 *
	 * @param path The TreePath to get the jcr path for.
	 * @return The jcr path for the TreePath provided.
	 */
	public String getJcrPath(TreePath path) {
		//get our last path component.
		Object lastNode = path.getLastPathComponent();

		//if our last node is a jcr tree node then grab its absolute path
		if (lastNode instanceof JcrTreeNode) {
			//cast our last node into our tree node
			JcrTreeNode treeNode = (JcrTreeNode) lastNode;

			//grab our jcr light node from our tree node
			LightNode jcrLightNode = (LightNode) treeNode.getUserObject();

			//return our jcr path
			return jcrLightNode.getPath();
		}
		return null;
	}
}
