package controllers

import play.api.mvc._
import java.lang.{Iterable, String}
import java.net.URL
import java.io.File
import play.api.{Mode, Play, Logger}
import play.api.libs.iteratee.Enumerator
import play.api.libs.MimeTypes
import scala.collection

import play.api.Play.current //To put an implicit application into scope

object ComboLoader extends Controller {

  /**
   * Generates an `Action` that serves a combo resource.
   *
   * @param path the root folder for searching the module files, such as `"/public"`
   */
  def load(path: String): Action[AnyContent] = Action { request =>

    Logger.info("Load path: " +path )

    val files = request.queryString.keys
    //val path = "/public"

    val data: collection.Iterable[Option[(Int, Enumerator[Array[Byte]])]] = files map { fileName =>
      val resourceName = if (fileName.startsWith("/")) fileName else "/" + fileName
      val resource: Option[URL] = Play.resource(path + resourceName)
      //val stream = new URL(resource).openStream()
      Logger.info(new File(path + resourceName).getCanonicalPath + ": " + resource.toString);
      resource.map {
        case url if new File(url.getFile).isDirectory =>
          Logger.info("Not found (is dir):" +url)
          (0, Enumerator[Array[Byte]]())
        case url if new File(url.getFile).exists() => {
          Logger.info("found " +url)
          val stream = url.openStream()
          (stream.available(), Enumerator.fromStream(stream).andThen(Enumerator.eof) )
        }
        case other =>
          Logger.info("Not found: " + other)
          (0, Enumerator[Array[Byte]]())
      }
    }

    Logger.info("Data: " +data.toString());
    val all = data.flatten.foldLeft ((0,Enumerator[Array[Byte]]() )) { (l, r) =>
      (l._1 + r._1, l._2.andThen(r._2))
    }
    //val resourceName = Option(path + "/" + file).map(name => if (name.startsWith("/")) name else ("/" + name)).get
    //Logger.info("File " +resourceName)
    //if (new File(resourceName).isDirectory || !new File(resourceName).getCanonicalPath.startsWith(new File(path).getCanonicalPath)) {
    //  NotFound
    //} else {

    //val resource = Play.resource(resourceName)

    //val data = resource.map {
    //val res = resource.map {

    //   case url if new File(url.getFile).isDirectory => NotFound //None           //TODO
    //   case (url) => {

    /*     lazy val (length, resourceData) = {
      val stream = url.openStream()
      try {
        (stream.available, Enumerator.fromStream(stream))
      } catch {
        case _ => (0, Enumerator[Array[Byte]]())
      }
    }*/

    val res = if (all._1 == 0) {
      NotFound("Content length == 0 for " + path)                      //TODO
    } else {

      // Prepare a streamed response
      val response = SimpleResult(
        header = ResponseHeader(OK, Map(
          CONTENT_LENGTH -> all._1.toString,
          CONTENT_TYPE -> MimeTypes.forFileName(files.head).getOrElse(BINARY)
        )),
        body = all._2.andThen(Enumerator.eof)
      )
      response
    }

    // }.getOrElse(NotFound)
    //val res = response
    Logger.info("res:" + res.toString)
    //res
    //TODO zip if possible
    /*val resource = {
    Play.resource(resourceName + ".gz").map(_ -> true)
      .filter(_ => request.headers.get(ACCEPT_ENCODING).map(_.split(',').exists(_ == "gzip" && Play.isProd)).getOrElse(false))
      .orElse(Play.resource(resourceName).map(_ -> false))
  }  */
    /*
    val gzippedResponse = if (isGzipped) {
      response.withHeaders(CONTENT_ENCODING -> "gzip")
    } else {
      response
    }
    */
    // Add Cache directive
    val cachedResponse = res.withHeaders(CACHE_CONTROL -> {
      Play.mode match {
        case Mode.Prod => Play.configuration.getString("assets.defaultCache").getOrElse("max-age=3600")
        case _ => "no-cache"
      }
    })

    Logger.info("cached:" +cachedResponse.body.toString)
    cachedResponse
    // data.map{ }.getOrElse(NotFound)
  }
  
}