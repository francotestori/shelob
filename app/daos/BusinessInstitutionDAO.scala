package daos

import models.{BusinessInstitutions, BusinessInstitution}
import slick.lifted.TableQuery

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future, ExecutionContext}

import slick.driver.H2Driver.api._

/**
 * Created by franco on 1/9/2015.
 */
class BusinessInstitutionDAO (implicit ec: ExecutionContext){

  private val db = Database.forURL("jdbc:h2:file:~/projects/uploader/db/db","sa","")
  private val businessInstitutions = TableQuery[BusinessInstitutions]

  def insert(name:String, description:String,sector:String, location:String) : Future[BusinessInstitution] = db.run{
    (businessInstitutions.map(p => (p.name, p.description,p.sector,p.location))
      returning businessInstitutions.map(_.id)
      into ((vars,id) => BusinessInstitution(id,vars._1,vars._2,vars._3,vars._4))
      ) += (name,description,sector,location)
  }

  def selectByName(name : String) : Future[Option[BusinessInstitution]] = db.run(
    businessInstitutions.filter(_.name === name).result.map(_.headOption)
  )

  def insertIfNotExists(name:String, description:String,sector:String, location:String) : Future[BusinessInstitution] ={
    val exists = Await.result(selectByName(name),Duration.Inf)
    if(exists == None) insert(name,description,sector,location)
    else Future(exists.get)
  }

  def getAllRows : Future[Seq[BusinessInstitution]] = db.run(businessInstitutions.drop(0).result)

  def emptyTable = db.run(businessInstitutions.filter(_.id in businessInstitutions.sortBy(_.id.asc).map(_.id)).delete)

}
