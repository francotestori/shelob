package utils

import java.io.File

import play.api.Play.current
import play.api.Play

import scala.concurrent.{ExecutionContext, Await}
import scala.concurrent.duration.Duration

import slick.driver.H2Driver.api._

import ExecutionContext.Implicits.global

/**
 * Object which takes care of deleting all rows from database schema. Must be called after the ZIP file has been generated
 *
 * Created by franco on 5/10/2015.
 */
object FileApocalypse {

  private val db = Database.forURL(Play.application.configuration.getString("db.default.url").get,"sa","")

  def judgement_day = {
    Await.result(db.run(sqlu"SET REFERENTIAL_INTEGRITY FALSE"), Duration.Inf)
    Await.result(db.run(sqlu"TRUNCATE TABLE ACADEMIC_BACKGROUND"), Duration.Inf)
    Await.result(db.run(sqlu"TRUNCATE TABLE BUSINESS_BACKGROUND"), Duration.Inf)
    Await.result(db.run(sqlu"TRUNCATE TABLE ACADEMIC_INSTITUTION"), Duration.Inf)
    Await.result(db.run(sqlu"TRUNCATE TABLE BUSINESS_INSTITUTION"), Duration.Inf)
    Await.result(db.run(sqlu"TRUNCATE TABLE LINKEDIN_OWNER"), Duration.Inf)
    Await.result(db.run(sqlu"SET REFERENTIAL_INTEGRITY TRUE"), Duration.Inf)
  }

  def file_anihilation(includes : Iterable[String]) = {
    includes.map(path => new File(path)).map{file =>
      try{
        file.delete()
      } catch{
        case e : Exception => e.printStackTrace()
      }
    }
  }

}
