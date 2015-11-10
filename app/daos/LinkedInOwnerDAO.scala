package daos

import models.{LinkedInOwner, LinkedInOwners}
import play.api.Play
import play.api.Play.current
import slick.lifted.TableQuery

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future, ExecutionContext}
import slick.driver.H2Driver.api._

/**
 * Created by franco on 1/9/2015.
 */
class LinkedInOwnerDAO (implicit ec: ExecutionContext){

  private val db = Database.forURL(Play.application.configuration.getString("db.default.url").get,"sa","")
  private val linkedInOwners = TableQuery[LinkedInOwners]

  def insert(name:String,location:String,industry:String,website:String,tangelaId : String, searched : Boolean, state : Long) : Future[LinkedInOwner] = db.run{
    (linkedInOwners.map(p => (p.name, p.location,p.industry,p.website, p.tangelaId, p.searched, p.state))
      returning linkedInOwners.map(_.id)
      into ((vars,id) => LinkedInOwner(id,vars._1,vars._2,vars._3,vars._4, vars._5,vars._6,vars._7))
      ) += (name,location,industry,website,tangelaId,searched,state)
  }

  def selectByUrl(website : String) : Future[Option[LinkedInOwner]] = db.run(
    linkedInOwners.filter(_.website === website).result.map(_.headOption)
  )

  def insertIfNotExists(name:String,location:String,industry:String,website:String,tangelaId : String, searched : Boolean, state : Long) : Future[LinkedInOwner] ={
    val exists = Await.result(selectByUrl(website),Duration.Inf)
    if(exists == None) insert(name,location,industry,website,tangelaId,searched,state)
    else Future(exists.get)
  }

  def getAllRows : Future[Seq[LinkedInOwner]] = db.run(linkedInOwners.drop(0).result)

}
