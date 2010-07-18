package com.jaeckel.locator.contacts;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Menu;
import android.content.Intent;
import android.widget.Toast;
import com.jaeckel.locator.EditPreferences;
import com.jaeckel.locator.user.AccountActivity;

/**
 * User: biafra
 * Date: Jun 21, 2010
 * Time: 12:16:23 AM
 */
public class ContactsActivity extends ListActivity {
    private static final int MENU_ADD_CONTACT = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case MENU_ADD_CONTACT:

                startActivity(new Intent(this, AddContactActivity.class));
                return true;

        }

        return false;
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(Menu.NONE, MENU_ADD_CONTACT, Menu.NONE, "Add Contact");

        return true;
    }
}
