package models

import slick.driver.H2Driver.api._
import slick.lifted.ProvenShape

/**
 * Created by franco on 22/05/2015.
 */
case class LinkedInOwner(id:Long,tangela_owner_id:Long,name:String,location:String,industry:String,website:String)

class LinkedInOwners(tag:Tag) extends Table[LinkedInOwner](tag,"LINKEDIN_OWNER"){

  def id = column[Long]("ID",O.PrimaryKey,O.AutoInc)
  def tangela_owner_id = column[Long]("TANGELA_OWNER_ID")
  def name = column[String]("NAME")
  def location = column[String]("LOCATION")
  def industry = column[String]("INDUSTRY")
  def website = column[String]("WEBSITE")

  override def * : ProvenShape[LinkedInOwner] = (id, tangela_owner_id,name, location, industry, website) <> (LinkedInOwner.tupled,LinkedInOwner.unapply)
}




