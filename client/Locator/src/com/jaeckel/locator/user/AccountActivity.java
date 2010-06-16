package com.jaeckel.locator.user;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import com.jaeckel.locator.R;

/**
 * User: biafra
 * Date: Jun 17, 2010
 * Time: 12:10:54 AM
 */
public class AccountActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.account);

        Button save = (Button) findViewById(R.id.save);

        save.setOnClickListener(onSave);
    }

    private View.OnClickListener onSave = new View.OnClickListener() {

        public void onClick(View view) {
            
            EditText name = (EditText) findViewById(R.id.name);
            EditText password = (EditText) findViewById(R.id.password);
            EditText password_again = (EditText) findViewById(R.id.password_again);
            EditText email = (EditText) findViewById(R.id.email);
            EditText pubKey = (EditText) findViewById(R.id.pubKey);
            EditText secKey = (EditText) findViewById(R.id.secKey);
            EditText passphrase = (EditText) findViewById(R.id.passphrase);


            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(AccountActivity.this);


            prefs.edit();

        }
    };
}
