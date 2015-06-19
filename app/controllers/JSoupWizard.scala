package controllers

import java.io.{FileWriter, BufferedWriter, File}
import java.util.regex.Pattern

import controllers.Application._
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.jsoup.nodes.Element
import play.api.mvc.{Action, Controller}

/**
 * Created by franco on 19/06/2015.
 */
object JSoupWizard extends Controller {

  def index = Action {
    val url = "http://ar.linkedin.com/in/ricardoanibalpasquini"
    val doc: Document = Jsoup.connect(url).get()

    //Basic Items
    val names: Elements = doc.select(".full-name")
    val actual_title: Elements = doc.select(".title")
    val locations: Elements = doc.select(".locality")
    val industries: Elements = doc.select(".industry")

    //Experience elements
    val experiences: Elements = doc.getElementsByAttributeValueMatching("id", Pattern.compile("experience-[0-9]+[0-9]*"))
    //Academic elements
    val academics: Elements = doc.getElementsByAttributeValueMatching("id", Pattern.compile("education-[0-9]+[0-9]*"))

    val file = new File("C:\\Users\\franco\\projects\\LinkedinProfile.txt")
    val bw = new BufferedWriter(new FileWriter(file))

    val name = names.get(0).text()
    val title = actual_title.get(0).text()
    val location = locations.get(0).text()
    val industry = industries.get(0).text()

    bw.write("\n" + name + "\n")
    bw.write("\n" + title + "\n")
    bw.write("\n" + location + "\n")
    bw.write("\n" + industry + "\n")


    for(i <- 0 to experiences.size() -1){
      val role = getRole(experiences.get(i))
      val institute = getInstitute(experiences.get(i))
      val when = getWhen(experiences.get(i))
      val desc = getDesc(experiences.get(i))
      if(validateBusiness(role,institute,when,desc)){
        bw.write("\n" + role + "\n")
        bw.write("\n" + institute + "\n")
        bw.write("\n"+ when + "\n")
        bw.write("\n" + desc + "\n")
      }
    }

    for(i <- 0 to academics.size() -1){
      val academy = getAcademy(academics.get(i))
      val title = getTitle(academics.get(i))
      val interval = getInterval(academics.get(i))
      if(validateAcademic(academy,title,interval)){
        bw.write("\n" + academy + "\n")
        bw.write("\n" + title + "\n")
        bw.write("\n"+ interval + "\n")
      }
    }


    bw.close()

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
}
