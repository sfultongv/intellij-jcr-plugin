package velir.intellij.cq5.actions.content;

import com.intellij.ide.IdeView;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.JDOMUtil;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import velir.intellij.cq5.jcr.model.VNode;
import velir.intellij.cq5.util.PsiUtils;

import java.io.IOException;

public class UnpackXml extends AnAction {

	@Override
	public void update(AnActionEvent e) {
		final DataContext dataContext = e.getDataContext();
		final Presentation presentation = e.getPresentation();

		final PsiElement element = (PsiElement)dataContext.getData(LangDataKeys.PSI_ELEMENT.getName());

		boolean enabled = false;

		if (element instanceof PsiFile) {
			PsiFile psiFile = (PsiFile) element;
			String name = psiFile.getName();
			// consider .content.xml already unpacked, so disable action for it
			enabled = ! PsiUtils.CONTENT_XML.equals(name) && name.endsWith(".xml");
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
			final PsiFile psiFile = (PsiFile) element;

				application.runWriteAction(new Runnable() {
					public void run() {
						try {
							final Document document = JDOMUtil.loadDocument(psiFile.getVirtualFile().getInputStream());
							final Element rootElement = document.getRootElement();
							final VNode rootNode = VNode.makeVNode(rootElement);

							// root node name is set specially
							String name = PsiUtils.unmungeNamespace(psiFile.getName().split("\\.")[0]);
							rootNode.setName(name);
							PsiDirectory psiDirectory = psiFile.getContainingDirectory();
							PsiUtils.createNode(psiDirectory,rootNode);

							// find newly created subdirectory, and call to recursive function
							PsiDirectory subDirectory = psiDirectory.findSubdirectory(PsiUtils.mungeNamespace(rootNode.getName()));
							unpackRecursively(subDirectory, rootElement);

						} catch (IOException ioe) {
							Messages.showMessageDialog(project, "error loading XML", "Error", Messages.getErrorIcon());
						} catch (JDOMException jde) {
							Messages.showMessageDialog(project, "error loading XML", "Error", Messages.getErrorIcon());
						}
					}
				});
		}
		else {
			Messages.showMessageDialog(project, "Action performed on non-file element somehow", "Error", Messages.getErrorIcon());
		}
	}

	/**
	 * creates xml files for each inner node recursively
	 */
	private void unpackRecursively (PsiDirectory psiDirectory, Element element) throws IOException, JDOMException {
		for (Object o : element.getChildren()) {
			Element child = (Element) o;
			VNode vNode = VNode.makeVNode(child);
			PsiUtils.createNode(psiDirectory, vNode);

			// find newly created subdirectory, and do recursive call
			PsiDirectory subDirectory = psiDirectory.findSubdirectory(PsiUtils.mungeNamespace(vNode.getName()));
			unpackRecursively(subDirectory, child);
		}
	}
}
