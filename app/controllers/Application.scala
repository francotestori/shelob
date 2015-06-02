package controllers

import play.api.mvc.{Action, Controller}
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import slick.driver.H2Driver.api._

object Application extends Controller {

  import play.api._
//  import play.api.mvc._
  //Referencia a la conexión a la base de datos en el archigo Global.scala
  import globals.database
  //Necesario para el metodo list en (books.list)
  import slick.driver.H2Driver.api._
  import models._
  import 

  object Application extends Controller {

    def index = Action {

      database.run(slick..seq()

        //Obtenemos la lista de libros de la base de datos
        val books = (for(b <- Book) yield b).list

        //Enviamos la lista a la vista para que se muestre
        Ok(views.html.index(books))
      }
    }

  }
}