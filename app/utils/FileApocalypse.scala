package utils

import java.io.File

import daos._
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

  /**DAO variables*/
  private val ownerDAO : LinkedInOwnerDAO = new LinkedInOwnerDAO()
  private val institutionDAO : BusinessInstitutionDAO = new BusinessInstitutionDAO()
  private val bBackgroundDAO : BusinessBackgroundDAO = new BusinessBackgroundDAO()
  private val academyDAO : AcademicInstitutionDAO = new AcademicInstitutionDAO()
  private val aBackgroundDAO : AcademicBackgroundDAO = new AcademicBackgroundDAO()

  def judgement_day = {
    Await.result(db.run(sqlu"SET REFERENTIAL_INTEGRITY FALSE;"), Duration.Inf)
    Await.result(db.run(sqlu"TRUNCATE TABLE ACADEMIC_BACKGROUND;"), Duration.Inf)
    Await.result(db.run(sqlu"TRUNCATE TABLE BUSINESS_BACKGROUND;"), Duration.Inf)
    Await.result(db.run(sqlu"TRUNCATE TABLE LINKEDIN_OWNER;"), Duration.Inf)
    Await.result(db.run(sqlu"SET REFERENTIAL_INTEGRITY TRUE;"), Duration.Inf)
    Await.result(db.run(sqlu"TRUNCATE TABLE ACADEMIC_INSTITUTION;"), Duration.Inf)
    Await.result(db.run(sqlu"TRUNCATE TABLE BUSINESS_INSTITUTION;"), Duration.Inf)
  }

  def judgement_day2 = {
    Await.result(db.run(sqlu"SET REFERENTIAL_INTEGRITY FALSE;"), Duration.Inf)
    Await.result(aBackgroundDAO.emptyTable,Duration.Inf)
    Await.result(bBackgroundDAO.emptyTable,Duration.Inf)
    Await.result(academyDAO.emptyTable,Duration.Inf)
    Await.result(institutionDAO.emptyTable,Duration.Inf)
    Await.result(ownerDAO.emptyTable,Duration.Inf)
    Await.result(db.run(sqlu"SET REFERENTIAL_INTEGRITY TRUE;"), Duration.Inf)
  }

  def restartIdentities = {
    Await.result(db.run(sqlu"ALTER TABLE ACADEMIC_BACKGROUND ALTER  COLUMN id RESTART WITH 1;"), Duration.Inf)
    Await.result(db.run(sqlu"ALTER TABLE ACADEMIC_BACKGROUND ALTER COLUMN id RESTART WITH 1;"), Duration.Inf)
    Await.result(db.run(sqlu"ALTER TABLE ACADEMIC_INSTITUTION ALTER COLUMN id RESTART WITH 1;"), Duration.Inf)
    Await.result(db.run(sqlu"ALTER TABLE BUSINESS_INSTITUTION ALTER COLUMN id RESTART WITH 1;"), Duration.Inf)
    Await.result(db.run(sqlu"ALTER TABLE LINKEDIN_OWNER ALTER COLUMN id RESTART WITH 1;"), Duration.Inf)

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
