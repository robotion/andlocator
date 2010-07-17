package com.jaeckel.locator;

import android.test.ActivityInstrumentationTestCase;

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
public class .MapTest extends ActivityInstrumentationTestCase<.Map> {

    public .MapTest() {
        super("com.jaeckel.locator", .Map.class);
    }

}
