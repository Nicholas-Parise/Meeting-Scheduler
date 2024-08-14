package ca.brocku.meetingscheduler;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;

public class MeetingManager extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_manager);
    }


    /**
     * drops the table
     * @param v
     */
    public void deleteAll(View v){
        DataHelper dh = new DataHelper(this);
        SQLiteDatabase db=dh.getWritableDatabase();
        db.execSQL("delete from "+ dh.DB_TABLE);

        Toast.makeText(this, "all meetings deleted", Toast.LENGTH_SHORT).show();
    }

    /**
     * query database and delete all rows where today is the meetign day
     * @param view
     */
    public void clearToday(View view) {

        java.util.Calendar cal = java.util.Calendar.getInstance();
        int year = cal.get(java.util.Calendar.YEAR);
        int monthOfYear = cal.get(java.util.Calendar.MONTH);
        int dayOfMonth = cal.get(java.util.Calendar.DAY_OF_MONTH);

        String today = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;


        DataHelper dh=new DataHelper(this);
        SQLiteDatabase db = dh.getReadableDatabase();

        db.delete(DataHelper.DB_TABLE, "meetingDate = ?", new String[]{today});
        db.close();

        Toast.makeText(this, "all meetings today cleared", Toast.LENGTH_SHORT).show();

    }


    /**
     * queries the table to update all the rows where today is the meeting day,
     * we then add the correct number of days to the meeting date depending on the requirements below
     *
     * @param view
     */
    public void pushMeetings(View view) {

        java.util.Calendar cal=java.util.Calendar.getInstance();
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        int year = cal.get(java.util.Calendar.YEAR);
        int monthOfYear = cal.get(java.util.Calendar.MONTH);
        int dayOfMonth = cal.get(java.util.Calendar.DAY_OF_MONTH);

        String oldToday = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;

        if(dayOfWeek == 1){
            // sunday, push 6 days
            cal.add(Calendar.DATE,6);
        }else if(dayOfWeek == 6){
            // friday, push 3 days
            cal.add(Calendar.DATE,3);
        }else{
            // other day, push one day
            cal.add(Calendar.DATE,1);
        }

        year = cal.get(java.util.Calendar.YEAR);
        monthOfYear = cal.get(java.util.Calendar.MONTH);
        dayOfMonth = cal.get(java.util.Calendar.DAY_OF_MONTH);

        String newToday = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;


        ContentValues cv = new ContentValues();
        cv.put("meetingDate",newToday); //These Fields should be your String values of actual column names

        DataHelper dh=new DataHelper(this);
        SQLiteDatabase db = dh.getReadableDatabase();

        db.update(DataHelper.DB_TABLE, cv, "meetingDate = ?", new String[]{oldToday});
        db.close();

        Toast.makeText(this, "all meetings today pushed", Toast.LENGTH_SHORT).show();
    }


    /**
     * for the menu system to work
     * @param menu The options menu in which you place your items.
     *
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.tasks,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return ModeSwitcher.handleMenuClicky(item,this);
    }


}