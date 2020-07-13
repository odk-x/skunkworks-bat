package org.odk.odknotifications.DatabaseCommunicator;

import android.content.ContentValues;
import android.util.Log;

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

    private  static String appName;
    private  static UserDbInterface userDbInterface;
    private static String userId;
    private static DbHandle dbHandle;

    private static final String USER_TABLE_ID = "UsersTable";
    private static final String GROUPS_TABLE_ID = "GroupsTable";
    private static final  String NOTIFICATIONS_TABLE_ID = "NotificationsTable";
    private static final String RESPONSES_TABLE_ID = "ResponsesTable";

    private static final List<String> USERS_TABLE_COLUMNS_LIST = Arrays.asList("UserId",
            "UserName","GroupList");

    private static final List<String> RESPONSES_TABLE_COLUMNS_LIST = Arrays.asList("ResponseId",
            "ResponseText","NotificationId","UserName","ResponseTime");

    public static void init(UserDbInterface userDb , String aName) throws ServicesAvailabilityException, JSONException, ActionNotAuthorizedException {

        userDbInterface = userDb;
        appName = aName;
        userId = userDbInterface.getActiveUser(appName);
        dbHandle = userDbInterface.openDatabase(appName);

        if(!isUserPresent(userId))addUser(userId);
    }

    public static ArrayList<Group> getGroupsList(String activeUser) throws ServicesAvailabilityException {

        ArrayList<Group> groupArrayList = new ArrayList<>();

        OrderedColumns orderedColumns = userDbInterface.getUserDefinedColumns(appName,dbHandle,USER_TABLE_ID);

        UserTable userTable = userDbInterface.simpleQuery(appName, dbHandle, USER_TABLE_ID, orderedColumns, null, null,
                null,null,null,null,null,null);

        int rowNo = userTable.getRowNumFromId(userId);

        TypedRow typedRow = userTable.getRowAtIndex(rowNo);

        String groupIds = typedRow.getStringValueByKey("GroupList");
        String[] groupList = groupIds.split(",");

        for (String s : groupList) {
            Group group = getGroupFromId(s);
            if(group.getId()!=null)groupArrayList.add(group);
        }
        return groupArrayList;
    }

    public static ArrayList<Notification> getNotifications(String groupId) throws ServicesAvailabilityException {
        ArrayList<Notification> notificationArrayList = new ArrayList<>();

        ArrayList<Notification> completeNotificationArrayList = getNotifications();

        for(int i=0;i<completeNotificationArrayList.size();i++){

            if(completeNotificationArrayList.get(i).getGroup().equals(groupId)){
                notificationArrayList.add(completeNotificationArrayList.get(i));
            }
        }
        Log.e("size",notificationArrayList.size()+"");
        return  notificationArrayList;
    }

    public static ArrayList<Notification> getNotifications() throws ServicesAvailabilityException {
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
            notification.setDate(Long.parseLong(typedRow.getStringValueByKey("NotificationTime")));
            if(!getResponse(notification.getId()).equals(""))notification.setResponse(getResponse(notification.getId()));

            notificationArrayList.add(notification);
        }

        return  notificationArrayList;
    }

    public static void addResponse(Response response) throws ServicesAvailabilityException, ActionNotAuthorizedException {

        OrderedColumns orderedColumns = userDbInterface.getUserDefinedColumns(appName,dbHandle,RESPONSES_TABLE_ID);

        List<String>columnValues = Arrays.asList(response.getResponseID(),response.getResponse(),response.getNotificationId(),
                response.getSenderID(),String.valueOf(response.getTime()));

        ContentValues contentValues = new ContentValues();

        for(int i=0;i<columnValues.size();i++){
            contentValues.put(RESPONSES_TABLE_COLUMNS_LIST.get(i),columnValues.get(i));
        }

        userDbInterface.insertRowWithId(appName,dbHandle,RESPONSES_TABLE_ID,orderedColumns,contentValues,response.getResponseID());

    }

    public static void addGroup(String groupId) throws ServicesAvailabilityException, ActionNotAuthorizedException {

        OrderedColumns orderedColumns = userDbInterface.getUserDefinedColumns(appName,dbHandle,USER_TABLE_ID);


        UserTable userTable = userDbInterface.simpleQuery(appName, dbHandle, USER_TABLE_ID, orderedColumns, null, null,
                null,null,null,null,null,null);

        int rowNo = userTable.getRowNumFromId(userId);
        TypedRow typedRow = userTable.getRowAtIndex(rowNo);

        String groups = typedRow.getStringValueByKey("GroupList");
        String userName = typedRow.getStringValueByKey("UserName");

        List<String> groupsArray = Arrays.asList(groups.split(","));

        if(!groupsArray.contains(groupId)){
            groups += (groupId + ",");
        }

        ContentValues contentValues = new ContentValues();

        List<String>columnValues = Arrays.asList(userId,userName,groups);

        for(int i=0;i<columnValues.size();i++){
            contentValues.put(USERS_TABLE_COLUMNS_LIST.get(i) , columnValues.get(i));
        }

        userDbInterface.updateRowWithId(appName,dbHandle,USER_TABLE_ID,orderedColumns,contentValues,userId);
    }

    private static boolean isUserPresent(String userId) throws ServicesAvailabilityException {

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

    private static void addUser (String userId) throws ServicesAvailabilityException, JSONException, ActionNotAuthorizedException {

        String roles_array_string = userDbInterface.getRolesList(appName);

        JSONArray temp = new JSONArray();

        if(roles_array_string!=null) {
            temp = new JSONArray(roles_array_string);
        }

        StringBuilder groups = new StringBuilder();
        for(int i=0; i<temp.length();i++){
            if(temp.getString(i).startsWith("GROUP_")|| temp.getString(i).startsWith("ROLE_")) {
                groups.append(temp.get(i)).append(",");
            }
        }

        String userName = getUserName(userId);

        List<String>columnValues = Arrays.asList(userId,userName,groups.toString());

        ContentValues contentValues = new ContentValues();

        for(int i=0;i<columnValues.size();i++){
            contentValues.put(USERS_TABLE_COLUMNS_LIST.get(i) , columnValues.get(i));
        }

        OrderedColumns orderedColumns = userDbInterface.getUserDefinedColumns(appName,dbHandle,USER_TABLE_ID);

        userDbInterface.insertRowWithId(appName,dbHandle,USER_TABLE_ID,orderedColumns,contentValues,userId);
    }

    private static Group getGroupFromId(String groupId) throws ServicesAvailabilityException {
        Group group = new Group();

        OrderedColumns orderedColumns = userDbInterface.getUserDefinedColumns(appName,dbHandle,GROUPS_TABLE_ID);

        UserTable userTable = userDbInterface.simpleQuery(appName, dbHandle, GROUPS_TABLE_ID, orderedColumns, null, null,
                null,null,null,null,null,null);

        int rowNo = userTable.getRowNumFromId(groupId);

        if(rowNo >= 0) {
            TypedRow typedRow = userTable.getRowAtIndex(rowNo);
            group.setId(typedRow.getStringValueByKey("GroupId"));
            group.setName(typedRow.getStringValueByKey("GroupName"));
        }
        return group;
    }

    private static String getResponse(String notificationId) throws ServicesAvailabilityException {

        String response = "";

        OrderedColumns orderedColumns = userDbInterface.getUserDefinedColumns(appName,dbHandle,RESPONSES_TABLE_ID);

        UserTable userTable = userDbInterface.simpleQuery(appName, dbHandle, RESPONSES_TABLE_ID, orderedColumns, null, null,
                null,null,null,null,null,null);

        for(int i=0;i<userTable.getNumberOfRows();i++){
            TypedRow typedRow = userTable.getRowAtIndex(i);

            if(typedRow.getStringValueByKey("NotificationId").equals(notificationId) &&
                    typedRow.getStringValueByKey("UserName").equals(getUserName(userId))){
                response = typedRow.getStringValueByKey("ResponseText");
            }
        }

        return response;
    }

    private static String getUserName (String userId){
        String userName = userId;

        if(!(userId.compareTo("anonymous")==0) && userId.length()>8 && userId.substring(0,9).compareTo("username:")==0){
            userName = userName.substring(9);
        }
        return userName;
    }
}
