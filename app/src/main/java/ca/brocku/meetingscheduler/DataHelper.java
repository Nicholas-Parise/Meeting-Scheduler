package ca.brocku.meetingscheduler;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// class based off code from lecture

public class DataHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 2;
    public static final String DB_NAME = "schedule";
    public static final String DB_TABLE = "meetings";

    private static final String CREATE_TABLE = "CREATE TABLE " + DB_TABLE +
            " (id INTEGER PRIMARY KEY, meetingName TEXT, meetingDate TEXT, startTime TEXT, endTime TEXT, contactId INTEGER, address TEXT);";
    DataHelper(Context context) {
        super(context,DB_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //How to migrate or reconstruct data from old version to new on upgrade
    }
}