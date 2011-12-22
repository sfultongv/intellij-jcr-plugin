package velir.intellij.cq5.config;

import com.intellij.ide.ui.LafManager;
import com.intellij.ide.ui.UISettings;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;

import javax.swing.*;

/**
 * The cq5 configuration for the intellij plugin.
 */
public class Cq5Config implements Configurable {
    @Nls
    public String getDisplayName() {
        return "CQ5 Configurations";
    }

    public Icon getIcon() {
        return null;
    }

    public String getHelpTopic() {
        return null;
    }

    public JComponent createComponent() {
        //javax.swing.
        return null;
    }

    public boolean isModified() {
        return true;
    }

    public void apply() throws ConfigurationException {
        UISettings settings = UISettings.getInstance();
        LafManager lafManager = LafManager.getInstance();
    }

    public void reset() {

    }

    public void disposeUIResources() {

    }
}
