package models

import slick.driver.H2Driver.api._
import slick.lifted.ProvenShape

/**
 * Created by franco on 4/11/2015.
 */
case class OwnerState(id: Option[Long], description : String)

class OwnerStates(tag:Tag) extends Table[OwnerState](tag,"OWNER_STATE"){

  def id = column[Option[Long]]("ID",O.PrimaryKey,O.AutoInc)
  def description = column[String]("DESCRIPTION")

  override def * : ProvenShape[OwnerState] = (id,description) <> (OwnerState.tupled, OwnerState.unapply)
}
