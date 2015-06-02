package models

import slick.driver.H2Driver.api._
import slick.lifted.ProvenShape

/**
 * Created by franco on 22/05/2015.
 */
case class BusinessInstitution(id:Long, name:String, description:String,
                               webSite:String,sector:String, location:String, size:String) {

class BusinessInstitutions(tag:Tag) extends Table[BusinessInstitution](tag, "BUSINESS_INSTITUTION"){

  def id = column[Long]("ID", O.PrimaryKey,O.AutoInc)
  def name = column[String]("NAME")
  def description = column[String]("DESCRIPTION")
  def website = column[String]("WEBSITE")
  def sector = column[String]("SECTOR")
  def location = column[String]("LOCATION")
  def size = column[String]("SIZE")

  override def * : ProvenShape[BusinessInstitution] = (id, name, description, website, sector, location, size) <> (BusinessInstitution.tupled,BusinessInstitution.unapply _)
  }
}
