package velir.intellij.cq5.listeners;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.TreePath;

/**
 * Created by IntelliJ IDEA.
 * User: ChristopherL
 * Date: 12/22/11
 * Time: 3:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class JcrTreeExpansionListener implements TreeExpansionListener{
	public void treeExpanded(TreeExpansionEvent event) {
		TreePath path = event.getPath();
		System.out.println(path.toString() + "expanded");
	}

	public void treeCollapsed(TreeExpansionEvent event) {
		System.out.println("collapsed");
	}
}
