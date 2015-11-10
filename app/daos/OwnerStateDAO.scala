package daos

import models.{OwnerStates, OwnerState}
import play.api.Play
import play.api.Play.current
import slick.lifted.TableQuery

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future, ExecutionContext}
import slick.driver.H2Driver.api._

/**
 * Created by franco on 4/11/2015.
 */
class OwnerStateDAO (implicit ec: ExecutionContext){

  private val db = Database.forURL(Play.application.configuration.getString("db.default.url").get,"sa","")
  private val ownerStates = TableQuery[OwnerStates]

  def insert(description : String) : Future[OwnerState] = db.run{
    (ownerStates.map(o => o.description)
      returning ownerStates.map(_.id)
      into ((vars,id) => OwnerState(id, vars))
      ) += description
  }

  def selectByDescription(state : String) : Future[Option[OwnerState]] = db.run(
    ownerStates.filter(_.description === state).result.map(_.headOption)
  )

  def insertIfNotExists(description : String) : Future[OwnerState] ={
    val exists = Await.result(selectByDescription(description),Duration.Inf)
    if(exists == None) insert(description)
    else Future(exists.get)
  }

  def getAllRows : Future[Seq[OwnerState]] = db.run(ownerStates.drop(0).result)
}
