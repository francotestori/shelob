package models

import slick.driver.H2Driver.api._
import slick.lifted.ProvenShape

/**
 * Created by franco on 22/05/2015.
 */
case class AcademicInstitution(id:Long, name:String, description:String, webSite:String)

class AcademicInstitutions(tag:Tag) extends Table[AcademicInstitution]("ACADEMIC_INSTITUTION"){
  override def * : ProvenShape[AcademicInstitution] = ???
}



