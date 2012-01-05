package velir.intellij.cq5.jcr.model;

import org.jdom.Element;
import org.jdom.Namespace;

public class VComponent implements VBase {
	private String name;
	private String allowedParents;
	private String componentGroup;
	private String title;
	private boolean container;

	private static final Namespace CQ_NS = Namespace.getNamespace("cq", "http://www.day.com/jcr/cq/1.0");
	private static final Namespace JCR_NS = Namespace.getNamespace("jcr", "http://www.jcp.org/jcr/1.0");

	public VComponent () {
		name = "";
		allowedParents = "";
		componentGroup = "";
		title = "";
		container = false;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAllowedParents() {
		return allowedParents;
	}

	public void setAllowedParents(String allowedParents) {
		this.allowedParents = allowedParents;
	}

	public String getComponentGroup() {
		return componentGroup;
	}

	public void setComponentGroup(String componentGroup) {
		this.componentGroup = componentGroup;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isContainer() {
		return container;
	}

	public void setContainer(boolean container) {
		this.container = container;
	}

	public String getPrimaryType() {
		return "cq:Component";
	}

	public Element getElement() {
		Element element = new Element(getName());
		element.addNamespaceDeclaration(CQ_NS);
		element.addNamespaceDeclaration(JCR_NS);
		element.setAttribute("title", getTitle(), JCR_NS);
		element.setAttribute("primaryType", getPrimaryType(), JCR_NS);
		element.setAttribute("isContainer", "{Boolean}" + isContainer(), CQ_NS);
		element.setAttribute("allowedParents", getAllowedParents());
		return element;
	}

}
