package models

import java.util.Date

import slick.lifted.ProvenShape

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import slick.driver.H2Driver.api._



/**
 * Created by franco on 22/05/2015.
 */

import slick.driver.H2Driver.api._


  case class Owner(id: Option[Long] = None, name: String, location: String, industry: String, summaryEducation: AcademicInstitution, personalWebsite: String,
                   experience: List[BusinessBackground], education: List[AcademicBackground], actualInstitution: BusinessInstitution)


  class Owners(tag:Tag) extends Table[Owner](tag,"OWNER"){

    def id = column[Long]("ID", O.PrimaryKey,O.AutoInc)
    def name = column[String]("NAME")
    def location = column[String]("LOCATION")
    def industry = column[String]("INDUSTRY")
    def summaryEducation = column[Long]("SUMMARY_EDUCATION")
    def personalWebsite = column[String]("WEBSITE")
    def experience = column[Long]("EXPERIENCE")
    def education = column[Long]("EDUCATION")
    def actualInstitution = column[Long]("ACTUAL_INTITUTION")

    override def * : ProvenShape[Owners] = (id, name, location, industry, summaryEducation, personalWebsite, experience, education, actualInstitution) <> (Owner.tupled, Owner.unapply _)

  }













