package models

import slick.driver.H2Driver.api._
import slick.lifted.ProvenShape
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
 * Created by franco on 22/05/2015.
 */
case class Owner(id:Long,name:String,location:String,industry:String,summaryEducation:AcademicInstitution,personalWebsite:String,
                 experience:List[BusinessBackground],education:List[AcademicBackground],actualInstitution:BusinessInstitution) {

class OwnerTable(tag:Tag) extends Table[Owner](tag, "OWNER"){
  override def * : ProvenShape[Owner] = ???
}

}
