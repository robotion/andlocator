package com.jaeckel.locator


class RestController {

  

  def index = {
    render (view: "index", model: [id: params.id])
  }


  def save = { saveCmd ->

    render "FNORD"
  }

  def show = { showCmd ->

  }

  def update = { updateCmd ->

  }

  def delete = { deleteCmd ->
    
  }


}
