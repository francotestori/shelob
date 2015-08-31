package models

import slick.driver.H2Driver.api._
import slick.lifted.ProvenShape

/**
 * Created by franco on 22/05/2015.
 */
case class BusinessInstitution(id:Option[Long], name:String, description:String,sector:String, location:String)

class BusinessInstitutions(tag:Tag) extends Table[BusinessInstitution](tag, "BUSINESS_INSTITUTION") {

  def id = column[Option[Long]]("ID", O.PrimaryKey, O.AutoInc)

  def name = column[String]("NAME")

  def description = column[String]("DESCRIPTION")

  def sector = column[String]("SECTOR")

  def location = column[String]("LOCATION")

  override def * : ProvenShape[BusinessInstitution] = (id, name, description, sector, location) <>(BusinessInstitution.tupled, BusinessInstitution.unapply _)
}

