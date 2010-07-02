package locatorservice

class RestController {

  def index = {
    render (view: "index", model: [id: params.id])
  }
}
