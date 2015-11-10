package models

import slick.driver.H2Driver.api._
import slick.lifted.ProvenShape

/**
 * Created by franco on 22/05/2015.
 */
case class LinkedInOwner(id:Option[Long],name:String,location:String,industry:String,website:String, tangelaId : String, searched : Boolean, state : Long)

class LinkedInOwners(tag:Tag) extends Table[LinkedInOwner](tag,"LINKEDIN_OWNER"){

  def id = column[Option[Long]]("ID",O.PrimaryKey,O.AutoInc)
  def name = column[String]("NAME")
  def location = column[String]("LOCATION")
  def industry = column[String]("INDUSTRY")
  def website = column[String]("WEBSITE")
  def tangelaId = column[String]("TANGELA_ID")
  def searched = column[Boolean]("SEARCHED")
  def state = column[Long]("OWNER_STATE_ID")

  override def * : ProvenShape[LinkedInOwner] = (id,name, location, industry, website, tangelaId, searched, state) <> (LinkedInOwner.tupled,LinkedInOwner.unapply)
}



