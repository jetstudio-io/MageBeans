/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.adfab.magebeans.actions;

import fr.adfab.magebeans.guis.CreateModuleForm;
import fr.adfab.magebeans.guis.CreateModuleTopComponent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JOptionPane;
import org.netbeans.api.project.Project;
import org.openide.filesystems.FileObject;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;

@ActionID(
        category = "PHP",
        id = "fr.adfab.magebeans.actions.CreateModuleAction"
)
@ActionRegistration(
        iconBase = "fr/adfab/magebeans/actions/icon.png",
        displayName = "#CTL_CreateModuleAction"
)
@ActionReferences({
    @ActionReference(path = "Menu/Source", position = 200, separatorAfter = 250)
    ,
  @ActionReference(path = "Toolbars/File", position = 500)
})
@Messages("CTL_CreateModuleAction=Create a module")
public final class CreateModuleAction implements ActionListener {

    private final Project context;

    public CreateModuleAction(Project context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        Lookup lookup = Utilities.actionsGlobalContext();
        Project project = lookup.lookup(Project.class);
        FileObject projectDir = project.getProjectDirectory();
        String filePath = projectDir.getPath() + "/app/Mage.php";
        File mageFile = new File(filePath);
        if (mageFile.exists() && !mageFile.isDirectory()) {
            String args[] = new String[1];
            args[0] = projectDir.getPath();
            CreateModuleForm.main(args);
        } else {
            JOptionPane.showMessageDialog(null, "Current project is not magento 1x", "MageBeans plugin", JOptionPane.ERROR_MESSAGE);
        }
    }
}
