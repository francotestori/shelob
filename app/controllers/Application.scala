package controllers

import models._
import org.h2.jdbc.JdbcSQLException
import play.api.mvc.{Action, Controller}
import play.api.{Application, GlobalSettings}

import scala.concurrent.{Future, Await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import slick.backend.DatabasePublisher
import slick.driver.H2Driver.api._

object Application extends Controller {
  def index = Action {

    val db = Database.forURL("jdbc:h2:file:~/projects/shelob/db/db","sa","")
    try {
      val b_institutions = TableQuery[BusinessInstitutions]
      val a_institutions = TableQuery[AcademicInstitutions]
      val a_backgrounds = TableQuery[AcademicBackgrounds]
      val b_backgrounds = TableQuery[BusinessBackgrounds]
      val l_owners = TableQuery[LinkedInOwners]
      val t_owners = TableQuery[TangelaOwners]

      val setupAction: DBIO[Unit] = DBIO.seq()

      val setupFuture: Future[Unit] = db.run(setupAction)

      Await.result(setupFuture, Duration.Inf)
    }finally db.close
    Ok(views.html.index("Your new application is ready."))
  }
}