package controllers

import java.io.{FileWriter, BufferedWriter, File}
import java.util.regex.Pattern

import models._
import org.h2.jdbc.JdbcSQLException
import org.jsoup.Jsoup
import org.jsoup.nodes.{Element, Document}
import org.jsoup.select.Elements
import play.api.mvc.{Action, Controller}
import play.api.{Application, GlobalSettings}

import scala.concurrent.{Future, Await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import slick.backend.DatabasePublisher
import slick.driver.H2Driver.api._

import scala.util.{Failure, Success}

object Application extends Controller {
  def index = Action {
    // Instanciacion de las tablas en la base de datos a traves de Slick
    val db = Database.forURL("jdbc:h2:file:~/projects/shelob/db/db","sa","")

    try{
    //LinkedInOwner
    val l_owners = TableQuery[LinkedInOwners]
    //Business Institution & Background
    val b_institutions = TableQuery[BusinessInstitutions]
    val b_backgrounds = TableQuery[BusinessBackgrounds]
    //Academic Institution & Background
    val a_institutions = TableQuery[AcademicInstitutions]
    val a_backgrounds = TableQuery[AcademicBackgrounds]

    //url & url_document
//    val url = "http://ar.linkedin.com/in/ricardoanibalpasquini"
//    val url = "https://ar.linkedin.com/in/arielDarioPerez/es"
//    val url = "https://ar.linkedin.com/in/luisrgarcia"
//    val url = "https://ar.linkedin.com/pub/franco-testori/38/814/197"
    val url = "https://ar.linkedin.com/in/horaciorodriguezlarreta"
    val doc : Document = Jsoup.connect(url).get()

    //Basic Items
    val names : Elements = doc.select(".full-name")
    val actual_title : Elements = doc.select(".title")
    val locations : Elements = doc.select(".locality")
    val industries : Elements = doc.select(".industry")
    //Experience Items
    val experiences : Elements = doc.getElementsByAttributeValueMatching("id",Pattern.compile("experience-[0-9]+[0-9]*"))
    //Academic Items
    val academics : Elements = doc.getElementsByAttributeValueMatching("id",Pattern.compile("education-[0-9]+[0-9]*"))

//    val title = actual_title.get(0).text()
    val location = locations.get(0).text()
    val name = names.get(0).text()
    val industry = industries.get(0).text()

    //Add LinkedIn Owner
    val setupAction: DBIO[Unit] = DBIO.seq(
      l_owners += LinkedInOwner(1,name,location,industry,url)
    )
    val setupFuture: Future[Unit] = db.run(setupAction)
    Await.result(setupFuture, Duration.Inf)

    //val f : Future[LinkedInOwner] = setupFuture.flatMap { _ =>
    //  val filterQuery: Query[LinkedInOwners, LinkedInOwners#TableElementType, Seq] =
    //    l_owners.filter(_.website === url)

      // Print the SQL for the filter query
    //  println("Generated SQL for filter query:\n" + filterQuery.result.statements)

      // Execute the query and print the Seq of results
      //  db.run(filterQuery.result.map(println))
    //}
    //Await.result(f, Duration.Inf)

    //Add both Business Institution & Business Background
    for(i <- 0 to experiences.size() -1){
      val role = getRole(experiences.get(i))
      val institute = getInstitute(experiences.get(i))
      val when = getWhen(experiences.get(i))
      val desc = getDesc(experiences.get(i))
      if(validateBusiness(role,institute,when,desc)){

        //Add Business
        val setupBusiness: DBIO[Unit] = DBIO.seq(
          b_institutions += BusinessInstitution(1,institute,"","","","","")
        )
        val setupFutureBusiness: Future[Unit] = db.run(setupBusiness)
        Await.result(setupFutureBusiness, Duration.Inf)

        //Add Background
//        val index = when.indexOf('-')
//        val from = when.substring(0,index - 1).trim
//        val to = when.substring(index + 1, when.length).trim
        val setupBackground: DBIO[Unit] = DBIO.seq(
          b_backgrounds += BusinessBackground(1,role,1,1,when,"",desc)
        )
        val setupFutureBackground: Future[Unit] = db.run(setupBackground)
        Await.result(setupFutureBackground, Duration.Inf)
      }
    }

    //Add both Academic Institution & Academic Background
    for(i <- 0 to academics.size() -1){
      val academy = getAcademy(academics.get(i))
      val title = getTitle(academics.get(i))
      val interval = getInterval(academics.get(i))
      if(validateAcademic(academy,title,interval)){
        //Add Academy
        val setupAcademy: DBIO[Unit] = DBIO.seq(
          a_institutions += AcademicInstitution(1,academy,"","")
        )
        val setupFutureAcademy: Future[Unit] = db.run(setupAcademy)
        Await.result(setupFutureAcademy, Duration.Inf)

        //Add Background
//        val index = interval.indexOf('-')
//        val from = interval.substring(0,index - 1).trim
//        val to = interval.substring(index + 1, interval.length).trim
        val setupBackground: DBIO[Unit] = DBIO.seq(
          a_backgrounds += AcademicBackground(1,title,1,1,interval,"","")
        )
        val setupFutureBackground: Future[Unit] = db.run(setupBackground)
        Await.result(setupFutureBackground, Duration.Inf)
      }
    }

    } finally db.close()

    Ok(views.html.index("Your new application is ready."))
  }

  //Get elements for business background
  private def getRole(element : Element) : String = {
    try{
      if(element.children().get(0).children().get(0).children().get(0).text().equals("")){
        return element.children().get(0).children().get(0).children().get(1).text()
      }
      element.children().get(0).children().get(0).children().get(0).text()
    }catch{
      case iob : IndexOutOfBoundsException => ""
    }

  }

  private def getInstitute(element : Element) : String = {
    try{
      if(element.children().get(0).children().get(0).children().get(0).text().equals("")){
      return element.children().get(0).children().get(0).children().get(2).text()
      }
      element.children().get(0).children().get(0).children().get(1).text()
    } catch{
      case iob : IndexOutOfBoundsException => ""
    }
  }

  private def getWhen(element : Element) : String = {
    try{
      element.children().get(0).children().get(1).text()
    }catch{
      case iob : IndexOutOfBoundsException => ""
    }
  }

  private def getDesc(element : Element) : String = {
    try{
      element.children().get(0).children().get(2).text()
    }catch{
      case iob : IndexOutOfBoundsException => ""
    }
  }

  private def validateBusiness(role : String, institute : String, when : String, desc : String): Boolean = {
    !role.isEmpty && !institute.isEmpty && !when.isEmpty && !desc.isEmpty
  }

  //Get elements for academic background
  private def getAcademy(element : Element) : String = {
    try{
      if(element.children().get(0).children().get(0).text().equals("")){
        return element.children().get(0).children().get(1).children().get(0).text()
      }

      if(element.children().get(0).children().get(0).children().get(0).text().equals("")){
        return element.children().get(0).children().get(0).children().get(1).children().get(0).text()
      }
      element.children().get(0).children().get(0).children().get(0).children().get(0).text()
    }catch{
      case iob : IndexOutOfBoundsException => ""
    }
  }

  private def getTitle(element : Element) : String = {
    try{
      if(element.children().get(0).children().get(0).text().equals("")){
        return element.children().get(0).children().get(1).children().get(1).text()
      }

      if(element.children().get(0).children().get(0).children().get(0).text().equals("")){
        return element.children().get(0).children().get(0).children().get(1).children().get(1).text()
      }
      element.children().get(0).children().get(0).children().get(0).children().get(1).text()
    }catch{
      case iob : IndexOutOfBoundsException => ""
    }
  }

  private def getInterval(element : Element) : String = {
    try{
      if(element.children().get(0).children().get(0).text().equals("")){
        return element.children().get(0).children().get(2).text()
      }

      if(element.children().get(0).children().get(0).children().get(0).text().equals("")){
        return element.children().get(0).children().get(0).children().get(2).text()
      }
      element.children().get(0).children().get(0).children().get(1).text()
    }catch{
      case iob : IndexOutOfBoundsException => ""
    }
  }

  private def validateAcademic(academy : String, title : String, interval : String): Boolean = {
    !academy.isEmpty && !title.isEmpty && !interval.isEmpty
  }

  //    // Instanciaci�n de las tablas en la base de datos a traves de Slick
  //    val db = Database.forURL("jdbc:h2:file:~/projects/shelob/db/db","sa","")
  //    try {
  //      val b_institutions = TableQuery[BusinessInstitutions]
  //      val a_institutions = TableQuery[AcademicInstitutions]
  //      val t_owners = TableQuery[TangelaOwners]
  //      val l_owners = TableQuery[LinkedInOwners]
  //      val a_backgrounds = TableQuery[AcademicBackgrounds]
  //      val b_backgrounds = TableQuery[BusinessBackgrounds]
  //
  //      val setupAction: DBIO[Unit] = DBIO.seq()
  //
  //      val setupFuture: Future[Unit] = db.run(setupAction)
  //
  //      Await.result(setupFuture, Duration.Inf)
  //    }finally db.close
}