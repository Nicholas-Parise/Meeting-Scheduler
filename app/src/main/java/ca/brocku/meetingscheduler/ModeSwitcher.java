package ca.brocku.meetingscheduler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.Toast;

// class based off code from lecture
public class ModeSwitcher {
    public static boolean handleMenuClicky(MenuItem item, Context from) { //Activity?

        if(item.getItemId() == R.id.menu_new){
            from.startActivity(new Intent(from,NewMeeting.class));
            ((Activity)from).finish();
        }else if(item.getItemId() == R.id.menu_home){
            from.startActivity(new Intent(from,MainActivity.class));
            ((Activity)from).finish();
        }else if(item.getItemId() == R.id.menu_drop){
            from.startActivity(new Intent(from,MeetingManager.class));
            ((Activity)from).finish();
        }else if(item.getItemId() == R.id.menu_search){
            from.startActivity(new Intent(from,ContactMeeting.class));
            ((Activity)from).finish();
        }

        return true;
    }
}
