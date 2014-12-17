package models

import scala.util.{Success, Failure}
import org.joda.time.DateTime

import play.api.libs.concurrent._
import play.api.libs.concurrent.Execution.Implicits._

import reactivemongo.api._
import reactivemongo.bson._

case class Person (
    _id: BSONObjectID,
    _creationDate: Option[DateTime],
    _updateDate: Option[DateTime],
    name: String,
    dob: Option[DateTime],
    age: Int,
    salary: Double,
    admin: Boolean
)
  
object PersonModel {
  
  	// Use Reader to deserialize document automatically
	implicit object PersonBSONReader extends BSONDocumentReader[Person] {
		def read(doc: BSONDocument): Person = {
			Person(
				doc.getAs[BSONObjectID]("_id").get,
				doc.getAs[BSONDateTime]("_creationDate").map(dt => new DateTime(dt.value)),
				doc.getAs[BSONDateTime]("_updateDate").map(dt => new DateTime(dt.value)),
				doc.getAs[String]("name").get,
				doc.getAs[BSONDateTime]("dob").map(dt => new DateTime(dt.value)),
				doc.getAs[Int]("age").getOrElse(0),
				doc.getAs[Double]("salary").getOrElse(0.0),
				doc.getAs[Boolean]("admin").getOrElse(false)
			)
		}
	}
	
	// Use Writer to serialize document automatically
	implicit object PersonBSONWriter extends BSONDocumentWriter[Person] {
		def write(person: Person): BSONDocument =
			BSONDocument(
				"_id" -> person._id,
				"_creationDate" -> person._creationDate.map(date => BSONDateTime(date.getMillis)),
				"_updateDate" -> person._updateDate.map(date => BSONDateTime(date.getMillis)),
				"name" -> person.name,
				"dob" -> person.dob.map(date => BSONDateTime(date.getMillis)),
				"age" -> person.age,
				"salary" -> person.salary,
				"admin" -> person.admin
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
	
	// Insert new document using non blocking 
	def insert(p_doc:Person)= {
	  val future = collection.insert(p_doc.copy(_creationDate = Some(new DateTime()), _updateDate = Some(new DateTime())))
	  
	  future.onComplete {
	  	case Failure(e) => throw e
	  	case Success(lastError) => {
	  		println("successfully inserted document with lastError = " + lastError)
	  	}
	  }
	}
	
	// Update document using blocking
	def update(p_query:BSONDocument,p_modifier:Person) = {
	  collection.update(p_query, p_modifier.copy(_updateDate = Some(new DateTime())))
	}
	
	// Optional - Soft deletion by setting deletion flag in document
	def remove(p_query:BSONDocument) = {}
	
	// Delete document using blocking
	def removePermanently(p_query:BSONDocument) = {
	  collection.remove(p_query)
	}
	
	// Find all documents using blocking
	def find(p_query:BSONDocument) = {
	  collection.find(p_query).cursor[Person].collect[List]()	  
	}
	
	// Find one document using blocking
	// Return the first found document
	def findOne(p_query:BSONDocument) = {
	  collection.find(p_query).one[Person]
	}
	
	// Optional - Find all document with filter
	def find(p_query:BSONDocument,p_filter:BSONDocument) = {}
	
	// Optional - Find all document with filter and sorting
	def find(p_query:BSONDocument,p_filter:BSONDocument,p_sort:BSONDocument) = {}
	
}