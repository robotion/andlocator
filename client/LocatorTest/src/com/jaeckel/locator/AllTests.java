package com.jaeckel.locator;

/**
 * User: biafra
 * Date: Jul 18, 2010
 * Time: 12:47:08 AM
 */

import junit.framework.Test;
import junit.framework.TestSuite;
import android.test.suitebuilder.TestSuiteBuilder;


/**
 * A test suite containing all tests for my application.
 */
public class AllTests extends TestSuite {

    public static Test suite() {
        System.out.println("FNORDDDDDDDDDDDDDDD");
        System.out.println("FNORDDDDDDDDDDDDDDD");
        System.out.println("FNORDDDDDDDDDDDDDDD");
        System.out.println("FNORDDDDDDDDDDDDDDD");
        System.out.println("FNORDDDDDDDDDDDDDDD");
        System.out.println("FNORDDDDDDDDDDDDDDD");
        return new TestSuiteBuilder(AllTests.class).includeAllPackagesUnderHere().build();
    }

}