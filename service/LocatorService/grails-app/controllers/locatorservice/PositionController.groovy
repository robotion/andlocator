package locatorservice

import com.jaeckel.locator.Location

class PositionController {

  def index = {

    render("{ status: OK }")

  }

  def save = {
    log.debug(params)

    Location p = new Location();

    p.encryptedPosition = params.position

    p.save()

    render("{ status: OK }")
  }

  def get = {
    render("{ status: OK }")

  }

  def list = {

    List<Location> locations = Location.list()

    def response = "{\n";

    locations.each {Location l ->
      response += "{ "
      response += "id: " + l.id + ", "
      response += "encryptedLocation: " + l.encryptedPosition + " "
      response += "}, \n"

    }

    response += " status: OK }";
    render(response)

  }
}
