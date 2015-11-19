package processors

import java.io.File

import com.github.tototoshi.csv.{CSVWriter, CSVReader}
import models._

/**
 * Created by franco on 2/9/2015.
 */
class CSVProcessor {

  /** Devuelve una lista con la tupla (linkedin_url,tangela_id) */
  def linkedIn_urls (file : String): List[(String,String)]= {

    val reader = CSVReader.open(new File(file))

    val inputs: List[Map[String, String]] = reader.allWithHeaders()

    (inputs.map(e => e.get("linkedin_url").get) zip inputs.map(i => i.get("id").get)).distinct.filter(! _._1.isEmpty)

  }

  def getEmpty_urlsColumn (file : String, columnName : String): List[String]= {

    val reader = CSVReader.open(new File(file))

    val inputs: List[Map[String, String]] = reader.allWithHeaders()
    inputs.filter{ input =>
      val linkedinURL = input.get("linkedin_url")
      linkedinURL.isEmpty || linkedinURL.get.equalsIgnoreCase(" ") || linkedinURL.get.equalsIgnoreCase("")
    }.map(_.get(columnName).get)
  }

  def writeLinkedInOwners (list : Seq[LinkedInOwner], fileName : String): Unit = {
    val writer = CSVWriter.open(fileName, append = true)

    writer.writeRow(List("Id", "Name", "Location", "Industry", "Website","TangelaId","Searched","State"))
    list.foreach(elem => writer.writeRow(List(elem.id.get, elem.name, elem.location, elem.industry, elem.website,elem.tangelaId,elem.searched,elem.state)))

    writer.close()
  }

  def writeBusinessInstitution (list : Seq[BusinessInstitution], fileName : String): Unit = {
    val writer = CSVWriter.open(fileName, append = true)

    writer.writeRow(List("Id", "Institute", "Description", "Sector", "Location"))
    list.foreach(elem => writer.writeRow(List(elem.id.get, elem.name, elem.description, elem.sector, elem.location)))

    writer.close()
  }

  def writeAcademicInstitution (list : Seq[AcademicInstitution], fileName : String): Unit = {
    val writer = CSVWriter.open(fileName, append = true)

    writer.writeRow(List("Id", "Institute", "Description"))
    list.foreach(elem => writer.writeRow(List(elem.id.get, elem.name, elem.description)))

    writer.close()
  }

  def writeBusinessBackground (list : Seq[BusinessBackground], fileName : String): Unit = {
    val writer = CSVWriter.open(fileName, append = true)

    writer.writeRow(List("Id", "Role", "Business_Institution", "LinkedIn_Owner", "Interval", "Description"))
    list.foreach(elem => writer.writeRow(List(elem.id.get, elem.role, elem.businessInstitution_id, elem.linkedinOwnerId, elem.interval, elem.description)))

    writer.close()
  }

  def writeAcademicBackground (list : Seq[AcademicBackground], fileName : String): Unit = {
    val writer = CSVWriter.open(fileName, append = true)

    writer.writeRow(List("Id", "Title", "Academic_Institution", "LinkedIn_Owner", "Interval", "Description"))
    list.foreach(elem => writer.writeRow(List(elem.id.get, elem.title, elem.academicInstitutionId, elem.linkedinOwnerId, elem.interval, elem.description)))

    writer.close()
  }

}

object CSVProcessor {

  val processor = new CSVProcessor()

  def process (file : String): List[(String,String)] = processor.linkedIn_urls(file)

  def getNullURLTuples (file: String): List[(String, String)] =
    (processor.getEmpty_urlsColumn(file, "id") zip processor.getEmpty_urlsColumn(file, "name")).distinct

  def getNullURLTuplesWithRoles (file: String): List[(String, String, String)] =
    (processor.getEmpty_urlsColumn(file, "id"),
      processor.getEmpty_urlsColumn(file, "startup name"),
      processor.getEmpty_urlsColumn(file, "role")).zipped.toList

  def writeLO (list: Seq[LinkedInOwner], fileName : String) = processor.writeLinkedInOwners(list, fileName)

  def writeBI (list: Seq[BusinessInstitution], fileName : String) = processor.writeBusinessInstitution(list,fileName)

  def writeBB (list: Seq[BusinessBackground], fileName : String) = processor.writeBusinessBackground(list,fileName)

  def writeAI (list: Seq[AcademicInstitution], fileName : String) = processor.writeAcademicInstitution(list,fileName)

  def writeAB (list: Seq[AcademicBackground], fileName : String) = processor.writeAcademicBackground(list,fileName)
}
