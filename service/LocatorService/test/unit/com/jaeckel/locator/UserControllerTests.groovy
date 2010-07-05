package com.jaeckel.locator

import grails.test.*

class UserControllerTests extends ControllerUnitTestCase {
  
  protected void setUp() {
    super.setUp()
  }

  protected void tearDown() {
    super.tearDown()
  }

  void testIndex() {
    fail("testIndex")

  }

  void testCreateNotSecure() {

    println("testCreateNotSecure")
//    mockDomain(User, [
//
//    ])
    controller.params.name = "foo"

    def result = controller.create()

    assertEquals("no SSL", result)

    fail("testCreateNotSecure")
  }

  void testGet() {
    println("testGet")

    fail("testGet")

  }

  void testList() {
    println("testList")
    fail("testList")

  }

}
