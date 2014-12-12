package models

import scala.util.{Success, Failure}

import play.api.libs.concurrent._
import play.api.libs.concurrent.Execution.Implicits._

import reactivemongo.api._
import reactivemongo.bson._

case class Person (
    _id: BSONObjectID,
    name: String
)
  
object PersonModel {
  
  	// Use Reader to deserialize document automatically
	implicit object PersonBSONReader extends BSONDocumentReader[Person] {
		def read(doc: BSONDocument): Person =
			Person(
					doc.getAs[BSONObjectID]("_id").get,
					doc.getAs[String]("name").get
			)
	}
	
	// Use Writer to serialize document automatically
	implicit object PersonBSONWriter extends BSONDocumentWriter[Person] {
		def write(person: Person): BSONDocument =
			BSONDocument(
					"_id" -> person._id,
					"name" -> person.name
			)
	}
    
	// Call MongoDriver
	val driver = new MongoDriver
    
	// Connect to localhost
	val connection = driver.connection(List("localhost"))
	
	// Connect to mongodb
	val db = connection.db("reactivemongo")
	
	// Connect to mongodb collection
	val collection = db.collection("persons")
	
	def insert (p_doc:Person)= {
	  val future = collection.insert(p_doc)
	  future.onComplete {
	  	case Failure(e) => throw e
	  	case Success(lastError) => {
	  		println("successfully inserted document with lastError = " + lastError)
	  	}
	  }
	  
	}
	
}