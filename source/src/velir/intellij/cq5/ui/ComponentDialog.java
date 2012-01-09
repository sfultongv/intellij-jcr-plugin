package velir.intellij.cq5.ui;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import velir.intellij.cq5.jcr.model.VComponent;

import javax.swing.*;

public class ComponentDialog extends DialogWrapper {

	private VComponent vComponent;

	public ComponentDialog(Project project) {
		super(project, false);

		vComponent = new VComponent("newComponent");

		init();
		setTitle("New Component");
	}

	@Override
	protected JComponent createCenterPanel() {
		return vComponent.makePanel();
	}

	public VComponent getVComponent() {
		return vComponent;
	}
}
