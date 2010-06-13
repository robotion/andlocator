package locatorservice

class UserController {

  def index = {
  }



  def create = {

    if (request.isSecure()) {
      render("is SSL")
    } else {
      render("no SSL")

    }
  }

  def list = {
  }

  def get = {
    // by name


  }

  def changePassword = {


  }

}
