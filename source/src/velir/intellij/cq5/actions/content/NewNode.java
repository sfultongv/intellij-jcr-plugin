package velir.intellij.cq5.actions.content;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import velir.intellij.cq5.jcr.model.VNode;

public class NewNode extends ANewNode {
	@Override
	public VNode getNode() {
		return new VNode("newNode", "nt:unstructured");
	}
}
