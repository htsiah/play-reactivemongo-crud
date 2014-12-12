package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models._

import reactivemongo.bson._
import reactivemongo.bson.BSONObjectID

object Application extends Controller {
  
  
  val personForm = Form(
      mapping(
          "_id" -> ignored(BSONObjectID.generate: BSONObjectID),
          "name" -> text
      )(Person.apply)(Person.unapply)
  )

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }
  
  def insert = Action {
    PersonModel. insert(Person(BSONObjectID.generate, "Simon rocks"))
    Ok(views.html.index("Your new application is ready."))
  }

}