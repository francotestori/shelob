package utils

import java.io.File

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

  private val db = Database.forURL("jdbc:h2:file:~/projects/uploader/db/db","sa","")

  def judgement_day = {
    Await.result(db.run(sqlu"SET REFERENTIAL_INTEGRITY FALSE;"), Duration.Inf)
    Await.result(db.run(sqlu"TRUNCATE TABLE ACADEMIC_BACKGROUND;"), Duration.Inf)
    Await.result(db.run(sqlu"TRUNCATE TABLE BUSINESS_BACKGROUND;"), Duration.Inf)
    Await.result(db.run(sqlu"TRUNCATE TABLE ACADEMIC_INSTITUTION;"), Duration.Inf)
    Await.result(db.run(sqlu"TRUNCATE TABLE BUSINESS_INSTITUTION;"), Duration.Inf)
    Await.result(db.run(sqlu"TRUNCATE TABLE LINKEDIN_OWNER;"), Duration.Inf)
    Await.result(db.run(sqlu"SET REFERENTIAL_INTEGRITY TRUE;"), Duration.Inf)
  }

  def restartIdentities = {
    Await.result(db.run(sqlu"ALTER TABLE ALTER ACADEMIC_BACKGROUND COLUMN id RESTART WITH 1;"), Duration.Inf)
    Await.result(db.run(sqlu"ALTER TABLE ALTER ACADEMIC_BACKGROUND COLUMN id RESTART WITH 1;"), Duration.Inf)
    Await.result(db.run(sqlu"ALTER TABLE ALTER ACADEMIC_INSTITUTION COLUMN id RESTART WITH 1;"), Duration.Inf)
    Await.result(db.run(sqlu"ALTER TABLE ALTER BUSINESS_INSTITUTION COLUMN id RESTART WITH 1;"), Duration.Inf)
    Await.result(db.run(sqlu"ALTER TABLE ALTER LINKEDIN_OWNER COLUMN id RESTART WITH 1;"), Duration.Inf)

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
