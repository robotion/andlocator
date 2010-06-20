package com.jaeckel.locator.contacts;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Menu;
import com.jaeckel.locator.R;

/**
 * User: biafra
 * Date: Jun 21, 2010
 * Time: 12:59:08 AM
 */
public class AddContactActivity extends Activity {
    private static final int MENU_CONTACTS = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_contact);

    }

     public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case MENU_CONTACTS:


                return true;

        }

        return false;
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(Menu.NONE, MENU_CONTACTS, Menu.NONE, "Add Contact");


        return true;
    }
}
