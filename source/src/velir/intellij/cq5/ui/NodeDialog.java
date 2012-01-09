package velir.intellij.cq5.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import velir.intellij.cq5.jcr.model.VNode;

import javax.swing.*;

public class NodeDialog extends DialogWrapper {

	private VNode vNode;

	public NodeDialog(Project project, VNode vNode, boolean isNew) {
		super(project, false);

		this.vNode = vNode;
		String title = isNew ? "New Node" : "Edit Node";

		init();
		setTitle(title);
	}

	@Override
	protected JComponent createCenterPanel() {
		return vNode.makePanel();
	}

	public VNode getVNode() {
		return vNode;
	}
}
