package com.jaeckel.locator

import com.google.appengine.api.datastore.Text
import grails.converters.XML

class PositionController {

  def index = {

    render(view: "index")

  }

  def save = {
    log.error("--------> " + params.position)

    Location l = new Location();

    l.encryptedPosition = new Text(params.position)
    l.timestamp = new Date()

    l.fromKey = params.fromKey
    l.toKey = params.toKey

//    l.keybitcount = Integer.valueOf(params.keybitcount)

    l.status = ""

    def validationResult = l.validate()

    log.error(" VALIDATION: " + validationResult)

    def result = l.save()

    log.error("save: " + result)

    render l as XML
  }

  def get = {

    // since="<timestamp>"  missing

    if (params.keyid != null && params.keyid != "") {

      log.error("Search by keyid: " + params.keyid)

      def locations = Location.findAll("select  from Location where keyid='" + params.keyid + "'");

      def response = "{\n";

      locations.each {Location l ->

        log.error("Location found: " + l)
        response += "{ "
        response += "id: " + l.id + ", "
        response += "timestamp: " + l.timestamp.time + ", "
        response += "encryptedLocation: " + l.encryptedPosition.value + " "
        response += "keyid: " + l.keyid + " "
        response += "}, \n"

      }
      response += "}";
      render(response)

      return

    } else if (params.id != null && params.id != "") {

      log.error("Search by id: " + params.id)

      def locations = Location.findAll("select  from Location where id=" + params.id);

      def getResponse = "{\n";

      locations.each {Location l ->

        log.error("Location found: " + l)
        getResponse += "{ "
        getResponse += "id: " + l.id + ", "
        getResponse += "timestamp: " + l.timestamp.time + ", "
        getResponse += "encryptedLocation: " + l.encryptedPosition.value + " "
        getResponse += "keyid: " + l.keyid + " "
        getResponse += "}, \n"

      }

      getResponse += "}";
      render(getResponse)

      return
    }

    response += "}";
    render(response)

    return


  }

  def list = {
    log.debug("listing Locations")
    List<Location> locations = Location.list()

    def listResponse = "{\n";

    locations.eachWithIndex {Location l ->
      listResponse += "{ "
      listResponse += "id: " + l.id + ", "
      listResponse += "timestamp: " + l.timestamp + ", "
      listResponse += "encryptedLocation: " + l.encryptedPosition.value + " "
      listResponse += "fromKey: " + l.fromKey + " "
      listResponse += "toKey: " + l.toKey + " "
      listResponse += "}, \n"

    }

    listResponse += " status: OK }";
    render(listResponse)

  }

  def remove = {
    Location b = Location.get(params.id)
    b.delete()
    redirect(action: list)
  }

  def currentPositions = {

    final long yesterday = new Date().getTime() - 24 * 60 * 60 * 1000
    long since = yesterday

    String query = "select  from Location where keyid='" + params.keyid + "'"

    if (params.since != null && params.since != "") {

      since = Long.valueOf(params.since)

    }


    def locations = Location.findAll(query);

    def response = "{\n";

    locations.each {Location l ->


      if (l.timestamp.time > since) {

        log.error("Location found: " + l)
        response += "{ "
        response += "id: " + l.id + ", "
        response += "timestamp: " + l.timestamp.time + ", "
        response += "encryptedLocation: " + l.encryptedPosition.value + " "
        response += "keyid: " + l.keyid + " "
        response += "}, \n"

      }

    }

    response += "}";

    render(response)

  }
}
