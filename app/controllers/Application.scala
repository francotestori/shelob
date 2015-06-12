package controllers

import java.io.{FileWriter, BufferedWriter, File}
import java.util.regex.Pattern

import models._
import org.h2.jdbc.JdbcSQLException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
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
    val url = "http://ar.linkedin.com/pub/juan-p%C3%A9rez-fern%C3%A1ndez/28/1b9/b22"
    val doc : Document = Jsoup.connect(url).get()

    //Basic Items
    val names : Elements = doc.select(".full-name")
    val actual_title : Elements = doc.select(".title")
    val location : Elements = doc.select(".locality")
    val industry : Elements = doc.select(".industry")

    //Experience elements
    val experiences : Elements = doc.getElementsByAttributeValueMatching("id",Pattern.compile("experience-"))
    //Academic elements
    val academics : Elements = doc.getElementsByAttributeValueMatching("id","education-")

    val file = new File("C:\\Users\\franco\\projects\\LinkedinProfile.txt")
    val bw = new BufferedWriter(new FileWriter(file))

    bw.write("\n" + "NAME" + "\n")
    for(i <- 0 to names.size() -1){
      val link = names.get(i)
      bw.write("\n" + link.text() + "\n")
    }

    bw.write("\n" + "TITLE" + "\n")
    for(i <- 0 to actual_title.size() -1){
      val link = actual_title.get(i)
      bw.write("\n" + link.text() + "\n")
    }

    bw.write("\n" + "LOCATION" + "\n")
    for(i <- 0 to location.size() -1){
      val link = location.get(i)
      bw.write("\n" + link.text() + "\n")
    }

    bw.write("\n" + "INDUSTRY" + "\n")
    for(i <- 0 to industry.size() -1){
      val link = industry.get(i)
      bw.write("\n" + link.text() + "\n")
    }

    bw.write("\n" + "EXPERIENCE" + "\n")
    for(i <- 0 to experiences.size() -1){
      val link = experiences.get(i)
      bw.write("\n" + link.text() + "\n")
    }

    bw.write("\n" + "ACADEMIC" + "\n")
    for(i <- 0 to academics.size() -1){
      val link = academics.get(i)
      bw.write("\n" + link.text() + "\n")
    }


    bw.close()

    // Instanciaci�n de las tablas en la base de datos a traves de Slick
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
    Ok(views.html.index("Your new application is ready."))
  }

  private def trim(s : String, width : Integer) : String = {
    if (s.length() > width)
      return s.substring(0, width-1) + ".";
    else
      return s;
  }

}