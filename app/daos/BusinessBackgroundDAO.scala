package daos

import models.{AcademicBackground, BusinessBackground, BusinessBackgrounds}
import play.api.Play
import play.api.Play.current
import slick.driver.H2Driver.api._
import slick.lifted.TableQuery

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future, ExecutionContext}

/**
 * Created by franco on 1/9/2015.
 */
class BusinessBackgroundDAO (implicit ec: ExecutionContext){

  private val db = Database.forURL(Play.application.configuration.getString("db.default.url").get,"sa","")
  private val businessBackgrounds = TableQuery[BusinessBackgrounds]

  def insert(role:String,businessInstitution_id:Long,linkedinOwnerId:Long,interval:String,description:String) : Future[BusinessBackground] = db.run{
    (businessBackgrounds.map(p => (p.role, p.businessInstitution_id,p.linkedinOwnerId,p.interval, p.description))
      returning businessBackgrounds.map(_.id)
      into ((vars,id) => BusinessBackground(id,vars._1,vars._2,vars._3,vars._4,vars._5))
      ) += (role,businessInstitution_id,linkedinOwnerId,interval,description)
  }

  def selectBackground(businessInstitution_id:Long ,linkedinOwnerId:Long,role : String) : Future[Option[BusinessBackground]] = db.run(
    businessBackgrounds.filter(_.role === role).filter(_.businessInstitution_id === businessInstitution_id).filter(_.linkedinOwnerId === linkedinOwnerId).result.map(_.headOption)
  )

  def insertIfNotExists(role:String,businessInstitution_id:Long ,linkedinOwnerId:Long, interval:String,description:String) : Future[BusinessBackground] ={
    val exists = Await.result(selectBackground(businessInstitution_id,linkedinOwnerId,role),Duration.Inf)
    if(exists == None) insert(role,businessInstitution_id,linkedinOwnerId,interval,description)
    else Future(exists.get)
  }

  def getAllRows : Future[Seq[BusinessBackground]] = db.run(businessBackgrounds.drop(0).result)

}
