package ca.brocku.meetingscheduler;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class ContactMeeting extends AppCompatActivity {

    int contactId;
    EditText contactname;
    String contactStr;

    ArrayList<Integer> idArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_meeting);

        idArr = new ArrayList<Integer>();

        contactname = (EditText)findViewById(R.id.contactFirstName);
        contactname.setEnabled(false);
    }


    /**
     * This is to allow the user to select a contact with the button
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
     * once the user selects a contact we get the contact id and contact name and save them to instance var
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
                contactStr = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                contactname.setText(contactStr);
                query();
            }
        }
    }


    /**
     * We query the database to get all the rows where the contactid is equal to the selected contact.
     * it populates the list view and also sets up for the popup menu by adding to the array.
     */
    private void query() {

        idArr.clear(); // empty array

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
            if(contactId == cursor.getInt(4)) {
                entries.add(cursor.getString(0) + " with " + contactStr+"\non "+
                        cursor.getString(1) + ", between " + cursor.getString(2) + " -> " + cursor.getString(3));
                idArr.add(cursor.getInt(5)); // add id to array
            }

            cursor.moveToNext();
        }
        if (cursor!=null && !cursor.isClosed())
            cursor.close();
        ArrayAdapter<String> adapter=new ArrayAdapter<>(this, R.layout.mytextview,entries);
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
            intent.putExtra("name",contactStr);

            startActivity(intent);
        }
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