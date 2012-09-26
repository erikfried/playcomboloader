import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "playcombo-sample"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      "yui3comboloader" % "yui3comboloader_2.9.1" % "1.0-SNAPSHOT"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      resolvers += "Local Play Repository" at "file:///usr/local/Cellar/play/2.0.3/libexec/repository/local"
    )

}
