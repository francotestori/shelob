package models

import java.util.Date

import slick.lifted.ProvenShape

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import slick.driver.H2Driver.api._



/**
 * Created by franco on 22/05/2015.
 */

import slick.driver.H2Driver.api._


  case class TangelaOwner(id: Long,tangela_id:Long, name: String, bio: String, role: String, followerCount:Long, angelList_url: String,
                   image: String, blog_url: String, bio_url: String,twitter_url: String, facebook_url: String, linkedin_url: String,investor: Boolean)


  class TangelaOwners(tag:Tag) extends Table[TangelaOwner](tag,"TANGELA_OWNER"){

    def id = column[Long]("ID", O.PrimaryKey,O.AutoInc)
    def tangela_id = column[Long]("TANGELA_ID")
    def name = column[String]("NAME")
    def bio = column[String]("LOCATION")
    def role = column[String]("INDUSTRY")
    def followerCount = column[Long]("SUMMARY_EDUCATION")
    def angelList_url = column[String]("WEBSITE")
    def image = column[String]("EXPERIENCE")
    def blog_url = column[String]("EDUCATION")
    def bio_url = column[String]("ACTUAL_INTITUTION")
    def twitter_url = column[String]("TWITTER_URL")
    def facebook_url = column[String]("FACEBOOK_URL")
    def linkedin_url = column[String]("LINKEDIN_URL")
    def investor = column[Boolean]("INVESTOR")

    override def * : ProvenShape[TangelaOwner] = (id, tangela_id, name, bio, role, followerCount, angelList_url, image, blog_url, bio_url, twitter_url, facebook_url, linkedin_url, investor) <> (TangelaOwner.tupled, TangelaOwner.unapply)


}













