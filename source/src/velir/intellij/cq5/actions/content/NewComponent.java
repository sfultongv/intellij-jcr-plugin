package velir.intellij.cq5.actions.content;

import velir.intellij.cq5.jcr.model.VNode;
import velir.intellij.cq5.jcr.model.VNodeDefinition;

public class NewComponent extends ANewNode {

	public VNode getNode () {
		return new VNode("newComponent", VNodeDefinition.CQ_COMPONENT);
	}
}
