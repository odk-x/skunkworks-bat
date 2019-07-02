package org.odk.odknotifications.DatabaseCommunicator;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.odk.odknotifications.Model.Group;
import org.odk.odknotifications.Model.Notification;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static junit.framework.TestCase.assertNotNull;


@RunWith(AndroidJUnit4.class)
public class DBHandlerTest{

    private DBHandler dbHandler;

    @Before
    public void setUp(){
        dbHandler = new DBHandler(InstrumentationRegistry.getInstrumentation().getTargetContext(),null,null,1);
    }

    @After
    public void finish() {
        dbHandler.close();
    }

    @Test
    public void testPreConditions() {
        assertNotNull(dbHandler);
    }

    @Test
    public void addNewGroupTest() throws Exception {
        Group group = new Group("test_group","Test Group",0);
        dbHandler.addNewGroup(group);
        List<Group> groups = dbHandler.getGroups();
        Assert.assertTrue(EqualsBuilder.reflectionEquals(group,groups.get(groups.size()-1)));
    }

    @Test
    public void addNotificationTest() {
        Notification notification = new Notification("test_notification","This is a test notification", Calendar.getInstance().getTimeInMillis(),"test_group");
        dbHandler.addNotification(notification);
        List<Notification> notifications = dbHandler.getNotifications("test_group");
        Assert.assertTrue(EqualsBuilder.reflectionEquals(notification,notifications.get(notifications.size()-1)));
    }

    @Test
    public void newGroupDatabaseTest(){
        ArrayList<Group> groups = new ArrayList<>();
        Group group = new Group("test_group","Test Group",0);
        groups.add(group);
        dbHandler.newGroupDatabase(groups);
        assertThat(dbHandler.getGroups().size(),is(1));
        Assert.assertTrue(EqualsBuilder.reflectionEquals(group,groups.get(0)));
    }


}
