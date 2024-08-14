package ca.brocku.meetingscheduler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.time.chrono.Chronology;
import java.util.Calendar;

public class NewMeeting extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    EditText meetingName, meetingAddress, meetingDate, startTime, endTime, contactname;
    int contactId;

    boolean start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_meeting);

        contactId = 0;

        meetingAddress = (EditText)findViewById(R.id.editAddress);
        meetingName = (EditText)findViewById(R.id.editMeetingName);
        meetingDate = (EditText)findViewById(R.id.meetingDate);
        startTime = (EditText)findViewById(R.id.StartTime);
        endTime = (EditText)findViewById(R.id.endTime);
        contactname = (EditText)findViewById(R.id.contactFirstName);

        makeReadOnly();
    }


    /**
     * this class makes the fields filled by a button read only
     */
    private void makeReadOnly(){

        meetingDate.setEnabled(false);
        startTime.setEnabled(false);
        endTime.setEnabled(false);
        contactname.setEnabled(false);
    }


    /**
     * on submit we want to clear the fields
     */
    private void resetMeeting(){

        meetingName.getText().clear();
        meetingAddress.getText().clear();
        meetingDate.getText().clear();
        startTime.getText().clear();
        endTime.getText().clear();
        contactname.getText().clear();

        android.widget.Toast.makeText(this,"Successfully created", Toast.LENGTH_SHORT).show();
    }


    /**
     * This method is used to create the row in the database with the data on the screen
     * @param v
     */
    public void save(View v) {

        DataHelper dh = new DataHelper(this);
        SQLiteDatabase datachanger=dh.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put("meetingName",meetingName.getText().toString());
        contentValues.put("meetingDate",meetingDate.getText().toString());
        contentValues.put("startTime",startTime.getText().toString());
        contentValues.put("endTime",endTime.getText().toString());
        contentValues.put("contactId",contactId);
        contentValues.put("address",meetingAddress.getText().toString());

        datachanger.insert(DataHelper.DB_TABLE,null,contentValues);
        datachanger.close();
        //startActivity(new Intent(this,ShowWisdom.class));
        resetMeeting();
    }


    /**
     * code to select contact
     * @param v
     */
    public void selectContact(View v){
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK,
                Uri.parse("content://contacts"));
// Show user only contacts with phone numbers
        pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(pickContactIntent, 123);
    }

    /**
     * code to pick date
     * @param v
     */
    public void pickDate(View v){

        java.util.Calendar today=java.util.Calendar.getInstance();
        new DatePickerDialog(NewMeeting.this, //context
                this, //listener
                today.get(java.util.Calendar.YEAR),
                today.get(java.util.Calendar.MONTH),
                today.get(java.util.Calendar.DAY_OF_MONTH)).show();
    }


    /**
     * code to pick time, depending on which button is pressed we set a flag "start"
     * @param v
     */
    public void pickTime(View v){

        if(v == (Button)findViewById(R.id.pickStartTime)) {
            start = true;
        }else{
            start = false;
        }

        java.util.Calendar today=java.util.Calendar.getInstance();
        new TimePickerDialog(this,
                this,
                today.get(Calendar.HOUR_OF_DAY),
                today.get(Calendar.MINUTE),
                false //24-hour display?
        ).show();
    }


    /**
     * here we set the text view with the supplied data
     *
     * @param view the picker associated with the dialog
     * @param year the selected year
     * @param monthOfYear the selected month (0-11 for compatibility with
     *              {@link Calendar#MONTH})
     * @param dayOfMonth the selected day of the month (1-31, depending on
     *                   month)
     */
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

       meetingDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
    }

    /**
     * here we use the flag to set the correct text view
     *
     * @param view the view associated with this listener
     * @param hour the hour that was set
     * @param minute the minute that was set
     */
    public void onTimeSet(TimePicker view, int hour, int minute) {

        if(start) {
            startTime.setText(hour + ":" + minute);
        }else {
            endTime.setText(hour + ":" + minute);
        }
    }


    /**
     * after picking a contact we go through the table and extract the name and the unique ID which we save to a var
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     *
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 123) {
            if (resultCode == RESULT_OK) { //User picked a contact; didn't cancel out
                // The Intent's data Uri identifies which contact was selected.
                Uri datadata=data.getData();

                String[] needed={ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone._ID,ContactsContract.Contacts._ID,ContactsContract.CommonDataKinds.Phone.CONTACT_ID};
                //This part should probably be in a different thread
                Cursor cursor=getContentResolver().query(datadata,needed,null,null,null);
                cursor.moveToFirst();

                contactId = Integer.valueOf(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));
                contactname.setText(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
            }
        }
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