package velir.intellij.cq5.jcr;

import org.apache.commons.lang.NullArgumentException;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

/**
 * Represents a light jcr Node that contains basic information
 * about a Node.
 */
public class LightNode {
	/**
	 * The name of the jcr Node.
	 */
	private String name;

	/**
	 * The path of this jcr Node in the jcr structure.
	 */
	private String path;

	/**
	 * The node type for this node.
	 */
	private String primaryNodeType;

	/**
	 * Whether or not this node has children
	 */
	private boolean hasChildren;

	/**
	 * Creates a new light jcr node for representation outside of immediate operations (ex. tree representation).
	 *
	 * @param node The original jcr Node.
	 * @throws RepositoryException
	 */
	public LightNode(Node node) throws RepositoryException {
		//verify we were provided a node
		if (node == null) {
			throw new NullArgumentException("node");
		}

		//populate our properties from our node
		this.name = node.getName();
		this.path = node.getPath();
		this.primaryNodeType = node.getPrimaryNodeType().getName();
		this.hasChildren = node.hasNodes();
	}

	/**
	 * Will return the name of this node.
	 *
	 * @return The name of this node.
	 */
	public String getName() {
		//return our name
		return this.name;
	}

	/**
	 * Will return the path of this node.
	 *
	 * @return The path of this node in the jcr structure.
	 */
	public String getPath() {
		//return our path
		return this.path;
	}

	public String getPrimaryNodeType() {
		//return our primary node type
		return this.primaryNodeType;
	}

	/**
	 * Returns if this node has children.
	 *
	 * @return Flag telling if this node has children or not.
	 */
	public boolean hasChildren() {
		//return if our node has children
		return this.hasChildren;
	}
}
