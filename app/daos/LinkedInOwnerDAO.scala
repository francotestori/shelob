package daos

import models.{LinkedInOwner, LinkedInOwners}
import slick.lifted.TableQuery

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future, ExecutionContext}
import slick.driver.H2Driver.api._

/**
 * Created by franco on 1/9/2015.
 */
class LinkedInOwnerDAO (implicit ec: ExecutionContext){

  private val db = Database.forURL("jdbc:h2:file:~/projects/shelob/db/db","sa","")
  private val linkedInOwners = TableQuery[LinkedInOwners]

  def insert(name:String,location:String,industry:String,website:String) : Future[LinkedInOwner] = db.run{
    (linkedInOwners.map(p => (p.name, p.location,p.industry,p.website))
      returning linkedInOwners.map(_.id)
      into ((vars,id) => LinkedInOwner(id,vars._1,vars._2,vars._3,vars._4))
      ) += (name,location,industry,website)
  }

  def selectByUrl(website : String) : Future[Option[LinkedInOwner]] = db.run(
    linkedInOwners.filter(_.website === website).result.map(_.headOption)
  )

  def insertIfNotExists(name:String,location:String,industry:String,website:String) : Future[LinkedInOwner] ={
    val exists = Await.result(selectByUrl(website),Duration.Inf)
    if(exists == None) insert(name,location,industry,website)
    else Future(exists.get)
  }


}
