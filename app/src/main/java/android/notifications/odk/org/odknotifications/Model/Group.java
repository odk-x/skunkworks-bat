package android.notifications.odk.org.odknotifications.Model;

public class Group {
    String name;
    int snoozeNotifications;

    public Group(String name, int snoozeNotifications) {
        this.name = name;
        this.snoozeNotifications = snoozeNotifications;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSnoozeNotifications() {
        return snoozeNotifications;
    }

    public void setSnoozeNotifications(int snoozeNotifications) {
        this.snoozeNotifications = snoozeNotifications;
    }
}
