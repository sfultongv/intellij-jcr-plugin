package velir.intellij.cq5.ui;

import velir.intellij.cq5.jcr.model.VComponent;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class VComponentComponent {
	private VComponent vComponent;
	private JPanel myRootPane;
	private JTextField myName;
	private JTextField myAllowedParents;
	private JTextField myTitle;
	private JTextField myComponentGroup;
	private JCheckBox myContainer;

	public VComponentComponent(final VComponent element) {
		vComponent = element;

		myName = new JTextField(element.getName());
		myTitle = new JTextField(element.getTitle());
		myAllowedParents = new JTextField(element.getAllowedParents());
		myComponentGroup = new JTextField(element.getComponentGroup());
		myContainer = new JCheckBox("is a container", false);

		myName.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				vComponent.setName(myName.getText());
			}

			public void removeUpdate(DocumentEvent e) {
				vComponent.setName(myName.getText());
			}

			public void changedUpdate(DocumentEvent e) {
				vComponent.setName(myName.getText());
			}
		});

		myTitle.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				vComponent.setTitle(myTitle.getText());
			}

			public void removeUpdate(DocumentEvent e) {
				vComponent.setTitle(myTitle.getText());
			}

			public void changedUpdate(DocumentEvent e) {
				vComponent.setTitle(myTitle.getText());
			}
		});

		myRootPane = new JPanel(new GridLayout(4,2));
		myRootPane.add(myName);
		myRootPane.add(myTitle);
		myRootPane.add(myAllowedParents);
		myRootPane.add(myComponentGroup);
		myRootPane.add(myContainer);
	}

	public JComponent getComponent() {
		return myRootPane;
	}

	public VComponent getVComponent() {
		return vComponent;
	}
}
