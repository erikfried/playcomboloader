package controllers

import play.api._
import libs.concurrent.Promise
import libs.iteratee.{Iteratee, Enumerator}
import play.api.mvc._
import java.io.{BufferedReader, FileReader, File}

import play.api.Play.current
import io.Source

object Application extends Controller {
  
  def index = Action {
    Ok(views.html.index("My application"))
  }

  def testNotWorks = Action {
    val file1 = new File(Play.resource("/public/javascripts/moduleA.js").get.getFile)
    val enumerator1 = Enumerator.fromFile(file1)

    val file2 = new File(Play.resource("/public/javascripts/moduleB.js").get.getFile)
    val enumerator2 = Enumerator.fromFile(file2)


    SimpleResult(
      ResponseHeader(OK),
      enumerator1.andThen(enumerator2)
    )
  }
  
}