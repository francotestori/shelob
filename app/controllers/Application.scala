package controllers

import com.gingersoftware.csv.ObjectCSV
import models._
import models.TangelaOwner
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


      val setupAction: DBIO[Unit] = DBIO.seq(
        // Create the schema by combining the DDLs for the Suppliers and Coffees
        // tables using the query interfaces
        b_institutions.schema.create,
        a_institutions.schema.create,
        a_backgrounds.schema.create,
        b_backgrounds.schema.create,
        l_owners.schema.create,
        t_owners.schema.create
      )

//      val reader = new CSVReader
//      new java.io.FileReader("C:\\users-argentina.csv")
//      reader.foreach(fields => println(fields))
//      val iterator = reader.iterator
//      while(iterator.hasNext){
//        val owner = iterator.next()
//      }
//      val tangela_owners = ObjectCSV.readCSV[TangelaOwner]("C:\\users-argentina.csv")
//      for(owner : TangelaOwner <- tangela_owners){
////        t_owners += (-99,owner.tangela_id,owner.name,owner.bio,owner.role, owner.follower_count,owner.angelList_url,owner.image,
////                    owner.blog_url,owner.bio_url, owner.twitter_url,owner.facebook_url,owner.linkedin_url,owner.what_ive_built,
////                    owner.what_i_do,owner.investor)
//        t_owners += owner
//      }

      val setupFuture: Future[Unit] = db.run(setupAction)

      Await.result(setupFuture, Duration.Inf)
    }catch {
      case jdbc:JdbcSQLException =>
    }finally db.close
    Ok(views.html.index("Your new application is ready."))
  }
}