package locatorservice

import com.jaeckel.locator.Location

class PositionController {

  def index = {

    render(view: "index")

  }

  def save = {
    log.debug(params)

    Location l = new Location();

    l.encryptedPosition = params.position
    l.timestamp = new Date()

    l.keyid = params.keyid
    l.keybitcount = Integer.valueOf(params.keybitcount)

    l.status = ""
    l.save()

    render("l: " + l)
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
        response += "encryptedLocation: " + l.encryptedPosition + " "
        response += "keyid: " + l.keyid + " "
        response += "}, \n"

      }
      response += "}";
      render(response)

      return

    } else if (params.id != null && params.id != "") {

      log.error("Search by id: " + params.id)

      def locations = Location.findAll("select  from Location where id=" + params.id);

      def response = "{\n";

      locations.each {Location l ->

        log.error("Location found: " + l)
        response += "{ "
        response += "id: " + l.id + ", "
        response += "timestamp: " + l.timestamp.time + ", "
        response += "encryptedLocation: " + l.encryptedPosition + " "
        response += "keyid: " + l.keyid + " "
        response += "}, \n"

      }

      response += "}";
      render(response)

      return
    }

    response += "}";
    render(response)

    return


  }

  def list = {

    List<Location> locations = Location.list()

    def response = "{\n";

    locations.each {Location l ->
      response += "{ "
      response += "id: " + l.id + ", "
      response += "timestamp: " + l.timestamp + ", "
      response += "encryptedLocation: " + l.encryptedPosition + " "
      response += "keyid: " + l.keyid + " "
      response += "}, \n"

    }

    response += " status: OK }";
    render(response)

  }

  def remove = {
    Location b = Location.get(params.id)
    b.delete()
    redirect(action: list)
  }
}
