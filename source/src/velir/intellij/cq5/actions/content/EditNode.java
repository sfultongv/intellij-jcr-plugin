package velir.intellij.cq5.actions.content;

import com.intellij.ide.IdeView;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.JDOMUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jdom.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import velir.intellij.cq5.jcr.model.VNode;
import velir.intellij.cq5.ui.NodeDialog;

import java.io.IOException;
import java.io.OutputStream;

public class EditNode extends AnAction {
	private static final Logger log = LoggerFactory.getLogger(EditNode.class);

	@Override
	public void update(AnActionEvent e) {
		final DataContext dataContext = e.getDataContext();
		final Presentation presentation = e.getPresentation();

		final PsiElement element = (PsiElement)dataContext.getData(LangDataKeys.PSI_ELEMENT.getName());

		boolean enabled = false;

		if (element instanceof PsiFile) {
			PsiFile psiFile = (PsiFile) element;
			enabled = ".content.xml".equals(psiFile.getName());
		}

		presentation.setVisible(enabled);
		presentation.setEnabled(enabled);
	}

	@Override
	public void actionPerformed(AnActionEvent anActionEvent) {
		final Project project = anActionEvent.getData(PlatformDataKeys.PROJECT);
		DataContext dataContext = anActionEvent.getDataContext();
		IdeView ideView = LangDataKeys.IDE_VIEW.getData(dataContext);
		Application application = ApplicationManager.getApplication();
		final PsiElement element = (PsiElement)dataContext.getData(LangDataKeys.PSI_ELEMENT.getName());

		if (element instanceof PsiFile) {

			// load node from xml file
			final PsiFile psiFile = (PsiFile) element;
			VNode vNode = application.runReadAction(new Computable<VNode>() {
				public VNode compute() {
					try {
						return VNode.makeVNode(psiFile.getVirtualFile().getInputStream(), psiFile.getContainingDirectory().getName());
					} catch (IOException ioe) {
						log.error("Could not read node xml", ioe);
						Messages.showMessageDialog(project, "Could not read node xml", "Error", Messages.getErrorIcon());
					}
					return null;
				}
			});

			// create dialog from node
			NodeDialog nodeDialog = new NodeDialog(project, vNode, true);
			nodeDialog.show();

			// if OK, update node xml
			if (nodeDialog.getExitCode() == DialogWrapper.OK_EXIT_CODE) {
				final VNode newNode = nodeDialog.getVNode();
				application.runWriteAction(new Runnable() {
					public void run() {
						VirtualFile virtualFile = psiFile.getVirtualFile();
						try {
							OutputStream outputStream = virtualFile.getOutputStream(newNode);
							Document document = new Document(newNode.getElement());
							JDOMUtil.writeDocument(document, outputStream, "\n");
							outputStream.close();
						} catch (IOException ioe) {
							Messages.showMessageDialog(project, "Could not write to content file", "Error", Messages.getErrorIcon());
						}
					}
				});
			}

		} else { // should not happen
			Messages.showMessageDialog(project, "Action performed on non-file element somehow", "Error", Messages.getErrorIcon());
		}
	}
}
