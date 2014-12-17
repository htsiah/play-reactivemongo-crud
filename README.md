Reactivemongo for Play 2.3 Example
=======================
This example use the following:
<ul>
<li>Play Framework 2.3.6</li>
<li>Mongo DB</li>
<li>JQuery</li>
</ul>
Step by step setup
=======================
1. Modify project/Build.scala
<div><pre>
import sbt._
import Keys._
import play.Play.autoImport._
import PlayKeys._
import play.twirl.sbt.Import.TwirlKeys

object ApplicationBuild extends Build {

  val appName = "reactivemongo"
  val appVersion = "1.0"
    
  val appDependencies = Seq(
      "org.reactivemongo" %% "play2-reactivemongo" % "0.10.5.0.akka23"
  )
  
  val main = Project(appName, file(".")).enablePlugins(play.PlayScala).settings(
    version := appVersion,
    libraryDependencies ++= appDependencies
  )

}
</pre></div>

2. Create or append conf/play.plugins
<div><pre>
1100:play.modules.reactivemongo.ReactiveMongoPlugin
</pre></div>

3. Append application.conf
<div><pre>
# Reactivemongo default database
mongodb.uri = "mongodb://localhost:27017/reactivemongo"
</pre></div>

4. Remove built.sbt as we are using Build.scala. 
