package models

import java.util.Date
import slick.driver.H2Driver.api._
import slick.lifted.ProvenShape

/**
 * Created by franco on 22/05/2015.
 */
case class BusinessBackground(id:Long,role:String,businessInstitution_id:Long,startDate:String,
                              finishDate:String,description:String)
class BusinessBackgrounds(tag:Tag) extends Table[BusinessBackground](tag,"BUSINESS_BACKGROUND"){

  def id = column[Long]("ID",O.PrimaryKey,O.AutoInc)
  def role = column[String]("ROLE")
  def businessInstitution_id = column[Long]("BUSINESS_INSTITUTION_ID")
  def startdate = column[String]("START_DATE")
  def finishdate = column[String]("FINISH_DATE")
  def description = column[String]("DESCRIPTION")

  override def * : ProvenShape[BusinessBackground] = (id, role, businessInstitution_id,startdate,finishdate,description) <> (BusinessBackground.tupled,BusinessBackground.unapply)

}
