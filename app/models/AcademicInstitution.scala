package models

import slick.driver.H2Driver.api._
import slick.lifted.ProvenShape

/**
 * Created by franco on 22/05/2015.
 */
case class AcademicInstitution(id:Option[Long], name:String, description:String)

class AcademicInstitutions(tag:Tag) extends Table[AcademicInstitution](tag,"ACADEMIC_INSTITUTION"){
  def id = column[Option[Long]]("ID", O.PrimaryKey, O.AutoInc)

  def name = column[String]("NAME")

  def description = column[String]("DESCRIPTION")

  override def * : ProvenShape[AcademicInstitution] = (id, name, description) <> (AcademicInstitution.tupled, AcademicInstitution.unapply _)
}



