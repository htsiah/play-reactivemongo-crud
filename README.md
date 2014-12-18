Reactivemongo for Play 2.3 Example
=======================
This is a CRUD Example Play Application using reactiveMongo driver. It demontrates:
<ul>
<li>MongoDb Connection using reactiveMongoDb in Play</li>
<li>BSONReader and BSONWriter Implementation</li>
<li>CRUD using different data type(String, JodaDate, Integer, Double, Boolean and List)</li>
</ul>

This example use the following:
<ul>
<li>Play Framework 2.3.6</li>
<li>reactiveMongoDb Driver</li>
<li>MongoDb</li>
<li>JQuery</li>
</ul>
Setup Instruction
=======================
Modify project/Build.scala
<div class="highlight highlight-scala"><pre>
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
  )

}
</pre></div>

Create or append conf/play.plugins
<div class="highlight highlight-scala"><pre>
1100:play.modules.reactivemongo.ReactiveMongoPlugin
</pre></div>

Append application.conf
<div class="highlight highlight-scala"><pre>
# Reactivemongo default database
mongodb.uri = "mongodb://localhost:27017/reactivemongo"
</pre></div>

Remove built.sbt as we are using Build.scala. 
