package velir.intellij.cq5.jcr.model;

import com.intellij.openapi.util.JDOMUtil;
import org.jdom.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;

public class VNode {

	public static final String JCR_PRIMARYTYPE = "jcr:primaryType";
	public static final String BOOLEAN_PREFIX = "{Boolean}";
	public static final String DATE_PREFIX = "{Date}";
	public static final String DOUBLE_PREFIX = "{Double}";
	public static final String LONG_PREFIX = "{Long}";
	public static final String NAME_PREFIX = "{Name}";
	public static final String PATH_PREFIX = "{Path}";
	public static final String BINARY_PREFIX = "{Binary}";
	public static final String[] TYPESTRINGS = {
			"{String}",
			BOOLEAN_PREFIX,
			//DATE_PREFIX,
			DOUBLE_PREFIX,
			//NAME_PREFIX,
			//PATH_PREFIX,
			//BINARY_PREFIX,
			LONG_PREFIX,
			LONG_PREFIX + "[]",
			DOUBLE_PREFIX + "[]",
			BOOLEAN_PREFIX + "[]",
			"{String}[]"
	};
	private static final Logger log = LoggerFactory.getLogger(VNode.class);

	public static Map<String,Namespace> namespaces;

	static {
		namespaces = new HashMap<String, Namespace>();
		namespaces.put("cq", Namespace.getNamespace("cq","http://www.day.com/jcr/cq/1.0"));
		namespaces.put("jcr", Namespace.getNamespace("jcr","http://www.jcp.org/jcr/1.0"));
		namespaces.put("sling", Namespace.getNamespace("sling", "http://sling.apache.org/jcr/sling/1.0"));
		namespaces.put("slingevent", Namespace.getNamespace("slingevent", "http://sling.apache.org/jcr/sling/1.0"));
	}

	private String name;
	private Map<String, Object> properties;
	protected boolean canChangeType;

	private VNode (String name) {
		this.name = name;
		properties = new HashMap<String, Object>();
		canChangeType = false;
	}

	// constructor for new VNode, allows changing of type
	public VNode (String name, String type) {
		this.name = name;
		// populate with the default fields of this type
		VNodeDefinition vNodeDefinition = VNodeDefinition.getDefinition(type);
		if (vNodeDefinition != null) {
			properties = vNodeDefinition.getPropertiesMap(false);
		} else {
			properties = new HashMap<String, Object>();
		}
		properties.put(JCR_PRIMARYTYPE, type);
		canChangeType = true;
	}

	public void setProperty (String name, Object value) {
		properties.put(name, value);
	}

	public <T> T getProperty (String name, Class<T> type) {
		return (T) properties.get(name);
	}

	public Object getProperty (String name) {
		return properties.get(name);
	}

	public void removeProperty (String name) {
		properties.remove(name);
	}

	/**
	 * can remove property from node?
	 * @param name
	 * @return
	 */
	public boolean canRemove (String name) {
		return (! JCR_PRIMARYTYPE.equals(name));
	}

	/**
	 * can alter property?
	 * @param name
	 * @return
	 */
	public boolean canAlter (String name) {
		return true;
	}

	public String getName () {
		return name;
	}

	public void setName (String name) {
		this.name = name;
	}

	public String getType () {
		return getProperty(JCR_PRIMARYTYPE, String.class);
	}

	public String[] getSortedPropertyNames () {
		String[] propertyKeys = new String[properties.size()];
		propertyKeys = properties.keySet().toArray(propertyKeys);
		Arrays.sort(propertyKeys);
		return propertyKeys;
	}

	private String getStringValue (Object o) {

		if (o instanceof Long) {
			return LONG_PREFIX + o.toString();
		} else if (o instanceof Boolean) {
			return BOOLEAN_PREFIX + o.toString();
		} else if (o instanceof Double) {
			return DOUBLE_PREFIX + o.toString();
		} else if (o instanceof Long[]) {
			String s = LONG_PREFIX + "[";
			Long[] ls = (Long[]) o;
			if (ls.length == 0) return s + "]";
			for (int i = 0; i < ls.length - 1; i++) {
				s += ls[i].toString() + ",";
			}
			return s + ls[ls.length - 1] + "]";
		} else if (o instanceof Boolean[]) {
			String s = BOOLEAN_PREFIX + "[";
			Boolean[] ls = (Boolean[]) o;
			if (ls.length == 0) return s + "]";
			for (int i = 0; i < ls.length - 1; i++) {
				s += ls[i].toString() + ",";
			}
			return s + ls[ls.length - 1] + "]";
		} else if (o instanceof Double[]) {
			String s = DOUBLE_PREFIX + "[";
			Double[] ls = (Double[]) o;
			if (ls.length == 0) return s + "]";
			for (int i = 0; i < ls.length - 1; i++) {
				s += ls[i].toString() + ",";
			}
			return s + ls[ls.length - 1] + "]";
		} else if (o instanceof String[]) {
			String s = "[";
			String[] ss = (String[]) o;
			if (ss.length == 0) return s + "]";
			for (int i = 0; i < ss.length - 1; i++) {
				s += ss[i] + ",";
			}
			return s + ss[ss.length - 1] + "]";
		} else {
			return o.toString();
		}
	}

	public Element getElement() {
		Element element = new Element("root", namespaces.get("jcr"));
		Set<String> elementNamespaces = new HashSet<String>();

		// properties
		for (Map.Entry<String,Object> property : properties.entrySet()) {

			// get namespace from property string, if there
			Namespace propertyNamespace = null;
			String propertyName = property.getKey();
			String[] attributeSections = propertyName.split(":");
			// if namespaced property
			if (attributeSections.length == 2) {
				propertyNamespace = namespaces.get(attributeSections[0]);
				if (propertyNamespace == null) {
					log.error("No namespace definition found for property: " + property.getKey());
				}
				else {
					propertyName = attributeSections[1];
					// add namespace to element if it isn't there already
					if (!elementNamespaces.contains(attributeSections[0])) {
						element.addNamespaceDeclaration(propertyNamespace);
						elementNamespaces.add(attributeSections[0]);
					}
				}
			}

			// prepend string value with property type
			Object value = property.getValue();
			String propertyStringValue = getStringValue(value);

			// set property
			if (propertyNamespace != null) {
				// propertyName cannot have colon, even here
				element.setAttribute(propertyName, propertyStringValue, propertyNamespace);
			}
			else {
				element.setAttribute(propertyName, propertyStringValue);
			}
		}

		return element;
	}

	public static VNode makeVNode (InputStream inputStream, String name) throws JDOMException, IOException {
		VNode vNode = makeVNode(inputStream);
		vNode.setName(name);
		return vNode;
	}

	public static VNode makeVNode (InputStream inputStream) throws JDOMException, IOException {
		Document document = JDOMUtil.loadDocument(inputStream);
		final Element element = document.getRootElement();
		return makeVNode(element);
	}

	public static VNode makeVNode (final Element element) throws JDOMException, IOException {
		String name = element.getName();
		String namespace = element.getNamespacePrefix();
		if (!namespace.equals("")) name = namespace + ":" + name;
		VNode vNode = new VNode(name);

		for (Object o : element.getAttributes()) {
			Attribute attribute = (Attribute) o;

			String propertyName = attribute.getQualifiedName();
			String value = attribute.getValue();

			// choose which type of object to insert
			if (value.startsWith(BOOLEAN_PREFIX + "[")) {
				Boolean[] vals;
				String valuesString = value.substring(0, value.length() - 1).replaceFirst(Pattern.quote(BOOLEAN_PREFIX + "["), "");
				if ("".equals(valuesString)) vals = new Boolean[0];
				else {
					String[] valueBits = valuesString.split(",");
					vals = new Boolean[valueBits.length];
					for (int i = 0; i < valueBits.length; i++) {
						vals[i] = Boolean.parseBoolean(valueBits[i]);
					}
				}
				vNode.setProperty(propertyName, vals);
			} else if (value.startsWith(BOOLEAN_PREFIX)) {
				Boolean b = Boolean.parseBoolean(value.replaceFirst(Pattern.quote(BOOLEAN_PREFIX), ""));
				vNode.setProperty(propertyName, b);
			} else if (value.startsWith(DOUBLE_PREFIX + "[")) {
				Double[] vals;
				String valuesString = value.substring(0, value.length() - 1).replaceFirst(Pattern.quote(DOUBLE_PREFIX + "["), "");
				if ("".equals(valuesString)) vals = new Double[0];
				else {
					String[] valueBits = valuesString.split(",");
					vals = new Double[valueBits.length];
					for (int i = 0; i < valueBits.length; i++) {
						vals[i] = Double.parseDouble(valueBits[i]);
					}
				}
				vNode.setProperty(propertyName, vals);
			} else if (value.startsWith(DOUBLE_PREFIX)) {
				Double d = Double.parseDouble(value.replaceFirst(Pattern.quote(DOUBLE_PREFIX), ""));
				vNode.setProperty(propertyName, d);
			} else if (value.startsWith(LONG_PREFIX + "[")) {
				Long[] vals;
				String valuesString = value.substring(0, value.length() - 1).replaceFirst(Pattern.quote(LONG_PREFIX + "["), "");
				if ("".equals(valuesString)) vals = new Long[0];
				else {
					String[] valueBits = valuesString.split(",");
					vals = new Long[valueBits.length];
					for (int i = 0; i < valueBits.length; i++) {
						vals[i] = Long.parseLong(valueBits[i]);
					}
				}
				vNode.setProperty(propertyName, vals);
			} else if (value.startsWith(LONG_PREFIX)) {
				Long l = Long.parseLong(value.replaceFirst(Pattern.quote(LONG_PREFIX), ""));
				vNode.setProperty(propertyName, l);
			} else if (value.startsWith("[")) {
				String[] vals;
				String valuesString = value.substring(1, value.length() - 1);
				if ("".equals(valuesString)) vals = new String[0];
				else  vals = valuesString.split(",");
				vNode.setProperty(propertyName, vals);
			} else {
				vNode.setProperty(propertyName, value);
			}
		}

		vNode.canChangeType = false;

		return vNode;
	}

}
