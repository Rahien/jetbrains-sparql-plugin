import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

public abstract class SparqlAction extends AnAction {
    abstract String getValidFileNameRegex();
    abstract String getName();
    protected Project project;

    @Override
    public void update(AnActionEvent e) {
        VirtualFile vfile = e.getDataContext().getData(CommonDataKeys.VIRTUAL_FILE);
        project = e.getDataContext().getData(CommonDataKeys.PROJECT);

        boolean visible = false;
        if (vfile != null) {
            String fileName = vfile.getPath();
            visible = fileName.matches(getValidFileNameRegex());
        }
        e.getPresentation().setEnabledAndVisible(visible);
    }

    public abstract void execute() throws Exception;

    public void executeWithMessages(){
        try{
            execute();
            Notifier.sendNotification(project, this.getName() + ": Success",
                    NotificationType.INFORMATION);
        }catch (Exception e){
            Notifier.sendNotification(project, this.getName() + ": Failed! "+e.getMessage(),
                    NotificationType.ERROR);
        }
    }
}
