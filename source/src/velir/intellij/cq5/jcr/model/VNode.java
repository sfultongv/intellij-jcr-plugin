package velir.intellij.cq5.jcr.model;

import org.jdom.Element;
import org.jdom.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.awt.HorizBagLayout;
import sun.awt.VerticalBagLayout;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class VNode {
	private String name;
	private Map<String, Object> properties;
	Logger log = LoggerFactory.getLogger(VNode.class);

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

	public String getName () {
		return name;
	}

	protected void setName (String name) {
		this.name = name;
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
			String propertyStringValue = "";
			if (value instanceof Boolean) {
				propertyStringValue = "{Boolean}" + value.toString();
			} else {
				propertyStringValue = value.toString();
			}

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

	public JPanel makePanel () {
		JPanel nodePanel = new JPanel(new GridLayout(properties.size() + 2, 1));

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
		namePanel.add(nameField);
		nodePanel.add(namePanel);

		// separator
		nodePanel.add(new JSeparator(JSeparator.HORIZONTAL));

		// properties
		JPanel propertiesPanel = new JPanel(new GridLayout(properties.size(), 1));
		for (Map.Entry<String,Object> property : properties.entrySet()) {
			JPanel jPanel = new JPanel(new GridLayout(1,2));

			// make label
			final String name = property.getKey();
			JLabel jLabel = new JLabel(name);
			jPanel.add(jLabel);

			// make input based on property class
			Object value = property.getValue();
			if (value instanceof Boolean) {
				final JCheckBox jCheckBox = new JCheckBox("", (Boolean) value);
				jCheckBox.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setProperty(name, jCheckBox.isSelected());
					}
				});
				jPanel.add(jCheckBox);
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

			propertiesPanel.add(jPanel);
		}
		nodePanel.add(propertiesPanel);

		return nodePanel;
	}

}
