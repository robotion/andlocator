package com.jaeckel.locator

import grails.test.ControllerUnitTestCase
/**
 * User: biafra
 * Date: Aug 3, 2010
 * Time: 1:50:21 AM
 */

class UserControllerTests extends ControllerUnitTestCase {

  void testIndex() {

    controller.index()
    assertEquals("{OK: HELO}", controller.response.contentAsString );


  }

}
