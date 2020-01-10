import com.intellij.notification.*;
import com.intellij.openapi.project.Project;

public class Notifier {

    public static final NotificationGroup SPARQL_NOTIFICATION_GROUP =
            new NotificationGroup("SPARQL-executor",
                    NotificationDisplayType.BALLOON, true);


    public static void sendNotification(Project project, String message, NotificationType type){
        Notification notification = SPARQL_NOTIFICATION_GROUP.createNotification(message, type);
        Notifications.Bus.notify(notification, project);
    }

}
