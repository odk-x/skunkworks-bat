package org.odk.odknotifications.DatabaseCommunicator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.odk.odknotifications.Model.Group;
import org.odk.odknotifications.Model.Notification;

import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {

   private static final int DATABASE_VERSION = 13;
   private static final String DATABASE_NAME = "odknotifications.db";
   private static final String TABLE_GROUPS = "groups";
   private static final String TABLE_NOTIFICATIONS = "notifications";
   private static final String COLUMN_NAME = "name";
   private static final String COLUMN_TITLE = "title";
   private static final String COLUMN_MESSAGE = "message";
   private static final String COLUMN_DATE = "date";
   private static final String COLUMN_GRP_ID = "grp_id";
   private static final String COLUMN_ID = "_id";
   private static final String COLUMN_SNOOZE = "snooze";


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
                COLUMN_TITLE + " TEXT, " +
                COLUMN_MESSAGE + " TEXT, " +
                COLUMN_DATE + " TEXT, " +
                "grp_id TEXT "+
                ");";
        db.execSQL(query2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
        onCreate(db);
    }

    //Add new group to the database.
    public void addNewGroup(Group group){
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, group.getName());
        values.put(COLUMN_SNOOZE, (group.getSnoozeNotifications()));
        values.put(COLUMN_GRP_ID, group.getId());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_GROUPS,null ,values);
        db.close();
    }

    public ArrayList<Group> getGroups(){
        ArrayList<Group> groupArrayList = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM "+ TABLE_GROUPS +" ;";
        Cursor c = db.rawQuery(query,null);
        c.moveToFirst();

        while(!c.isAfterLast()){
            if(c.getString(c.getColumnIndex(COLUMN_GRP_ID))!=null){
                String name = c.getString(c.getColumnIndex(COLUMN_NAME));
                Integer snoozeNotifications = c.getInt(c.getColumnIndex(COLUMN_SNOOZE));
                String id = c.getString(c.getColumnIndex(COLUMN_GRP_ID));
                groupArrayList.add(new Group(id,name, snoozeNotifications));
            }else{
                System.out.println("ID is Null");
            }
            c.moveToNext();
        }
        c.close();
        db.close();
        return groupArrayList;
    }

    public void newGroupDatabase(ArrayList<Group> groupArrayList){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUPS);
        onCreate(db);
        for(Group group: groupArrayList){
            addNewGroup(group);
        }
        db.close();
    }

    public ArrayList<Notification> getNotifications(String groupId) {
        ArrayList<Notification> notificationArrayList = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        System.out.println(groupId);
        String query = "SELECT * FROM "+ TABLE_NOTIFICATIONS + " WHERE "+COLUMN_GRP_ID+" = '"+groupId+"' ;";
        System.out.println(query);

        Cursor c = db.rawQuery(query,null);
        c.moveToFirst();

        while(!c.isAfterLast()){
            if(c.getString(c.getColumnIndex(COLUMN_TITLE))!=null){
                String title = c.getString(c.getColumnIndex(COLUMN_TITLE));
                String message = c.getString(c.getColumnIndex(COLUMN_MESSAGE));
                Long date = Long.parseLong(c.getString(c.getColumnIndex(COLUMN_DATE)));
                String group = c.getString(c.getColumnIndex(COLUMN_GRP_ID));
                notificationArrayList.add(new Notification(title,message,date,group));
            }
            c.moveToNext();
        }
        db.close();
        c.close();
        System.out.println(notificationArrayList);
        return  notificationArrayList;
    }

    public void addNotification(Notification notification){
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, notification.getTitle());
        values.put(COLUMN_MESSAGE, (notification.getMessage()));
        values.put(COLUMN_DATE, (String.valueOf(notification.getDate())));
        values.put(COLUMN_GRP_ID,notification.getGroup());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_NOTIFICATIONS,null ,values);
        db.close();
    }
}
