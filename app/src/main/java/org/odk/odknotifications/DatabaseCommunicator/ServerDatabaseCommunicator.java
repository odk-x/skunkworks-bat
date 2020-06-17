package org.odk.odknotifications.DatabaseCommunicator;

import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.odk.odknotifications.Model.Group;
import org.odk.odknotifications.Model.Notification;
import org.odk.odknotifications.Model.Response;
import org.opendatakit.database.data.OrderedColumns;
import org.opendatakit.database.data.TypedRow;
import org.opendatakit.database.data.UserTable;
import org.opendatakit.database.service.DbHandle;
import org.opendatakit.database.service.UserDbInterface;
import org.opendatakit.exception.ActionNotAuthorizedException;
import org.opendatakit.exception.ServicesAvailabilityException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServerDatabaseCommunicator {

    private  String appName;
    private  UserDbInterface userDbInterface;
    private String userId;
    private DbHandle dbHandle;

    private static final String USER_TABLE_ID = "UsersTable";
    private static final String GROUPS_TABLE_ID = "GroupsTable";
    private static final  String NOTIFICATIONS_TABLE_ID = "NotificationsTable";

    private static final List<String> USERS_TABLE_COLUMNS_LIST = Arrays.asList("UserId",
            "UserName","GroupList");

    private static final String GROUP_LIST_KEY = "Groups";

    public ServerDatabaseCommunicator (UserDbInterface userDbInterface, String appName) throws ServicesAvailabilityException, JSONException, ActionNotAuthorizedException {
        this.userDbInterface = userDbInterface;
        this.appName = appName;
        this.userId = userDbInterface.getActiveUser(appName);
        this.dbHandle = userDbInterface.openDatabase(appName);

        if(!isUserPresent(userId))addUser(userId);
    }

    public ArrayList<Group> getGroupsList(String activeUser) throws ServicesAvailabilityException {

        ArrayList<Group> groupArrayList = new ArrayList<>();

        OrderedColumns orderedColumns = userDbInterface.getUserDefinedColumns(appName,dbHandle,USER_TABLE_ID);

        UserTable userTable = userDbInterface.simpleQuery(appName, dbHandle, USER_TABLE_ID, orderedColumns, null, null,
                null,null,null,null,null,null);

        int rowNo = userTable.getRowNumFromId(userId);

        TypedRow typedRow = userTable.getRowAtIndex(rowNo);

        String groupIds = typedRow.getStringValueByKey("GroupList");
        String[] groupList = groupIds.split(",");

        for (String s : groupList) {
            groupArrayList.add(getGroupFromId(s));
        }

        return groupArrayList;
    }

    public ArrayList<Notification> getNotifications(String groupId) throws ServicesAvailabilityException {
        ArrayList<Notification> notificationArrayList = new ArrayList<>();

        ArrayList<Notification> completeNotificationArrayList = getNotifications();

        for(int i=0;i<completeNotificationArrayList.size();i++){

            if(completeNotificationArrayList.get(i).getId().equals(groupId)){
                notificationArrayList.add(completeNotificationArrayList.get(i));
            }
        }
        return  notificationArrayList;
    }

    public ArrayList<Notification> getNotifications() throws ServicesAvailabilityException {
        ArrayList<Notification> notificationArrayList = new ArrayList<>();

        OrderedColumns orderedColumns = userDbInterface.getUserDefinedColumns(appName,dbHandle,NOTIFICATIONS_TABLE_ID);

        UserTable userTable = userDbInterface.simpleQuery(appName, dbHandle, NOTIFICATIONS_TABLE_ID, orderedColumns, null, null,
                null,null,null,null,null,null);

        for(int i=0; i<userTable.getNumberOfRows(); i++){

            TypedRow typedRow = userTable.getRowAtIndex(i);

            Notification notification = new Notification();
            notification.setId(typedRow.getStringValueByKey("NotificationId"));
            notification.setGroup(typedRow.getStringValueByKey("GroupId"));
            notification.setTitle(typedRow.getStringValueByKey("NotificationTitle"));
            notification.setMessage(typedRow.getStringValueByKey("NotificationMessage"));
            notification.setType(typedRow.getStringValueByKey("NotificationType"));
            notification.setDate(Long.parseLong(typedRow.getStringValueByKey("NotificationDate")));
            notificationArrayList.add(notification);
        }

        return  notificationArrayList;
    }

    public void addResponse(Response response){

    }

    private boolean isUserPresent(String userId) throws ServicesAvailabilityException {

        OrderedColumns orderedColumns = userDbInterface.getUserDefinedColumns(appName,dbHandle,USER_TABLE_ID);

        UserTable userTable = userDbInterface.simpleQuery(appName, dbHandle, USER_TABLE_ID, orderedColumns, null, null,
                null,null,null,null,null,null);

        boolean isUserPresent = false;

        for(int i=0; i<userTable.getNumberOfRows(); i++){
            if(userTable.getRowAtIndex(i).getStringValueByKey("UserId").equals(userId)){
                isUserPresent = true;
            }
        }
        return isUserPresent;
    }

    private void addUser (String userId) throws ServicesAvailabilityException, JSONException, ActionNotAuthorizedException {

        String roles_array_string = userDbInterface.getRolesList(appName);

        JSONArray temp = new JSONArray(roles_array_string);

        StringBuilder groups = new StringBuilder();
        for(int i=0; i<temp.length();i++){
            if(temp.getString(i).startsWith("GROUP_")|| temp.getString(i).startsWith("ROLE_")) {
                groups.append("group:").append(temp.get(i)).append(",");
            }
        }

        String userName = userId;
        if(!(userId.compareTo("anonymous")==0) && userId.length()>8 && userId.substring(0,9).compareTo("username:")==0){
            userName = userName.substring(9);
        }
        List<String>columnValues = Arrays.asList(userId,userName,groups.toString());

        ContentValues contentValues = new ContentValues();

        for(int i=0;i<columnValues.size();i++){
            contentValues.put(USERS_TABLE_COLUMNS_LIST.get(i) , columnValues.get(i));
        }

        OrderedColumns orderedColumns = userDbInterface.getUserDefinedColumns(appName,dbHandle,USER_TABLE_ID);

        userDbInterface.insertRowWithId(appName,dbHandle,USER_TABLE_ID,orderedColumns,contentValues,userId);
    }

    private Group getGroupFromId(String groupId) throws ServicesAvailabilityException {
        Group group = new Group();

        OrderedColumns orderedColumns = userDbInterface.getUserDefinedColumns(appName,dbHandle,GROUPS_TABLE_ID);

        UserTable userTable = userDbInterface.simpleQuery(appName, dbHandle, GROUPS_TABLE_ID, orderedColumns, null, null,
                null,null,null,null,null,null);

        int rowNo = userTable.getRowNumFromId(groupId);

        TypedRow typedRow = userTable.getRowAtIndex(rowNo);

        group.setId(typedRow.getStringValueByKey("GroupId"));
        group.setName(typedRow.getStringValueByKey("GroupName"));

        return group;
    }

}
