package com.jaeckel.locator.user;

import android.app.Activity;
import android.os.Bundle;
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
    }
}
