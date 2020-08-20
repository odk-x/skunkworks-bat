package org.odk.odknotifications.DatabaseCommunicator;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.odk.odknotifications.Model.Group;
import org.odk.odknotifications.Model.Notification;
import org.odk.odknotifications.SyncDataWithServices;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(AndroidJUnit4.class)
public class DBHandlerTest{

    private DBHandler dbHandler;

    @Before
    public void setUp(){
        dbHandler = new DBHandler(InstrumentationRegistry.getInstrumentation().getTargetContext(),null,null,1);
        dbHandler.clearTable(DBHandler.TABLE_GROUPS);
        dbHandler.clearTable(DBHandler.TABLE_NOTIFICATIONS);
    }

    @After
    public void finish() {
        dbHandler.clearTable(DBHandler.TABLE_GROUPS);
        dbHandler.clearTable(DBHandler.TABLE_NOTIFICATIONS);
        dbHandler.close();
    }

    @Test
    public void testPreConditions() {
        assertNotNull(dbHandler);
    }

    @Test
    public void addNewGroupTest() {
        Group group = new Group("test_group","Test Group",0);
        dbHandler.addNewGroup(group);
        List<Group> groups = dbHandler.getGroups();
        Assert.assertTrue(EqualsBuilder.reflectionEquals(group,groups.get(0)));
    }

    @Test
    public void addNotificationTest() {
        Notification notification = new Notification("test_id","test_notification","This is a test notification", Calendar.getInstance().getTimeInMillis(),"test_group",Notification.SIMPLE,null);
        dbHandler.addNotification(notification);
        List<Notification> notifications = dbHandler.getNotifications("test_group");
        Assert.assertTrue(EqualsBuilder.reflectionEquals(notification,notifications.get(0)));
    }

    @Test
    public void newGroupDatabaseTest(){
        ArrayList<Group> groups = new ArrayList<>();
        Group group1 = new Group("test_group 1","Test Group 1",0);
        Group group2 = new Group("test_group 2","Test Group 2",0);
        groups.add(group1);
        groups.add(group2);
        dbHandler.newGroupDatabase(groups);
        assertThat(dbHandler.getGroups().size(),is(2));
        Assert.assertTrue(EqualsBuilder.reflectionEquals(group1,groups.get(0)));
        Assert.assertTrue(EqualsBuilder.reflectionEquals(group2,groups.get(1)));
    }

    @Test
    public void syncDataTest() {

        Notification notification1 = new Notification("test1", "test_notification1", "This is a test notification1", Calendar.getInstance().getTimeInMillis(), "test_group", Notification.SIMPLE, null);
        Notification notification2 = new Notification("test2", "test_notification2", "This is a test notification2", Calendar.getInstance().getTimeInMillis(), "test_group", Notification.SIMPLE, null);

        ArrayList<Notification> notificationArrayList = new ArrayList<>();
        notificationArrayList.add(notification1);
        notificationArrayList.add(notification2);

        new SyncDataWithServices(notificationArrayList, dbHandler).syncData();
        List<Notification> notifications = dbHandler.getAllNotificationsWithResponses();

        Assert.assertTrue(EqualsBuilder.reflectionEquals(notification1, notifications.get(0)));
        Assert.assertTrue(EqualsBuilder.reflectionEquals(notification2, notifications.get(1)));
    }

}
