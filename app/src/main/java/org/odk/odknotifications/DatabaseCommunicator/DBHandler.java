package org.odk.odknotifications.DatabaseCommunicator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.odk.odknotifications.Model.Group;
import org.odk.odknotifications.Model.Notification;
import org.odk.odknotifications.Model.Response;

import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 13;
    private static final String DATABASE_NAME = "odknotifications.db";
    public static final String TABLE_GROUPS = "groups";
    public static final String TABLE_NOTIFICATIONS = "notifications";
    public static final String TABLE_RESPONSES = "responses";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_NOTIF_ID = "notif_id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_MESSAGE = "message";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_GRP_ID = "grp_id";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_SNOOZE = "snooze";
    private static final String COLUMN_RESPONSE_ID = "response_id";
    private static final String COLUMN_SENDER_ID = "sender_id";
    private static final String COLUMN_RESPONSE = "response";
    private static final String COLUMN_RESPONSE_DATE = "response_date";
    private static final String COLUMN_IMG_URI = "img_uri";


    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE IF NOT EXISTS " + TABLE_GROUPS + "(" +
                COLUMN_NAME + " TEXT ," +
                COLUMN_GRP_ID + " TEXT PRIMARY KEY," +
                COLUMN_SNOOZE+" INTEGER "+
                ");";
        db.execSQL(query);

        String query2 = "CREATE TABLE IF NOT EXISTS " + TABLE_NOTIFICATIONS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NOTIF_ID + " TEXT, "+
                COLUMN_TITLE + " TEXT, " +
                COLUMN_MESSAGE + " TEXT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_GRP_ID+ " TEXT, "+
                COLUMN_TYPE + " TEXT, " +
                COLUMN_IMG_URI + " TEXT " +
                ");";
        db.execSQL(query2);
        String query3 = "CREATE TABLE IF NOT EXISTS " + TABLE_RESPONSES + "(" +
                COLUMN_RESPONSE_ID + " TEXT PRIMARY KEY, " +
                COLUMN_NOTIF_ID + " TEXT, "+
                COLUMN_RESPONSE + " TEXT, "+
                COLUMN_SENDER_ID + " TEXT, "+
                COLUMN_RESPONSE_DATE + " TEXT " +
                ");";
        db.execSQL(query3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESPONSES);
        onCreate(db);
    }

    //Add new group to the database.
    public void addNewGroup(Group group) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, group.getName());
        values.put(COLUMN_SNOOZE, (group.getSnoozeNotifications()));
        values.put(COLUMN_GRP_ID, group.getId());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_GROUPS, null, values);
        db.close();
    }

    public ArrayList<Group> getGroups() {
        ArrayList<Group> groupArrayList = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_GROUPS + " ;";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while (!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex(COLUMN_GRP_ID)) != null) {
                String name = c.getString(c.getColumnIndex(COLUMN_NAME));
                int snoozeNotifications = c.getInt(c.getColumnIndex(COLUMN_SNOOZE));
                String id = c.getString(c.getColumnIndex(COLUMN_GRP_ID));
                groupArrayList.add(new Group(id, name, snoozeNotifications));
            } else {
                System.out.println("ID is Null");
            }
            c.moveToNext();
        }
        c.close();
        db.close();
        return groupArrayList;
    }

    public void newGroupDatabase(ArrayList<Group> groupArrayList) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUPS);
        onCreate(db);
        for (Group group : groupArrayList) {
            addNewGroup(group);
        }
        db.close();
    }

    public ArrayList<Notification> getNotifications(String groupId) {
        ArrayList<Notification> notificationArrayList = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        System.out.println(groupId);
        String query = "SELECT * FROM " + TABLE_NOTIFICATIONS + " WHERE " + COLUMN_GRP_ID + " = '" + groupId + "' ;";
        System.out.println(query);

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while (!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex(COLUMN_NOTIF_ID)) != null) {
                String id = c.getString(c.getColumnIndex(COLUMN_NOTIF_ID));
                String title = c.getString(c.getColumnIndex(COLUMN_TITLE));
                String message = c.getString(c.getColumnIndex(COLUMN_MESSAGE));
                Long date = Long.parseLong(c.getString(c.getColumnIndex(COLUMN_DATE)));
                String group = c.getString(c.getColumnIndex(COLUMN_GRP_ID));
                String type = c.getString(c.getColumnIndex(COLUMN_TYPE));
                String img_uri = c.getString(c.getColumnIndex(COLUMN_IMG_URI));
                notificationArrayList.add(new Notification(id, title, message, date, group, type, img_uri));
            }
            c.moveToNext();
        }
        db.close();
        c.close();
        System.out.println(notificationArrayList);
        return notificationArrayList;
    }

    public ArrayList<Notification> getAllNotificationsWithResponses() {
        ArrayList<Notification> notificationArrayList = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NOTIFICATIONS + " LEFT JOIN " + TABLE_RESPONSES + " USING(" + COLUMN_NOTIF_ID + ")" + ";";
        System.out.println(query);

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while (!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex(COLUMN_NOTIF_ID)) != null) {
                String id = c.getString(c.getColumnIndex(COLUMN_NOTIF_ID));
                String title = c.getString(c.getColumnIndex(COLUMN_TITLE));
                String message = c.getString(c.getColumnIndex(COLUMN_MESSAGE));
                Long date = Long.parseLong(c.getString(c.getColumnIndex(COLUMN_DATE)));
                String group = c.getString(c.getColumnIndex(COLUMN_GRP_ID));
                String type = c.getString(c.getColumnIndex(COLUMN_TYPE));
                String response = c.getString(c.getColumnIndex(COLUMN_RESPONSE));
                String img_uri=c.getString(c.getColumnIndex(COLUMN_IMG_URI));
                Notification notification = new Notification(id, title, message, date, group, type, img_uri);
                notification.setResponse(response);
                notificationArrayList.add(notification);
            }
            c.moveToNext();
        }
        db.close();
        c.close();
        System.out.println(notificationArrayList);
        return notificationArrayList;
    }

    public void addNotification(Notification notification) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTIF_ID, notification.getId());
        values.put(COLUMN_TITLE, notification.getTitle());
        values.put(COLUMN_MESSAGE, (notification.getMessage()));
        values.put(COLUMN_DATE, (String.valueOf(notification.getDate())));
        values.put(COLUMN_GRP_ID, notification.getGroup());
        values.put(COLUMN_TYPE, notification.getType());
        values.put(COLUMN_IMG_URI,notification.getImg_uri());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_NOTIFICATIONS, null, values);
        db.close();
    }

    public void addResponse(Response response,String notificationID) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_RESPONSE_ID, response.getResponseID());
        values.put(COLUMN_NOTIF_ID, notificationID);
        values.put(COLUMN_RESPONSE, response.getResponse());
        values.put(COLUMN_SENDER_ID, response.getSenderID());
        values.put(COLUMN_RESPONSE_DATE, response.getTime());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_RESPONSES, null, values);
        db.close();
    }

    void clearTable(String tableName) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(tableName, null, null);
        db.close();
    }
}
