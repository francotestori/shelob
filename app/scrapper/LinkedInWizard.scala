package scrapper

import java.util.regex.Pattern

import daos._
import models.{AcademicInstitution, BusinessInstitution, LinkedInOwner}
import org.jsoup.{Jsoup}
import org.jsoup.nodes.{Element, Document}
import org.jsoup.select.Elements
import scrapper.strategies._

import scala.concurrent.{ExecutionContext, Await}
import scala.concurrent.duration.Duration

import ExecutionContext.Implicits.global

/**
 * Created by franco on 1/9/2015.
 */
class LinkedInWizard {

  private var url : String = null
  private var document : Document = null

  /**DAO variables*/
  private val ownerDAO : LinkedInOwnerDAO = new LinkedInOwnerDAO()
  private val institutionDAO : BusinessInstitutionDAO = new BusinessInstitutionDAO()
  private val bBackgroundDAO : BusinessBackgroundDAO = new BusinessBackgroundDAO()
  private val academyDAO : AcademicInstitutionDAO = new AcademicInstitutionDAO()
  private val aBackgroundDAO : AcademicBackgroundDAO = new AcademicBackgroundDAO()


  def run(urls : List[String]) = {
    if(!urls.isEmpty) {

      for (i <- 0 to urls.length - 1) {

        if(LinkedInValidator.validateUrl(urls(i))) {

          switch(urls(i))
          val owner: Long = insertOwner.id.get
          insertBusinessInfo(owner)
          insertAcademicInfo(owner)

        }
      }

    }
  }

  private def switch(initUrl : String)={
    url = initUrl
    document = Jsoup.connect(initUrl).get}

  /**LinkedInOwner methods*/
  private def insertOwner: LinkedInOwner = {
    Await.result(ownerDAO.insertIfNotExists(getOwnerName, getOwnerLocation, getOwnerIndustry, url), Duration.Inf)
  }

  private def getOwnerName : String = document.select(".full-name").get(0).text()
  private def getOwnerLocation : String = document.select(".locality").get(0).text()
  private def getOwnerIndustry : String = document.select(".industry").get(0).text()

  /**Business Institution & Background methods*/
  private def insertBusinessInfo(owner : Long) = {
    val businessInfo : Elements = getExperiences

    for(i <- 0 to businessInfo.size() - 1 by 2){
      processBusiness(businessInfo.get(i),owner)
    }
  }

  private def getExperiences : Elements = document.getElementsByAttributeValueMatching("id",Pattern.compile("experience-[0-9]+[0-9]*"))

  private def processBusiness(element : Element, owner : Long) ={
    val background : (String,String,String,String) = BusinessBackgroundStrategy.apply(element)

    if(LinkedInValidator.validateBusiness(background._1,background._2,background._3)){
      val institution = insertBusinessInstitution(background._2)
      insertBusinessBackground(background._1,institution.id.get,owner,background._3,background._4)
    }
  }

  private def insertBusinessInstitution(name : String) : BusinessInstitution = {
    Await.result(institutionDAO.insertIfNotExists(name, "", "", ""), Duration.Inf)
  }

  private def insertBusinessBackground(role : String, business : Long, owner : Long, interval : String, info : String) ={
    bBackgroundDAO.insertIfNotExists(role, business, owner, interval, info)
  }

  /**Academic Institution & Background methods*/
  private def insertAcademicInfo(owner : Long) = {
    val academicInfo : Elements = getAcademics

    for(i <- 0 to academicInfo.size() - 1 by 2){
      processAcademics(academicInfo.get(i),owner)
    }
  }

  private def getAcademics : Elements = document.getElementsByAttributeValueMatching("id",Pattern.compile("education-[0-9]+[0-9]*"))

  private def processAcademics(element : Element, owner : Long) ={
    val background = AcademicBackgroundStrategy.apply(element)

    if(LinkedInValidator.validateAcademic(background._1,background._2,background._3)){
      val institution = insertAcademicInstitution(background._1)
      insertAcademicBackground(background._2,institution.id.get,owner,background._3)
    }
  }

  private def insertAcademicInstitution(name : String) : AcademicInstitution = {
    Await.result(academyDAO.insertIfNotExists(name, ""),Duration.Inf)
  }

  private def insertAcademicBackground(title : String, academy : Long, owner : Long, interval : String) ={
    aBackgroundDAO.insertIfNotExists(title, academy, owner, interval, "")
  }

}

object LinkedInWizard {
  def run(urls : List[String]) = {
    new LinkedInWizard().run(urls)
  }
}
