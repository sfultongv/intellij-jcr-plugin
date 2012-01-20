package velir.intellij.cq5.util;

import com.intellij.openapi.util.JDOMUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import org.jdom.Document;
import velir.intellij.cq5.jcr.model.VNode;

import java.io.IOException;
import java.io.OutputStream;

public class PsiUtils {

	public static final String CONTENT_XML = ".content.xml";

	// change namespace colon to underscores if present, otherwise do nothing
	public static String mungeNamespace (String name) {
		return name.replaceFirst("^([^:]*):", "_$1_");
	}

	// change namespace in filesystem to xml version of namespace
	public static String unmungeNamespace (String name) {
		return name.replaceFirst("^_([^_]*)_", "$1:");
	}

	public static void writeNodeContent (PsiFile psiFile, VNode vNode) throws IOException {
		VirtualFile virtualFile = psiFile.getVirtualFile();
		OutputStream outputStream = virtualFile.getOutputStream(vNode);
		Document document = new Document(vNode.getElement());
		JDOMUtil.writeDocument(document, outputStream, "\n");
		outputStream.close();
	}

	/**
	 * creates a node representation on the filesystem
	 * @param vNode
	 */
	public static void createNode (PsiDirectory parentDirectory, VNode vNode) throws IOException {
		String name = mungeNamespace(vNode.getName());
		PsiDirectory contentDirectory = parentDirectory.createSubdirectory(name);
		PsiFile contentFile = contentDirectory.createFile(".content.xml");
		writeNodeContent(contentFile, vNode);
	}
}
