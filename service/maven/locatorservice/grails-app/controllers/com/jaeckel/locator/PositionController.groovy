package com.jaeckel.locator

import com.google.appengine.api.datastore.Text
import grails.converters.XML
import grails.converters.JSON

class PositionController {

  def index = {

    render(view: "index")

  }

  def save = {

    Location l = new Location();

    l.encryptedPosition = new Text(params.position)
    l.timestamp = new Date()

    l.fromKey = params.fromKey
    l.toKey = params.toKey

    l.keybitcount = Integer.valueOf(params.keybitcount)

    l.status = ""

    def validationResult = l.validate()

    log.error(" VALIDATION: " + validationResult)

    def result = l.save()

    log.error("save: " + result)

    render l as JSON
  }

  def get = {

    // since="<timestamp>"  missing
    def locations

    if (params.fromKey != null && params.fromKey != "") {

      log.info("Search by fromKey: " + params.fromKey)

      locations = Location.findAll("select  from Location where fromKey='" + params.fromKey + "'");
    } else if (params.id != null && params.id != "") {

      log.info("Search by id: " + params.id)
      locations = Location.findAll("select  from Location where id=" + params.id);
    }

    render locations as JSON


  }

  def list = {

    log.info("listing Locations")

    List<Location> locations = Location.list()

    render(locations as JSON)

  }

  def remove = {
    Location b = Location.get(params.id)
    b.delete()
    redirect(action: list)
  }

  def currentPositions = {

    final long yesterday = new Date().getTime() - 24 * 60 * 60 * 1000
    long since = yesterday

    String query = "select  from Location where toKey='" + params.toKey + "'"

    if (params.since != null && params.since != "") {

      since = Long.valueOf(params.since)

    }


    def locations = Location.findAll(query);

    render locations as JSON

  }
}
