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
    log.error("Gugug")

    List<Location> locations = Location.list()

    def response = "{";

    locations.each { Location l ->
      log.error("Location: " + l.encryptedPosition )
      response += "encryptedLocation: " + l.encryptedPosition + ", "


    }

    response += " status: OK }";
    render(response)

  }
}
