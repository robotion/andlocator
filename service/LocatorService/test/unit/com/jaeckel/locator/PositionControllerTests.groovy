package com.jaeckel.locator

import grails.test.*
import org.hibernate.mapping.Value

class PositionControllerTests extends ControllerUnitTestCase {


  protected void setUp() {
    super.setUp()
  }

  protected void tearDown() {
    super.tearDown()
  }

  void testSomething() {

  }

  void testSave() {
    
    mockDomain(Location, [
            new Location(toKey: "foo", fromKey: "bar"),
            new Location(toKey: "biafra", fromKey: "jello")
    ])

    this.controller.params.position = "bla"
    this.controller.params.toKey = "baz"
    this.controller.params.fromKey = "bam"

    def result = this.controller.save()

    assertEquals(result, "")
  }
}


/*
*
  Text encryptedPosition

  Date timestamp
  String status
  String toKey
  String fromKey
  Integer keybitcount = 0

*/
