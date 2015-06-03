package models

import slick.driver.H2Driver.api._
import slick.lifted.ProvenShape

/**
 * Created by franco on 22/05/2015.
 */
case class AcademicInstitution(id:Long, name:String, description:String, webSite:String)

class AcademicInstitutions(tag:Tag) extends Table[AcademicInstitution](tag,"ACADEMIC_INSTITUTION"){
  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)

  def name = column[String]("NAME")

  def description = column[String]("DESCRIPTION")

  def website = column[String]("WEBSITE")

  override def * : ProvenShape[AcademicInstitution] = (id, name, description, website) <> (AcademicInstitution.tupled, AcademicInstitution.unapply _)
}



