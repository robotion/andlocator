package com.jaeckel.locator

import grails.test.ControllerUnitTestCase

/**
 * User: biafra
 * Date: Aug 3, 2010
 * Time: 9:26:57 AM
 */
class PositionControllerTests extends  ControllerUnitTestCase {

  void testIndex() {

    controller.index()
    assertEquals("Wrong View for index action: " + controller.response.view, "index", controller.response.view );


  }
}
