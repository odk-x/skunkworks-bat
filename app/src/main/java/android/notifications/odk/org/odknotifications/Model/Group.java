package android.notifications.odk.org.odknotifications.Model;

public class Group {
    private String name;
    private String id;
    private int snoozeNotifications;

    public Group(String id,String name, int snoozeNotifications) {
        this.id = id;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
