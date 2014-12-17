package controllers

import scala.concurrent.Future

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.libs.concurrent.Execution.Implicits._

import models.{Person,PersonModel}

import reactivemongo.api._
import reactivemongo.bson.{BSONObjectID,BSONDocument}

object Application extends Controller {
  
  val personForm = Form(
      mapping(
          "_id" ->  ignored(BSONObjectID.generate: BSONObjectID),
          "name" -> text
      ){(_id,name)=>Person(_id,name)}
      {person:Person=>Some(person._id,person.name)}
  )

  def index = Action.async {
    val futurePerson = PersonModel.find(BSONDocument())
    futurePerson.map(persons => Ok(views.html.index(persons)))
  }
  
  def create = Action {
    Ok(views.html.personform(personForm))
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
    val objectId = BSONObjectID(id)
    val futurePerson = PersonModel.findOne(BSONDocument("_id" -> objectId))
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

  def update(id:String) = Action (implicit request => {
    personForm.bindFromRequest.fold(
        formWithErrors => Ok(views.html.personform(formWithErrors,id)),
        personData => {
          val objectId = BSONObjectID(id)
          PersonModel.update(BSONDocument("_id" -> objectId),personData.copy(_id=objectId))
          Redirect(routes.Application.index)
        }
    )
  })

  def delete(id:String) = Action{
      val objectId = BSONObjectID(id)
      PersonModel.removePermanently(BSONDocument("_id" -> objectId))
      Redirect(routes.Application.index)
  }
  
}