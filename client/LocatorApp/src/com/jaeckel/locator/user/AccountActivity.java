package com.jaeckel.locator.user;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import com.jaeckel.locator.R;
import com.jaeckel.locator.pgp.KeyBasedProcessor;

import java.io.*;

/**
 * User: biafra
 * Date: Jun 17, 2010
 * Time: 12:10:54 AM
 */
public class AccountActivity extends Activity {

    SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(AccountActivity.this);

        setContentView(R.layout.account);
        EditText name = (EditText) findViewById(R.id.name);
        name.setText(prefs.getString("name", ""));
        
        EditText password = (EditText) findViewById(R.id.password);
        password.setText(prefs.getString("password", ""));

        EditText password_again = (EditText) findViewById(R.id.password_again);
        password_again.setText(prefs.getString("password", ""));

        EditText email = (EditText) findViewById(R.id.email);
        email.setText(prefs.getString("email", ""));

        EditText pubKey = (EditText) findViewById(R.id.pubKey);
        pubKey.setText(prefs.getString("pubKey", ""));

        EditText secKey = (EditText) findViewById(R.id.secKey);
        secKey.setText(prefs.getString("secKey", ""));

        EditText passphrase = (EditText) findViewById(R.id.passphrase);
        passphrase.setText(prefs.getString("passphrase", ""));

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


            SharedPreferences.Editor editor = prefs.edit();

            final String nameString = name.getText().toString();
            final String passwordString = password.getText().toString();
            final String emailString = email.getText().toString();
            String pubKeyString = pubKey.getText().toString();
            final String secKeyString = secKey.getText().toString();
            final String passphraseString = passphrase.getText().toString();


            editor.putString("name", nameString);
            editor.putString("password", passwordString);
            editor.putString("email", emailString);
            editor.putString("pubKey", pubKeyString);
            editor.putString("secKey", secKeyString);
            editor.putString("passphrase", passphraseString);

            editor.commit();

            long pubKeyId = 0;
            if (new File(pubKeyString).exists()) {

                pubKeyId = KeyBasedProcessor.GetKeyId(new File(pubKeyString));

                File file = new File(pubKeyString);
                StringBuffer contents = new StringBuffer();
                BufferedReader reader = null;

                try {
                    reader = new BufferedReader(new FileReader(file));
                    String text = null;

                    // repeat until all lines is read
                    while ((text = reader.readLine()) != null) {
                        contents.append(text)
                                .append(System.getProperty(
                                        "line.separator"));
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                pubKeyString = contents.toString();


            } else {
                pubKeyId = KeyBasedProcessor.GetKeyId(pubKeyString);

            }

            String result = AccountManager.createAccount(new Account(
                    nameString,
                    emailString,
                    passwordString,
                    "" + pubKeyId,
                    pubKeyString)
            );
            Toast.makeText(AccountActivity.this, "createAccount -> result: " + result, Toast.LENGTH_LONG).show();

        }
    };
}
