package com.jaeckel.locator


class RestController {

  

  def index = {
    render (view: "index", model: [id: params.id])
  }


  def save = { saveCmd ->

  }

  def show = { showCmd ->

  }

  def update = { updateCmd ->

  }

  def delete = { deleteCmd ->
    
  }


}
