package velir.intellij.cq5.ui;

import org.apache.commons.lang.NullArgumentException;
import velir.intellij.cq5.jcr.Connection;
import velir.intellij.cq5.jcr.LightNode;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Represents a jcr tree node in the intellij ui.
 */
public class JcrTreeNode extends DefaultMutableTreeNode {
	/**
	 * The jcr node of this tree node.
	 */
	private LightNode node;

	/**
	 * Whether or not this nodes children have been populated in the tree.
	 */
	private boolean populated;

	/**
	 * Whether or not this nodes in in an interim state.  This is true if we
	 * populated the first child to simply display the expand handle on this node.
	 */
	private boolean interim;

	/**
	 * Creates a new jcr tree node from the light node provided.
	 *
	 * @param node light node to add to the jcr tree.
	 */
	public JcrTreeNode(LightNode node) {
		//verify we were provided a node
		if (node == null) {
			throw new NullArgumentException("node");
		}

		//set our node.
		this.node = node;

		// Hold the node as the user object.
		this.setUserObject(node);
	}

	/**
	 * Override isLeaf to check whether this is a directory
	 *
	 * @return Whether or not this node is a leaf.
	 */
	public boolean isLeaf() {
		return !this.node.hasChildren();
	}

	/**
	 * Override getAllowsChildren to check whether this is a directory
	 *
	 * @return Whether or not this node allows children.
	 */
	public boolean getAllowsChildren() {
		return this.node.hasChildren();
	}

	/**
	 * Override toString so our name is displayed in the tree.
	 *
	 * @return The string representation of this node.
	 */
	public String toString() {
		//return our name
		return this.node.getName();
	}

	/**
	 * Will populate our children in the jcr tree.
	 *
	 * @param descend Whether or not to also populate child node's children.
	 * @return Whether or not nodes were added to the tree.
	 */
	boolean populateChildren(boolean descend) {
		//call our overloaded method without a session
		return this.populateChildren(descend, null);
	}

	/**
	 * Will populate our children in the jcr tree.
	 *
	 * @param descend Whether or not to also populate child node's children.
	 * @param session The session to the repository.
	 * @return Whether or not nodes were added to the tree.
	 */
	boolean populateChildren(boolean descend, Session session) {
		//if our children are already populated then return false
		//no need to add any nodes
		if (this.populated) {
			return false;
		}

		//if we are a leaf node then we don't have children.
		if (this.isLeaf()) {
			//set that we are populated
			this.populated = true;

			//return false because no nodes were added
			return false;
		}

		//if we are in an interim state then a dummy node was added to the tree
		//we should remove this
		if (this.interim) {
			//remove all our children
			this.removeAllChildren();

			//set our interim state to false
			this.interim = false;
		}

		//initialize flag telling if we added child nodes.
		boolean addedNodes = false;

		//pull out our child nodes from the repository.
		boolean sessionCreated = false;
		try {
			//if we weren't provided a session then get a session
			if (session == null) {
				//get our session
				session = Connection.getSession();

				//set our flag indicating that we created our session
				sessionCreated = true;
			}

			//pull out our current jcr node.
			Node jcrNode = session.getNode(this.node.getPath());

			//go through each of our nodes children and add them to the tree
			NodeIterator ni = jcrNode.getNodes();
			while (ni.hasNext()) {
				//get a light node from our next child node.
				LightNode childNode = new LightNode(ni.nextNode());

				//create our tree node from our jcr light node.
				JcrTreeNode childTreeNode = new JcrTreeNode(childNode);

				//add our tree node to our tree.
				this.add(childTreeNode);

				//if we are flagged to populate our descendants then populate our children.
				if (descend) {
					childTreeNode.populateChildren(false, session);
				}

				//flag that we added nodes.
				addedNodes = true;

				//if we aren't flagged to populate our descendants then break out of loop
				//this will let us add the first child so the expand handle is displayed
				//for the node, but save on processing time.
				if (!descend) {
					break;
				}
			}
		} catch (RepositoryException rex) {
		} finally {
			//if we created our session and it isn't null then logout
			if (sessionCreated && session != null) {
				session.logout();
			}
		}

		//if we scanned all sub directories then we should be populated
		//also set as populated if we haven't added any nodes.
		if (descend || !addedNodes) {
			this.populated = true;
		} else {
			//we are just in an interim state
			this.interim = true;
		}

		//return if we added nodes.
		return addedNodes;
	}
}
