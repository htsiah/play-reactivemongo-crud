package controllers

import scala.concurrent.Future
import org.joda.time.DateTime
import play.api.data.JodaForms._

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.libs.concurrent.Execution.Implicits._

import play.api.i18n.{MessagesApi, I18nSupport}

import models.{Person,Address,PersonModel}

import reactivemongo.api._
import reactivemongo.bson.{BSONObjectID,BSONDocument}

import javax.inject.Inject

class Application @Inject()(cc: ControllerComponents) extends AbstractController(cc)  with I18nSupport {
  
  val personForm = Form(
      mapping(
          "_id" -> ignored(BSONObjectID.generate: BSONObjectID),
          "_creationDate" -> optional(jodaDate),
          "_updateDate" -> optional(jodaDate),
          "name" -> text,
          "dob" -> optional(jodaDate("dd-mm-yyyy")),
          "age" -> optional(number),
          "salary" -> of[Double],
          "admin" -> boolean,
          "hobbies" -> text,
          "address" -> mapping(
              "address" -> text,
              "country" -> text
          )(Address.apply)(Address.unapply)
      ){(_id,_creationDate,_updateDate,name,dob,age,salary,admin,hobbies,address)=>
        Person(
            _id,
            _creationDate,
            _updateDate,
            name,
            dob,
            age,
            salary,
            admin,
            hobbies.split(",").toList,
            address
        )
      }{person:Person=>
        Some(
            person._id,
            person._creationDate,
            person._updateDate,
            person.name,
            person.dob,
            person.age,
            person.salary,
            person.admin,
            person.hobbies.mkString(","),
            person.address
        )
      }
  )

  def index = Action.async {
    val futurePerson = PersonModel.find(BSONDocument())
    futurePerson.map(persons => Ok(views.html.index(persons)))
  }
  
  def create = Action { implicit request => {
      Ok(views.html.personform(personForm))
    }
  }
  
  def insert = Action { implicit request => {   
    personForm.bindFromRequest.fold(
        formWithErrors => Ok(views.html.personform(formWithErrors)),
        personData => {
          // Copy new id before inserting
          PersonModel.insert(personData.copy(_id=BSONObjectID.generate))
          Redirect(routes.Application.index)
        }
    )
  }}
  
  def edit(id:String) = Action.async { implicit request => {
    val objectId = BSONObjectID.parse(id)
    val futurePerson = PersonModel.findOne(BSONDocument("_id" -> objectId.get))
    // Let's use for-comprehensions to compose futures 
    // Bassically for will remove Future and using map to check whether the documen found or not found
    // (see http://doc.akka.io/docs/akka/2.0.3/scala/futures.html#For_Comprehensions for more information)
    for {
       maybePerson <- futurePerson
    } yield {
      maybePerson.map( person => {
        Ok(views.html.personform(personForm.fill(person),id))
        
      }).getOrElse(NotFound)
    }
  }}

  def update(id:String) = Action { implicit request => {
    personForm.bindFromRequest.fold(
        formWithErrors => Ok(views.html.personform(formWithErrors,id)),
        personData => {
          val objectId = BSONObjectID.parse(id)
          PersonModel.update(BSONDocument("_id" -> objectId.get),personData.copy(_id=objectId.get))
          Redirect(routes.Application.index)
        }
    )
  }}

  def delete(id:String) = Action {
      val objectId = BSONObjectID.parse(id)
      PersonModel.removePermanently(BSONDocument("_id" -> objectId.get))
      Redirect(routes.Application.index)
  }
  
}