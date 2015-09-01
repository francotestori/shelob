package scrapper

import java.util.regex.Pattern

import org.jsoup.Jsoup
import org.jsoup.nodes.{Element, Document}
import org.jsoup.select.Elements
/**
 * Created by franco on 1/9/2015.
 */
class LinkedInWizard(initUrl : String) {

  var url = initUrl
  private var document = Jsoup.connect(initUrl).get

  def switch(url : String)={document = Jsoup.connect(url).get}

  //LinkedInOwner methods
  def getOwnerName(document : Document) : String = document.select(".full-name").get(0).text()

  def getOwnerLocation(document : Document) : String = document.select(".locality").get(0).text()

  def getOwnerIndustry(document : Document) : String = document.select(".industry").get(0).text()

  //Business methods
  def getExperiences(document : Document) : Elements = document.getElementsByAttributeValueMatching("id",Pattern.compile("experience-[0-9]+[0-9]*"))

  //Academics methods
  def getAcademics(document : Document) : Elements = document.getElementsByAttributeValueMatching("id",Pattern.compile("education-[0-9]+[0-9]*"))



}
