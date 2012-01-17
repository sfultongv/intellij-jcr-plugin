package velir.intellij.cq5.jcr.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

public class VPropertyDefinition {
	public static final String STRING = "STRING";
	public static final String URI = "URI";
	public static final String BINARY = "BINARY";
	public static final String LONG = "LONG";
	public static final String DOUBLE = "DOUBLE";
	public static final String DECIMAL = "DECIMAL";
	public static final String BOOLEAN = "BOOLEAN";
	public static final String DATE = "DATE";
	public static final String NAME = "NAME";
	public static final String PATH = "PATH";
	public static final String REFERENCE = "REFERENCE";
	public static final String WEAKREFERENCE = "WEAKREFERENCE";
	public static final String UNDEFINED = "UNDEFINED";
	public static final String JCR_REQUIREDTYPE = "jcr:requiredType";
	public static final String JCR_MULTIPLE = "jcr:multiple";

	private static final Logger log = LoggerFactory.getLogger(VPropertyDefinition.class);

	private String type;
	private boolean multiValued;

	VPropertyDefinition (Node node) throws RepositoryException {
			type = node.getProperty(JCR_REQUIREDTYPE).getString();
			multiValued = node.getProperty(JCR_MULTIPLE).getBoolean();

	}

	Object getDefaultValue() {
		if (multiValued) {
			if (LONG.equals(type)) return new Long[] {0L};
			else if (DOUBLE.equals(type)) return new Double[] {0.0D};
			else if (BOOLEAN.equals(type)) return new Boolean[] {false};
			else return new String[] {""};
		} else {
			if (LONG.equals(type)) return 0L;
			else if (DOUBLE.equals(type)) return 0.0D;
			else if (BOOLEAN.equals(type)) return false;
			else return "";
		}
	}

}
