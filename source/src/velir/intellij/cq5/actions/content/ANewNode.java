package velir.intellij.cq5.actions.content;

import com.intellij.ide.IdeView;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiDirectory;
import velir.intellij.cq5.jcr.model.VNode;
import velir.intellij.cq5.ui.NodeDialog;
import velir.intellij.cq5.util.PsiUtils;

import java.io.IOException;

public abstract class ANewNode extends AnAction {
	@Override
	public void actionPerformed(AnActionEvent anActionEvent) {
		final Project project = anActionEvent.getData(PlatformDataKeys.PROJECT);
		DataContext dataContext = anActionEvent.getDataContext();
		IdeView ideView = LangDataKeys.IDE_VIEW.getData(dataContext);
		Application application = ApplicationManager.getApplication();

		PsiDirectory[] dirs = ideView.getDirectories();

		NodeDialog nodeDialog = new NodeDialog(project, getNode(), true);
		nodeDialog.show();
		if (nodeDialog.getExitCode() == DialogWrapper.OK_EXIT_CODE) {
			final VNode vNode = nodeDialog.getVNode();
			for (final PsiDirectory dir : dirs) {
				application.runWriteAction(new Runnable() {
					public void run() {
						try {
							PsiUtils.createNode(dir, vNode);
						} catch (IOException ioe) {
							Messages.showMessageDialog(project, "Could not write to content file", "Error", Messages.getErrorIcon());
						}
					}
				});
			}
		}
	}

	public abstract VNode getNode();

}
