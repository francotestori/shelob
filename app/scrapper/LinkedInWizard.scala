package scrapper

import java.io.IOException
import java.net.{SocketTimeoutException, UnknownHostException}
import java.util.regex.Pattern

import daos._
import models.{AcademicInstitution, BusinessInstitution, LinkedInOwner}
import org.jsoup.select.Elements
import org.jsoup.{Connection, HttpStatusException, Jsoup}
import org.jsoup.nodes.{Element, Document}
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
  private var connectionCounter = 0
  private var errorsList : List[(String,String)]  = List()

  private var cookies : java.util.Map[String,String] = null
  private val ID : String = "shelob.nazgul@gmail.com"
  private val PASS : String = "Shelob6719"

  /**DAO variables*/
  private val ownerDAO : LinkedInOwnerDAO = new LinkedInOwnerDAO()
  private val institutionDAO : BusinessInstitutionDAO = new BusinessInstitutionDAO()
  private val bBackgroundDAO : BusinessBackgroundDAO = new BusinessBackgroundDAO()
  private val academyDAO : AcademicInstitutionDAO = new AcademicInstitutionDAO()
  private val aBackgroundDAO : AcademicBackgroundDAO = new AcademicBackgroundDAO()

  /**Constantes de estado {OK - CONNECTION ERROR - VALIDATION ERROR - URL ERROR}*/
  private val ok =  Await.result(new OwnerStateDAO().selectByDescription("Scrap OK"), Duration.Inf).get
  private val connectionError =  Await.result(new OwnerStateDAO().selectByDescription("Scrap CONNECTION ERROR"),Duration.Inf).get
  private val validationError =  Await.result(new OwnerStateDAO().selectByDescription("Scrap VALIDATION ERROR"),Duration.Inf).get
  private val urlError =  Await.result(new OwnerStateDAO().selectByDescription("Scrap URL ERROR"),Duration.Inf).get

  /** Metodo que se encarga de ejecutar el scrap de la Lista(url,tangelaId) */
  def run(urls : List[(String,String)], searched : Boolean) = {

    if(urls.nonEmpty) {

      urls.foreach { e =>

        if (LinkedInValidator.validateUrl(e._1)) {

          var owner : Long = 0
          if(connect(e._1, e._2, searched)){
            owner = insertOwner(e._2, searched, ok.id.get).id.get
            insertBusinessInfo(owner)
            insertAcademicInfo(owner)
            owner = 0
          }

        }

      }

    }
  }

  def generateCookie = {
    val loginUrl = "https://www.linkedin.com/uas/login?goback=&trk=hb_signin"

    try {

      var response = Jsoup.connect(loginUrl).method(Connection.Method.GET).execute()

      val responseDocument : Document = response.parse()
      val loginCsrfParam : Element = responseDocument.select("input[name=loginCsrfParam]").first()

      response = Jsoup.connect("https://www.linkedin.com/uas/login-submit")
        .cookies(response.cookies())
        .data("loginCsrfParam", loginCsrfParam.attr("value"))
        .data("session_key", ID)
        .data("session_password", PASS)
        .method(Connection.Method.POST)
        .followRedirects(true)
        .execute()

      val document : Document = response.parse()


      println("Welcome " + document.select(".act-set-name-split-link").html())
      cookies = response.cookies()

    } catch {
      case io : IOException => io.printStackTrace()
    }
  }

  private def reRunErrors(errors : List[(String,String)]) = run(errors,false)

  private def connect(url : String, id : String,searched : Boolean) : Boolean = {
    this.url = url
    try{
      if(cookies != null)
        document = Jsoup.connect(url).cookies(cookies).timeout(10*1000).get
      else
        document = Jsoup.connect(url).timeout(10*1000).get
      true
    }
    catch {
      case st : SocketTimeoutException => connect(url,id,searched)
      case se : HttpStatusException => {
        connectionCounter = connectionCounter + 1
        if(connectionCounter >= 3){
          connectionCounter = 0
          errorsList = (url,id) :: errorsList
          document = null
          insertOwner(id,searched,urlError.id.get)
          false
        }
        else{
          connect(url,id,searched)
        }

      }
      //TODO UnknownHostException se puede deber a problemas de conexion o a que el server del url es inexistente. Ver de desambiguar casos
      case uh : UnknownHostException =>{
        connectionCounter = connectionCounter + 1
        if(connectionCounter > 2){
          connectionCounter = 0
          errorsList = (url,id) :: errorsList
          document = null
          insertOwner(id,searched,connectionError.id.get)
          false
        }
        else{
          connect(url,id,searched)
        }
      }
    }
  }


  /**LinkedInOwner methods*/
  private def insertOwner(tangelaId : String, searched : Boolean, state : Long): LinkedInOwner = {
      Await.result(ownerDAO.insertIfNotExists(getOwnerName, getOwnerLocation, getOwnerIndustry, url,tangelaId, searched,state), Duration.Inf)
  }

  //".full-name"
  private def getOwnerName : String = {
    if(document != null){
      try
        document.getElementById("name").text()
      catch{
        case iob: IndexOutOfBoundsException => getOwnerName2
        case npe: NullPointerException => ""
      }
    }
    else ""
  }

  private def getOwnerName2 : String = {
    if(document != null){
      try
        document.select("#name").get(0).text()
      catch {
        case iob: IndexOutOfBoundsException => ""
      }
    }
    else ""
  }

  private def getOwnerLocation : String = {
    if(document != null){
      try
        document.select(".locality").get(0).text()
      catch {
        case iob: IndexOutOfBoundsException => ""
        case npe: NullPointerException => ""
      }
    }
    else ""
  }

  private def getOwnerIndustry : String = {
    if(document != null){
      try
        document.getElementsByClass("descriptor").get(1).text()
      catch {
        case iob: IndexOutOfBoundsException => ""
        case npe: NullPointerException => ""
      }
    }
    else ""
  }

  /**Business Institution&Background methods*/
  private def insertBusinessInfo(owner : Long) = {
    val businessInfo : Elements = getExperiences

    if(businessInfo != null)
      for(i <- 0 to businessInfo.size() - 1 by 1){
        processBusiness(businessInfo.get(i),owner)
      }
  }

  //document.getElementsByAttributeValueMatching("id",Pattern.compile("experience-[0-9]+[0-9]*-view"))
  private def getExperiences : Elements = {
    try{
      document.getElementsByAttributeValueMatching("id",Pattern.compile("experience-[0-9]+[0-9]*-view"))
//      document.getElementsByAttributeValueMatching("class",Pattern.compile("position")).first()
    }
    catch {
      case iob: IndexOutOfBoundsException => null
      case npe: NullPointerException => null
    }
}

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

  /**Academic Institution&Background methods*/
  private def insertAcademicInfo(owner : Long) = {
    val academicInfo : Elements = getAcademics

    if(academicInfo != null)
      for(i <- 0 to academicInfo.size() - 1 by 1){
        processAcademics(academicInfo.get(i),owner)
      }

  }

  //document.getElementsByAttributeValueMatching("id",Pattern.compile("background-education")).first().getElementsByClass("education")
  private def getAcademics : Elements = {
    try{
        document.getElementsByAttributeValueMatching("id",Pattern.compile("background-education")).first().getElementsByClass("education")
//        document.getElementsByAttributeValueMatching("class",Pattern.compile("school")).first()
    }
    catch {
      case iob: IndexOutOfBoundsException => null
      case npe: NullPointerException => null
    }
  }

  private def processAcademics(element : Element, owner : Long)={
    val background = AcademicBackgroundStrategy.apply(element)

    if(LinkedInValidator.validateAcademic(background._1,background._2,background._3)){
      val institution = insertAcademicInstitution(background._1)
      insertAcademicBackground(background._2,institution.id.get,owner,background._3,background._4)
    }
  }

  private def insertAcademicInstitution(name : String) : AcademicInstitution = {
    Await.result(academyDAO.insertIfNotExists(name, ""),Duration.Inf)
  }

  private def insertAcademicBackground(title : String, academy : Long, owner : Long, interval : String, description : String) ={
    aBackgroundDAO.insertIfNotExists(title, academy, owner, interval, description)
  }

  def getOwnerTable = Await.result(ownerDAO.getAllRows, Duration.Inf)
  def getInstitutionTable = Await.result(institutionDAO.getAllRows, Duration.Inf)
  def getBBTable = Await.result(bBackgroundDAO.getAllRows, Duration.Inf)
  def getAcademyTable = Await.result(academyDAO.getAllRows, Duration.Inf)
  def getABTable = Await.result(aBackgroundDAO.getAllRows, Duration.Inf)

  def getErrors = {
    val errors = errorsList
    errorsList = List()
    errors
  }

}

object LinkedInWizard {

  val wizard  = new LinkedInWizard()
  var size : Long = 0

  def run(urls : List[(String,String)], searched : Boolean) = {
    size = urls.size
    wizard.run(urls,searched)
    val errors = wizard.getErrors
    wizard.reRunErrors(errors)
  }

  def generateCookies = wizard.generateCookie

  def getSize = size

  def getOwnerTable = wizard.getOwnerTable
  def getInstitutionTable = wizard.getInstitutionTable
  def getBBTable = wizard.getBBTable
  def getAcademyTable = wizard.getAcademyTable
  def getABTable = wizard.getABTable
}
