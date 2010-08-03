package com.jaeckel.locator
/**
 * Created by IntelliJ IDEA.
 * User: biafra
 * Date: Aug 3, 2010
 * Time: 1:21:21 AM
 * To change this template use File | Settings | File Templates.
 */

class PositionControllerTests extends GroovyTestCase {

  PositionController controller

  void setUp() {
    controller = new PositionController()
  }

  void testList() {

    log.info("testList")

    def result = controller.list()

    assertEquals "[]", result

  }

}
