package models

import java.util.Date

import slick.driver.H2Driver.api._
import slick.lifted.ProvenShape

/**
 * Created by franco on 22/05/2015.
 */
case class AcademicBackground (id:Option[Long],title:String,academicInstitutionId:Long ,linkedinOwnerId:Long, interval:String,description:String)

class AcademicBackgrounds(tag:Tag) extends Table[AcademicBackground](tag,"ACADEMIC_BACKGROUND"){

  def id = column[Option[Long]]("ID",O.PrimaryKey,O.AutoInc)
  def title = column[String]("TITLE")
  def academicInstitutionId = column[Long]("ACADEMIC_INSTITUTION_ID")
  def linkedinOwnerId = column[Long]("LINKEDIN_OWNER_ID")
  def interval = column[String]("INTERVAL")
  def description = column[String]("DESCRIPTION")

  override def * : ProvenShape[AcademicBackground] = (id, title, academicInstitutionId, linkedinOwnerId, interval, description) <> (AcademicBackground.tupled, AcademicBackground.unapply)


}
