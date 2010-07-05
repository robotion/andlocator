package com.jaeckel.locator

import grails.test.*
import com.google.appengine.tools.development.testing.LocalServiceTestHelper
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig
import com.google.appengine.api.datastore.DatastoreService
import com.google.appengine.api.datastore.Query
import com.google.appengine.api.datastore.Entity

class PositionControllerTests extends ControllerUnitTestCase {

  LocalServiceTestHelper testHelper

  protected void setUp() {
    super.setUp()
    testHelper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig())
    testHelper.setUp()
  }

  protected void tearDown() {
    super.tearDown()
    testHelper.tearDown()
  }

  void testSomething() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

    assertEquals(0, ds.prepare(new Query("yam")).countEntities());
    ds.put(new Entity("yam"));
    ds.put(new Entity("yam"));
    assertEquals(2, ds.prepare(new Query("yam")).countEntities());
  }

  void testSave() {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    assertEquals(0, ds.prepare(new Query("yam")).countEntities());
    ds.put(new Entity("yam"));
    ds.put(new Entity("yam"));
    assertEquals(2, ds.prepare(new Query("yam")).countEntities());
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
