package ca.brocku.meetingscheduler;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class MeetingInformation extends AppCompatActivity {

    EditText meetingName, meetingAddress, meetingDate, startTime, endTime, contactname;

    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_information);

        initTextView();
        makeReadOnly();

        Bundle extras=getIntent().getExtras();
        if (extras!=null) {
            int id = extras.getInt("id");
            name = extras.getString("name");
            System.out.println(id);
            query(id);
        }
    }


    /**
     * initialises all the the text views
     */
    private void initTextView(){
        meetingAddress = (EditText)findViewById(R.id.editAddress);
        meetingName = (EditText)findViewById(R.id.editMeetingName);
        meetingDate = (EditText)findViewById(R.id.meetingDate);
        startTime = (EditText)findViewById(R.id.StartTime);
        endTime = (EditText)findViewById(R.id.endTime);
        contactname = (EditText)findViewById(R.id.contactFirstName);
    }


    /**
     * this class makes the fields filled by a button read only
     */
    private void makeReadOnly(){

        meetingName.setEnabled(false);
        meetingAddress.setEnabled(false);
        meetingDate.setEnabled(false);
        startTime.setEnabled(false);
        endTime.setEnabled(false);
        contactname.setEnabled(false);
    }

    /**
     * This queries the database to find which row matches the same provided address.
     * We then set all the text views to the data we want from the database
     *
     * @param meetingId
     */
    private void query(int meetingId) {

        String[] fields=new String[]{"meetingName","meetingDate","startTime","endTime","address"};

        ListView lv=(ListView)findViewById(R.id.allEntries);

        DataHelper dh=new DataHelper(this);
        SQLiteDatabase datareader=dh.getReadableDatabase();
        Cursor cursor=datareader.query(DataHelper.DB_TABLE,fields,
                "id = ?", new String[]{meetingId+""},null,null,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            meetingName.setText(cursor.getString(0));
            meetingAddress.setText(cursor.getString(4));
            meetingDate.setText(cursor.getString(1));
            startTime.setText(cursor.getString(2));
            endTime.setText(cursor.getString(3));
            contactname.setText(name);

            cursor.moveToNext();
        }

        if (cursor!=null && !cursor.isClosed())
            cursor.close();

        datareader.close();
    }


    /**
     * required for menu to work
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