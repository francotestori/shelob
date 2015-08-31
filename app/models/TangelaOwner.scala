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


  case class TangelaOwner(id: Option[Long], name: String, bio: String, role: String,linkedin_url: String)


  class TangelaOwners(tag:Tag) extends Table[TangelaOwner](tag,"TANGELA_OWNER"){

    def id = column[Option[Long]]("ID", O.PrimaryKey,O.AutoInc)
//    def tangela_id = column[Long]("TANGELA_ID")
    def name = column[String]("NAME")
    def bio = column[String]("BIO")
    def role = column[String]("ROLE")
//    def follower_count = column[Long]("FOLLOWER_COUNT")
//    def angelList_url = column[String]("ANGELLIST_URL")
//    def image = column[String]("IMAGE")
//    def blog_url = column[String]("BLOG_URL")
//    def bio_url = column[String]("BIO_URL")
//    def twitter_url = column[String]("TWITTER_URL")
//    def facebook_url = column[String]("FACEBOOK_URL")
    def linkedin_url = column[String]("LINKEDIN_URL")
//    def what_ive_built = column[String]("WHAT_IVE_BUILT")
//    def what_i_do = column[String]("WHAT_I_DO")
//    def investor = column[Boolean]("INVESTOR")

//    override def * : ProvenShape[TangelaOwner] = (id, tangela_id, name, bio, role, follower_count, angelList_url,
//      image, blog_url, bio_url, twitter_url, facebook_url, linkedin_url, what_ive_built,
//      what_i_do, investor) <> (TangelaOwner.tupled, TangelaOwner.unapply)
override def * : ProvenShape[TangelaOwner] = (id, name, bio, role, linkedin_url) <> (TangelaOwner.tupled, TangelaOwner.unapply)
}













