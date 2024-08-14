package ca.brocku.meetingscheduler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import android.Manifest;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;

// Nicholas Parise 7242530
// COSC 3P97 A2

public class MainActivity extends AppCompatActivity {

    ToggleButton toggle;
    boolean todayTomorrow = true;

    ArrayList<Integer> idArr;
    ArrayList<String> nameArr;
    private static final int PERMISSION_READ_CONTACTS = 777;//arbitrarily chosen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        idArr = new ArrayList<Integer>();

        nameArr = new ArrayList<String>();

        toggle = (ToggleButton) findViewById(R.id.toggle);
        toggle.setOnClickListener(view -> {
            if (toggle.isChecked()) {
                todayTomorrow = false;
                query();
            } else {
                todayTomorrow = true;
                query();
            }
        });

        if (checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
            query();
        }else {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_READ_CONTACTS);
        }

    }


    /**
     * permissions to allow program to access the contacts
     *
     * @param requestCode The request code passed in {@link #requestPermissions(
     * android.app.Activity, String[], int)}
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     *
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode,permissions,grantResults);

        if (requestCode == PERMISSION_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) { //Permission granted
                getContactName(1);
            }
        }
    }


    /**
     * launch the new meeting activity
     * @param view
     */
    public void newMeeting(View view) {
        startActivity(new Intent(this,NewMeeting.class));
    }


    /**
     * either today's or tomorrows date depending on the flag
     * @return string date
     */
    private String todayTomorrow(){

        java.util.Calendar cal=java.util.Calendar.getInstance();
        int year = cal.get(java.util.Calendar.YEAR);
        int monthOfYear = cal.get(java.util.Calendar.MONTH);
        int dayOfMonth = cal.get(java.util.Calendar.DAY_OF_MONTH);

        if(todayTomorrow){
            return dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
        }else{
            return (dayOfMonth+1) + "-" + (monthOfYear + 1) + "-" + year;
        }
    }

    /**
     * we query the database the find all the rows that correspond to the selected date
     */
    private void query() {

        idArr.clear(); // empty array
        nameArr.clear();

        String[] fields=new String[]{"meetingName","meetingDate","startTime","endTime","contactId","id"};

        ListView lv=(ListView)findViewById(R.id.allEntries);

        ArrayList<String> entries=new ArrayList<>();
        DataHelper dh=new DataHelper(this);
        SQLiteDatabase datareader=dh.getReadableDatabase();
        Cursor cursor=datareader.query(DataHelper.DB_TABLE,fields,
                null,null,null,null,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            // if the date is the right one
            if(todayTomorrow().equals(cursor.getString(1))) {
                entries.add(cursor.getString(0) + " with " + getContactName(cursor.getInt(4))+"\non "+
                        cursor.getString(1) + ", between " + cursor.getString(2) + " -> " + cursor.getString(3)+" "+cursor.getInt(5));
                idArr.add(cursor.getInt(5));
                nameArr.add(getContactName(cursor.getInt(4)));
            }

            cursor.moveToNext();
        }
        if (cursor!=null && !cursor.isClosed())
            cursor.close();
        ArrayAdapter<String> adapter=new ArrayAdapter<>(this,
                R.layout.mytextview,entries);
        lv.setAdapter(adapter);
        registerForContextMenu(lv);
        datareader.close();
    }


    /**
     * This allows for the list view to start the meetingInformation activity
     *
     * @param menu The context menu that is being built
     * @param v The view for which the context menu is being built
     * @param menuInfo Extra information about the item for which the
     *            context menu should be shown. This information will vary
     *            depending on the class of v.
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.allEntries) {
            AdapterView.AdapterContextMenuInfo cmi= (AdapterView.AdapterContextMenuInfo) menuInfo;

            Intent intent=new Intent(this, MeetingInformation.class);
            intent.putExtra("id",idArr.get(cmi.position));
            intent.putExtra("name",nameArr.get(cmi.position));

            startActivity(intent);
        }
    }


    /**
     *  given a contact ID we query the contacts database to find the corresponding entry
     *  once found we return the string name
     *
     * @param id contact id
     * @return contact name
     */
    public String getContactName(int id){

        Uri contactsUri= Uri.parse("content://contacts/people");
        String[] projection=new String[]{ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME};
        Cursor c;
        CursorLoader cursorLoader=new CursorLoader(this, contactsUri, projection, null, null, null);
        c=cursorLoader.loadInBackground();

        c.moveToFirst();
        while (c.isAfterLast()==false) {

            String contactID = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));

            String contactDisplayName=c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

            int newId = Integer.valueOf(c.getString(c.getColumnIndex(ContactsContract.Contacts._ID)));

            if(newId == id){
                c.close();
                return contactDisplayName;
            }

            c.moveToNext();
        }
        if (c!=null && !c.isClosed()) {
            c.close();
        }

        return "Contact not found";
    }


    /**
     * required to get menu to work
     *
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