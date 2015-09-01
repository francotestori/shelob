package daos

import models.{AcademicInstitutions, AcademicInstitution}
import slick.lifted.TableQuery

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future, ExecutionContext}

import slick.driver.H2Driver.api._

/**
 * Created by franco on 1/9/2015.
 */
class AcademicInstitutionDAO (implicit ec: ExecutionContext){

  private val db = Database.forURL("jdbc:h2:file:~/projects/shelob/db/db","sa","")
  private val academicInstitutions = TableQuery[AcademicInstitutions]

  def insert( name:String, description:String) : Future[AcademicInstitution] = db.run{
    (academicInstitutions.map(p => (p.name, p.description))
      returning academicInstitutions.map(_.id)
      into ((vars,id) => AcademicInstitution(id,vars._1,vars._2))
      ) += (name,description)
  }

  def selectByName(name : String) : Future[Option[AcademicInstitution]] = db.run(
    academicInstitutions.filter(_.name === name).result.map(_.headOption)
  )

  def insertIfNotExists(name:String, description:String) : Future[AcademicInstitution] ={
    val exists = Await.result(selectByUrl(name),Duration.Inf)
    if(exists == None) insert(name,description)
    else Future(exists.get)
  }

}
