package velir.intellij.cq5.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import velir.intellij.cq5.jcr.model.VNode;

import javax.swing.*;

public class NodeDialog extends DialogWrapper {

	private VNode vNode;
	private boolean isNew;

	public NodeDialog(Project project, VNode vNode, boolean isNew) {
		super(project, false);

		this.vNode = vNode;
		this.isNew = isNew;
		String title = isNew ? "New Node" : "Edit Node";

		init();
		setTitle(title);
	}

	@Override
	protected JComponent createCenterPanel() {
		return (new NodeDialogConnector(isNew, isNew, vNode)).getRootPanel();
	}

	public VNode getVNode() {
		return vNode;
	}
}
