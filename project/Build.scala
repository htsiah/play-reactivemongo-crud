import sbt._
import Keys._

object ApplicationBuild extends Build {

  val appName = "reactivemongo"
  val appVersion = "1.0"
    
  val appDependencies = Seq(
      "org.reactivemongo" %% "play2-reactivemongo" % "0.10.5.0.akka23"
  )
  
  val main = Project(appName, file(".")).enablePlugins(play.PlayScala).settings(
    version := appVersion,
    libraryDependencies ++= appDependencies
    // routesImport += "se.radley.plugin.salat.Binders._",
    // TwirlKeys.templateImports += "org.bson.types.ObjectId"
  )

}