package daos

import models.{AcademicBackground, AcademicBackgrounds}
import play.api.Play
import play.api.Play.current
import slick.lifted.TableQuery

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future, ExecutionContext}

import slick.driver.H2Driver.api._

/**
 * Created by franco on 1/9/2015.
 */
class AcademicBackgroundDAO (implicit ec: ExecutionContext){

  private val db = Database.forURL(Play.application.configuration.getString("db.default.url").get,"sa","")
  private val academicBackgrounds = TableQuery[AcademicBackgrounds]

  def insert(title:String,academicInstitutionId:Long ,linkedinOwnerId:Long, interval:String,description:String) : Future[AcademicBackground] = db.run{
    (academicBackgrounds.map(p => (p.title, p.academicInstitutionId,p.linkedinOwnerId,p.interval, p.description))
      returning academicBackgrounds.map(_.id)
      into ((vars,id) => AcademicBackground(id,vars._1,vars._2,vars._3,vars._4,vars._5))
      ) += (title,academicInstitutionId,linkedinOwnerId,interval,description)
  }

  def selectBackground(academicInstitutionId:Long ,linkedinOwnerId:Long,title : String) : Future[Option[AcademicBackground]] = db.run(
    academicBackgrounds.filter(_.title === title).filter(_.academicInstitutionId === academicInstitutionId).filter(_.linkedinOwnerId === linkedinOwnerId).result.map(_.headOption)
  )

  def insertIfNotExists(title:String,academicInstitutionId:Long ,linkedinOwnerId:Long, interval:String,description:String) : Future[AcademicBackground] ={
    val exists = Await.result(selectBackground(academicInstitutionId,linkedinOwnerId,title),Duration.Inf)
    if(exists == None) insert(title,academicInstitutionId,linkedinOwnerId,interval,description)
    else Future(exists.get)
  }

  def getAllRows : Future[Seq[AcademicBackground]] = db.run(academicBackgrounds.drop(0).result)

  def emptyTable = db.run(academicBackgrounds.filter(_.id in academicBackgrounds.sortBy(_.id.asc).map(_.id)).delete)

}
