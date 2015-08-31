package models

import java.util.Date
import slick.driver.H2Driver.api._
import slick.lifted.ProvenShape

/**
 * Created by franco on 22/05/2015.
 */
case class BusinessBackground(id:Option[Long],role:String,businessInstitution_id:Long,linkedinOwnerId:Long,interval:String,description:String)

class BusinessBackgrounds(tag:Tag) extends Table[BusinessBackground](tag,"BUSINESS_BACKGROUND"){

  def id = column[Option[Long]]("ID",O.PrimaryKey,O.AutoInc)
  def role = column[String]("ROLE")
  def businessInstitution_id = column[Long]("BUSINESS_INSTITUTION_ID")
  def linkedinOwnerId = column[Long]("LINKEDIN_OWNER_ID")
  def interval = column[String]("INTERVAL")
  def description = column[String]("DESCRIPTION")

  override def * : ProvenShape[BusinessBackground] = (id, role, businessInstitution_id,linkedinOwnerId,startdate,finishdate,description) <> (BusinessBackground.tupled,BusinessBackground.unapply)

}
