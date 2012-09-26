package controllers

import play.api._
import play.api.mvc._

object ComboLoader extends Controller {
  
  def index = Action {
    Ok("Hey I am here")
  }
  
}