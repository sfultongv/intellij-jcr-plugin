package velir.intellij.cq5.actions.content;

import velir.intellij.cq5.jcr.model.VComponent;
import velir.intellij.cq5.jcr.model.VNode;

public class NewComponent extends ANewNode {

	public VNode getNode () {
		return new VComponent("newComponent");
	}
}
