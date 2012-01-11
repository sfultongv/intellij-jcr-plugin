package velir.intellij.cq5.jcr.model;

import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.openapi.util.JDOMUtil;
import org.jdom.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import velir.intellij.cq5.swing.RegexTextField;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;

public class VNode {

	private static final String BOOLEAN_PREFIX = "{Boolean}";
	private static final String DATE_PREFIX = "{Date}";
	private static final String DOUBLE_PREFIX = "{Double}";
	private static final String LONG_PREFIX = "{Long}";
	private static final String NAME_PREFIX = "{Name}";
	private static final String PATH_PREFIX = "{Path}";
	private static final String BINARY_PREFIX = "{Binary}";
	private static final String[] TYPESTRINGS = {
			"{String}",
			BOOLEAN_PREFIX,
			//DATE_PREFIX,
			DOUBLE_PREFIX,
			//NAME_PREFIX,
			//PATH_PREFIX,
			//BINARY_PREFIX,
			LONG_PREFIX
	};

	private String name;
	private Map<String, Object> properties;
	private static final Logger log = LoggerFactory.getLogger(VNode.class);

	public static Map<String,Namespace> namespaces;
	static {
		namespaces = new HashMap<String, Namespace>();
		namespaces.put("cq", Namespace.getNamespace("cq","http://www.day.com/jcr/cq/1.0"));
		namespaces.put("jcr", Namespace.getNamespace("jcr","http://www.jcp.org/jcr/1.0"));
	}

	public VNode (String name, String type) {
		this.name = name;
		properties = new HashMap<String, Object>();
		properties.put("jcr:primaryType", type);
	}

	protected void setProperty (String name, Object value) {
		properties.put(name, value);
	}

	protected String getPropertyString (String name) {
		return (String) properties.get(name);
	}

	protected Boolean getPropertyBoolean (String name) {
		return (Boolean) properties.get(name);
	}

	protected Long getPropertyLong (String name) {
		return (Long) properties.get(name);
	}

	protected <T> T getProperty (String name, Class<T> type) {
		return (T) properties.get(name);
	}

	protected void removeProperty (String name) {
		properties.remove(name);
	}

	public String getName () {
		return name;
	}

	protected void setName (String name) {
		this.name = name;
	}

	private String getStringValue (Object o) {
		if (o instanceof Long) {
			return LONG_PREFIX + o.toString();
		} else if (o instanceof Boolean) {
			return BOOLEAN_PREFIX + o.toString();
		} else if (o instanceof Double) {
			return DOUBLE_PREFIX + o.toString();
		} else {
			return o.toString();
		}
	}

	public Element getElement() {
		Element element = new Element(name);
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

	private void addPropertyPanel (final JPanel parentPanel, final String name, final Object value) {
		final JPanel jPanel = new JPanel(new GridLayout(1,3));

		// make sure the property is set in the node
		setProperty(name, value);

		// make label
		JLabel jLabel = new JLabel(name);
		jPanel.add(jLabel);

		// make input based on property class
		if (value instanceof Boolean) {
			final JCheckBox jCheckBox = new JCheckBox("", (Boolean) value);
			jCheckBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setProperty(name, jCheckBox.isSelected());
				}
			});
			jPanel.add(jCheckBox);
		} else if (value instanceof Double) {
			final RegexTextField regexTextField = new RegexTextField(Pattern.compile("[0-9]*\\.?[0-9]*"), value.toString());
			regexTextField.getDocument().addDocumentListener(new DocumentListener() {
				public void insertUpdate(DocumentEvent e) {
					setProperty(name, Double.parseDouble(regexTextField.getText()));
				}

				public void removeUpdate(DocumentEvent e) {
					setProperty(name, Double.parseDouble(regexTextField.getText()));
				}

				public void changedUpdate(DocumentEvent e) {
					setProperty(name, Double.parseDouble(regexTextField.getText()));
				}
			});
			jPanel.add(regexTextField);
		} else if (value instanceof Long) {
			final RegexTextField regexTextField = new RegexTextField(Pattern.compile("[0-9]*"), value.toString());
			regexTextField.getDocument().addDocumentListener(new DocumentListener() {
				public void insertUpdate(DocumentEvent e) {
					setProperty(name, Long.parseLong(regexTextField.getText()));
				}

				public void removeUpdate(DocumentEvent e) {
					setProperty(name, Long.parseLong(regexTextField.getText()));
				}

				public void changedUpdate(DocumentEvent e) {
					setProperty(name, Long.parseLong(regexTextField.getText()));
				}
			});
			jPanel.add(regexTextField);
		} else {
			final JTextField jTextField = new JTextField(value.toString());
			jTextField.getDocument().addDocumentListener(new DocumentListener() {
				public void insertUpdate(DocumentEvent e) {
					setProperty(name, jTextField.getText());
				}

				public void removeUpdate(DocumentEvent e) {
					setProperty(name, jTextField.getText());
				}

				public void changedUpdate(DocumentEvent e) {
					setProperty(name, jTextField.getText());
				}
			});
			jPanel.add(jTextField);
		}

		// make remove button
		JButton jButton = new JButton("remove");
		jButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parentPanel.remove(jPanel);
				parentPanel.revalidate();
				removeProperty(name);
			}
		});
		jPanel.add(jButton);

		parentPanel.add(jPanel);
	}

	public JPanel makePanel (boolean nameEditingEnabled) {
		JPanel nodePanel = new JPanel(new VerticalFlowLayout());

		// node name
		JPanel namePanel = new JPanel(new GridLayout(1,2));
		JLabel nameLabel = new JLabel("name");
		namePanel.add(nameLabel);
		final JTextField nameField = new JTextField(name);
		nameField.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				name = nameField.getText();
			}

			public void removeUpdate(DocumentEvent e) {
				name = nameField.getText();
			}

			public void changedUpdate(DocumentEvent e) {
				name = nameField.getText();
			}
		});
		nameField.setEditable(nameEditingEnabled);
		namePanel.add(nameField);
		nodePanel.add(namePanel);

		// separator
		nodePanel.add(new JSeparator(JSeparator.HORIZONTAL));

		// properties
		final JPanel propertiesPanel = new JPanel(new VerticalFlowLayout());
		for (Map.Entry<String,Object> property : properties.entrySet()) {
			addPropertyPanel(propertiesPanel, property.getKey(), property.getValue());
		}
		nodePanel.add(propertiesPanel);

		// separator
		nodePanel.add(new JSeparator(JSeparator.HORIZONTAL));

		// make add property panel
		JPanel newPropertyPanel = new JPanel(new GridLayout(1,2));
		final JTextField jTextField = new JTextField();
		newPropertyPanel.add(jTextField);
		final JComboBox jComboBox = new JComboBox(TYPESTRINGS);
		newPropertyPanel.add(jComboBox);
		JButton jButton = new JButton("add property");
		jButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String type = (String) jComboBox.getSelectedItem();
				if (BOOLEAN_PREFIX.equals(type)) {
					addPropertyPanel(propertiesPanel, jTextField.getText(), false);
				} else if (LONG_PREFIX.equals(type)) {
					addPropertyPanel(propertiesPanel, jTextField.getText(), 0L);
				} else if (DOUBLE_PREFIX.equals(type)) {
					addPropertyPanel(propertiesPanel, jTextField.getText(), 0.0D);
				} else {
					addPropertyPanel(propertiesPanel, jTextField.getText(), "");
				}
				propertiesPanel.revalidate();
			}
		});
		newPropertyPanel.add(jButton);
		nodePanel.add(newPropertyPanel);

		return nodePanel;
	}


	public static VNode makeVNode (InputStream inputStream, String name) {
		VNode vNode = null;
		try {
			Document document = JDOMUtil.loadDocument(inputStream);
			final Element element = document.getRootElement();

			vNode = new VNode(name, "_dummy_");

			for (Object o : element.getAttributes()) {
				Attribute attribute = (Attribute) o;

				String propertyName = attribute.getQualifiedName();
				String value = attribute.getValue();

				// choose which type of object to insert
				if (value.startsWith(BOOLEAN_PREFIX)) {
					Boolean b = Boolean.parseBoolean(value.replaceFirst(Pattern.quote(BOOLEAN_PREFIX), ""));
					vNode.setProperty(propertyName, b);
				} else if (value.startsWith(DOUBLE_PREFIX)) {
					Double d = Double.parseDouble(value.replaceFirst(Pattern.quote(DOUBLE_PREFIX), ""));
					vNode.setProperty(propertyName, d);
				} else if (value.startsWith(LONG_PREFIX)) {
					Long l = Long.parseLong(value.replaceFirst(Pattern.quote(LONG_PREFIX), ""));
					vNode.setProperty(propertyName, l);
				} else {
					vNode.setProperty(propertyName, value);
				}
			}

		} catch (JDOMException jde) {
			log.error("Could not load VNode from inputstream", jde);

		} catch (IOException ioe) {
			log.error("Could not load VNode from inputstream", ioe);
		}

		return vNode;
	}

}
