package com.jaeckel.locator;

import android.test.AndroidTestCase;
import android.util.Log;
import com.jaeckel.locator.pgp.PubKeyGenerator;

/**
 * User: biafra
 * Date: Jul 18, 2010
 * Time: 1:19:41 PM
 */

public class PubKeyGeneratorTest extends AndroidTestCase {

    private PubKeyGenerator instance;

    public void setUp() {

        Log.i("TEST", "setUp");
        instance = new PubKeyGenerator();
    }

    public void tearDown() {

        Log.i("TEST", "tearDown");

    }

    public void testCreateKeyPair() {

        Log.i("TEST", "testCreateKeyPair");

        String[] keyPair = instance.createKeyPair();
        assertNotNull("KeyPair must not be null", keyPair);
        assertEquals("KeyPair must have length 2", 2, keyPair.length);

    }

}
