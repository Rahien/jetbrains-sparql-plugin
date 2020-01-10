import com.intellij.ide.BrowserUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.UUID;

public class ExecuteSparql extends SparqlAction {
    String validFileNameRegex = ".*\\.sparql";
    String sparql;
    VirtualFile vfile;

    @Override
    String getValidFileNameRegex() {
        return validFileNameRegex;
    }

    @Override
    String getName() {
        return "Fetch Responses";
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        this.vfile = event.getDataContext().getData(CommonDataKeys.VIRTUAL_FILE);
        try {
            this.sparql = VfsUtil.loadText(vfile);
            if(SPARQLApi.remembersSettings()){
                executeWithMessages();
            }else{
                ApiInfoDialog dialog = new ApiInfoDialog(this);
                dialog.show();
            }
        }catch (Exception e){
            this.sparql = null;
            Notifier.sendNotification(
                    project,
                    "Failed to fetch code from file: "+e.getMessage(),
                    NotificationType.ERROR
            );
        }
    }

    @Override
    public void execute() throws Exception {
        String result = SPARQLApi.postSparqlToServer(this.sparql);
        String extension = "html";
        if(this.sparql.toLowerCase().contains("construct")){
            extension = "txt";
        }

        String targetDir = System.getProperty("java.io.tmpdir");
        File url = SPARQLApi.ensureFile(targetDir+File.separator+ UUID.randomUUID().toString()+"."+extension, result);
        if (url.exists()) {
            BrowserUtil.browse(url);
        }
    }
}