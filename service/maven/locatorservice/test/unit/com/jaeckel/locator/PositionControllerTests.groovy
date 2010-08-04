package com.jaeckel.locator

import grails.test.ControllerUnitTestCase
import grails.converters.JSON

/**
 * User: biafra
 * Date: Aug 3, 2010
 * Time: 9:26:57 AM
 */
class PositionControllerTests extends  ControllerUnitTestCase {

  void testIndex() {

    def result = controller.index()

    println("result: " + result);

    assertEquals("Wrong view for index action: " + controller.renderArgs.view, "index", controller.renderArgs.view );


  }
}
