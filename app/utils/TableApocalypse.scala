package utils

import daos._

import scala.concurrent.{ExecutionContext, Await}
import scala.concurrent.duration.Duration

import ExecutionContext.Implicits.global

/**
 * Object which takes care of deleting all rows from database schema. Must be called after the ZIP file has been generated
 *
 * Created by franco on 5/10/2015.
 */
object TableApocalypse {

  /**DAO variables*/
  private val ownerDAO : LinkedInOwnerDAO = new LinkedInOwnerDAO()
  private val institutionDAO : BusinessInstitutionDAO = new BusinessInstitutionDAO()
  private val bBackgroundDAO : BusinessBackgroundDAO = new BusinessBackgroundDAO()
  private val academyDAO : AcademicInstitutionDAO = new AcademicInstitutionDAO()
  private val aBackgroundDAO : AcademicBackgroundDAO = new AcademicBackgroundDAO()

  def judgement_day = {
    Await.result(ownerDAO.emptyTable, Duration.Inf)
    Await.result(institutionDAO.emptyTable, Duration.Inf)
    Await.result(bBackgroundDAO.emptyTable, Duration.Inf)
    Await.result(academyDAO.emptyTable, Duration.Inf)
    Await.result(aBackgroundDAO.emptyTable, Duration.Inf)
  }

}
