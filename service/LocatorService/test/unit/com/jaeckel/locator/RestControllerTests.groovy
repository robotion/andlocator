package com.jaeckel.locator

import grails.test.*
import org.junit.Test;

class RestControllerTests extends ControllerUnitTestCase {

  protected void setUp() {
    super.setUp()
  }

  protected void tearDown() {
    super.tearDown()
  }

  @Test
  void testSomething() {


    controller.params.id = 10

    def model = controller.index()

    assertTrue(model["id"] == 10)
    assertEqulas(model["id"], 10)

//    fail ("Foo")
  }
} 
