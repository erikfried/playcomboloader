package controllers

import play.api.mvc._
import java.lang.{Iterable, String}
import java.net.URL
import java.io.File
import play.api.{Play, Logger}

import play.api.Play.current
import io.Source


object ComboLoader extends Controller {

  /**
   * Generates an `Action` that serves a combo resource.
   *
   * @param path the root folder for searching the module files, such as `"/public"`
   */
  def load(path: String, version: String): Action[AnyContent] = Action { request =>

    def getMinifiedNameInProd(fileName: String): String = {
      if (Play.isProd) {
        fileName.replaceAll(".js", ".min.js")
      } else {
        fileName
      }
    }

    def ensureLeadingSlash(fileName: String): String = {
      if (fileName.startsWith("/")) fileName else "/" + fileName
    }

    val files = request.queryString.keys
    Logger.debug("Files: " + files)
    val data: String = files.foldLeft("") { (combo, fileName)  =>
      val resource: Option[URL] = Play.resource(path + ensureLeadingSlash(getMinifiedNameInProd(fileName) ) )
      val lines = resource.map {
        case url if new File(url.getFile).isDirectory =>
          Logger.warn("Not found (is dir):" +url)
          "/*Not found " +url + "*/"
        case url => {
          val stream = url.openStream()
          val source = Source.fromInputStream(stream, "UTF-8").getLines()
          source.mkString("\n")
        }
      }
      combo ++ lines.getOrElse("/*No resource found for " + path + fileName +"*/")
    }
    Ok(data).as("application/javascript")
  }
}