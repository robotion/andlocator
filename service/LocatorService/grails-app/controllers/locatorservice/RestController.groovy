package locatorservice

class RestController {

  def index = {
    render params.id
  }
}
