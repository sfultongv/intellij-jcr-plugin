package velir.intellij.cq5.jcr.model;

public class VComponent extends VNode {
	public static final String CQ_COMPONENT = "cq:Component";
	public static final String CQ_ISCONTAINER = "cq:isContainer";
	public static final String JCR_TITLE = "jcr:title";
	public static final String ALLOWED_PARENTS = "allowedParents";
	public static final String COMPONENT_GROUP = "componentGroup";

	public VComponent (String name) {
		super(name, CQ_COMPONENT);
		setProperty(CQ_ISCONTAINER, false);
		setProperty(JCR_TITLE, "");
		setProperty(ALLOWED_PARENTS, "");
		setProperty(COMPONENT_GROUP, "General");
	}

	public String getAllowedParents() {
		return getPropertyString(ALLOWED_PARENTS);
	}

	public void setAllowedParents(String allowedParents) {
		setProperty(ALLOWED_PARENTS,allowedParents);
	}

	public String getComponentGroup() {
		return getPropertyString(COMPONENT_GROUP);
	}

	public void setComponentGroup(String componentGroup) {
		setProperty(COMPONENT_GROUP,componentGroup);
	}

	public String getTitle() {
		return getPropertyString(JCR_TITLE);
	}

	public void setTitle(String title) {
		setProperty(JCR_TITLE, title);
	}

	public boolean isContainer() {
		return getPropertyBoolean(CQ_ISCONTAINER);
	}

	public void setContainer(boolean container) {
		setProperty(CQ_ISCONTAINER, container);
	}

	public String getPrimaryType() {
		return getPropertyString(CQ_COMPONENT);
	}
}
