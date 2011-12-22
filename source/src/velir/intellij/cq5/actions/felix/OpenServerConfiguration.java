package velir.intellij.cq5.actions.felix;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ide.BrowserUtil;

/**
 * Felix action that will open the felix server configuration page.
 */
public class OpenServerConfiguration extends AnAction {
    /**
     * The entry point when the action is performed.
     * @param e The action event arguments.
     */
    public void actionPerformed(AnActionEvent e) {
        BrowserUtil.launchBrowser("http://localhost:4502/system/console/configMgr");
    }
}
