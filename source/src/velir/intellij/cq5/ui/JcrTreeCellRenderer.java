package velir.intellij.cq5.ui;

import velir.intellij.cq5.jcr.LightNode;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * Custom tree cell render for rendering our jcr tree cells.
 */
public class JcrTreeCellRenderer extends DefaultTreeCellRenderer {
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		//call our superclass functionality
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

		//if our value is null then just return
		if (value == null) {
			return this;
		}

		//if we are not of the proper type then just return.
		//extra nodes that are not JcrTreeNode objects are being passed in for some reason.
		if (!(value instanceof JcrTreeNode)) {
			return this;
		}

		//get our node.
		JcrTreeNode node = (JcrTreeNode) value;

		//get our light node.
		LightNode lightNode = (LightNode) node.getUserObject();

		//if we don't have a light node then just return
		if (lightNode == null) {
			return this;
		}

		//pull out our node type
		String nodeType = lightNode.getPrimaryNodeType();

		//if we don't have a node type then just return
		if (nodeType == null || "".equals(nodeType)) {
			return this;
		}

		//split our node type on the colon
		String[] split = nodeType.split(":");

		//if we don't have exactly 2 parts then just return
		if (split.length != 2) {
			return this;
		}

		//pull out our folder and file names
		String folder = split[0];
		String file = split[1];

		//get our icon
		Icon icon = getIcon("/velir/intellij/cq5/ui/images/icons/" + folder + "/" + file + ".gif");

		//if we didn't get an icon then get our default empty icon
		if(icon == null){
			icon = getIcon("/velir/intellij/cq5/ui/images/icons/empty.gif");
		}

		//set our icon and return
		setIcon(icon);
		return this;
	}

	/**
	 * Will get our icon from the provided path.
	 * @param path The path of the icon.
	 * @return The icon or null.
	 */
	private Icon getIcon(String path){
		//get our icon stream for our icon.
		InputStream iconStream = this.getClass().getResourceAsStream(path);

		//try to pull out our icon from our icon stream
		Icon icon = null;
		try {
			//if we don't have an icon, just return
			if (iconStream != null && iconStream.available() > 0) {
				//pull out our image bytes from the icon stream.
				byte[] imageData = new byte[iconStream.available()];
				iconStream.read(imageData);

				//create a new image icon from our bytes.
				icon = new ImageIcon(imageData);
			}
		} catch (IOException ex) {
			icon = null;
		}

		//return our icon
		return icon;
	}
}
