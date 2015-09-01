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
import scrapper.strategies._

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
    val url = "https://ar.linkedin.com/pub/franco-testori/38/814/197"
//    val url = "https://ar.linkedin.com/in/horaciorodriguezlarreta"
    val doc : Document = Jsoup.connect(url).get()

    //Basic Items
    val names : Elements = doc.select(".full-name")
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
    val insertOwner =  (l_owners returning l_owners.map(_.id) into ((owner,id) => owner.copy(id=Some(id).get))) += LinkedInOwner(None,name,location,industry,url)

//    val insertOwner = LinkedInOwnerDAO().insertIfNotExists(name,location,industry,url)

    val ownerValue = db.run(insertOwner)
    Await.result(ownerValue,Duration.Inf)

    val ownerID = ownerValue.value.head.get.id.get

    //Add both Business Institution & Business Background
    for(i <- 0 to experiences.size() -1){
      val role = RoleStrategy.RoleStrategies(experiences.get(i)).first()
      val institute = InstituteStrategy.InstituteStrategies(experiences.get(i)).first().toUpperCase
      val when = IntervalStrategy.IntervalStrategies(experiences.get(i)).businessFirst()
      val desc = DescriptionStrategy.DescriptionStrategies(experiences.get(i)).first()
      if(validateBusiness(role,institute,when,desc)){

        //Add Business Institution
        val insertBusiness = (b_institutions returning b_institutions.map(_.id) into ((institution,id) => institution.copy(id=Some(id).get))) += BusinessInstitution(None,institute,"","","")
        val institutionValue = db.run(insertBusiness)
        Await.result(institutionValue,Duration.Inf)

        val businessID = institutionValue.value.head.get.id.get

        //Add Background
//        val index = when.indexOf('-')
//        val from = when.substring(0,index - 1).trim
//        val to = when.substring(index + 1, when.length).trim
        val setupBackground: DBIO[Unit] = DBIO.seq(
          b_backgrounds += BusinessBackground(None,
            role,
            businessID,
            1,
            when,
            desc)
        )
        val setupFutureBackground: Future[Unit] = db.run(setupBackground)
        Await.result(setupFutureBackground, Duration.Inf)
      }
    }

    //Add both Academic Institution & Academic Background
    for(i <- 0 to academics.size() -1){
      val academy = AcademyStrategy.AcademyStrategies(academics.get(i)).first().toUpperCase
      val title = TitleStrategy.TitleStrategies(academics.get(i)).first()
      val interval = IntervalStrategy.IntervalStrategies(academics.get(i)).academyFirst()
      if(validateAcademic(academy,title,interval)){
        //Add Academy
        val insertAcademy = (a_institutions returning a_institutions.map(_.id) into ((academicInstitute,id) => academicInstitute.copy(id=Some(id.get)))) += AcademicInstitution(None,academy,"")
        val academyValue = db.run(insertAcademy)
        Await.result(academyValue,Duration.Inf)

        val academyID = academyValue.value.head.get.id.get

        //Add Background
        val setupBackground: DBIO[Unit] = DBIO.seq(
          a_backgrounds += AcademicBackground(None,
            title,
            academyID,
            1,
            interval,
            "")
        )
        val setupFutureBackground: Future[Unit] = db.run(setupBackground)
        Await.result(setupFutureBackground, Duration.Inf)
      }
    }

    } finally db.close()

    Ok(views.html.index("Your new application is ready."))
  }

  private def validateBusiness(role : String, institute : String, when : String, desc : String): Boolean = {
    !role.isEmpty && !institute.isEmpty && !when.isEmpty && !desc.isEmpty
  }

  private def validateAcademic(academy : String, title : String, interval : String): Boolean = {
    !academy.isEmpty && !title.isEmpty && !interval.isEmpty
  }
}