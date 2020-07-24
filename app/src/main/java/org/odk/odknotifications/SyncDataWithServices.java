package org.odk.odknotifications;

import org.odk.odknotifications.DatabaseCommunicator.DBHandler;
import org.odk.odknotifications.Model.Notification;

import java.util.ArrayList;
import java.util.concurrent.Executor;

public class SyncDataWithServices {

    private ArrayList<Notification>notificationArrayList;
    private DBHandler dbHandler;

    public SyncDataWithServices(ArrayList<Notification> notificationArrayList, DBHandler dbHandler) {

        this.notificationArrayList = notificationArrayList;
        this.dbHandler = dbHandler;
    }

    public void syncData() {

        new Executor() {
            @Override
            public void execute(Runnable command) {
                command.run();
            }
        }.execute(new Runnable() {
            @Override
            public void run() {

                dbHandler.clearTable(DBHandler.TABLE_NOTIFICATIONS);

                for (Notification notification : notificationArrayList) {
                    dbHandler.addNotification(notification);
                }
            }
        });
    }
}
