package com.jaeckel.locator;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class com.jaeckel.locator..MapTest \
 * com.jaeckel.locator.tests/android.test.InstrumentationTestRunner
 */
public class MapTest extends ActivityInstrumentationTestCase2<Map> {

    private Map instance;

    public MapTest() {
        super("com.jaeckel.locator", Map.class);
    }

    @Override
    public void setUp() {
        Log.i("MAPTEST", "setUp()");
        instance = new Map();
    }

    public void testTestMe() {
        Log.i("MAPTEST", "testTestMe()");
        assertTrue("testMe", instance.testMe(true));
    }
}
