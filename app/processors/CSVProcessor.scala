package processors

import java.io.File

import com.github.tototoshi.csv.{CSVWriter, CSVReader}
import models._

/**
 * Created by franco on 2/9/2015.
 */
class CSVProcessor {


  def linkedIn_urls (file : String): List[String]= {

    val reader = CSVReader.open(new File(file))

    val inputs: List[Map[String, String]] = reader.allWithHeaders()

    inputs.map(e => e.get("linkedin_url")).distinct.map(i => i.get)

  }

  def empty_urls (file : String): List[String]= {

    val reader = CSVReader.open(new File(file))

    val inputs: List[Map[String, String]] = reader.allWithHeaders()

    //Gets entries with no linkedin_url TODO get names from list
    inputs.map{ e =>
    }
    inputs.map(e => e.get("linkedin_url")).filter(i => i.get.equals(""))

    null
  }

  def writeLinkedInOwners (list : Seq[LinkedInOwner], fileName : String): Unit = {
    val writer = CSVWriter.open(fileName, append = true)

    writer.writeRow(List("Id", "Name", "Location", "Industry", "Website"))
    list.foreach(elem => writer.writeRow(List(elem.id.get, elem.name, elem.location, elem.industry, elem.website)))

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

  def process (file : String): List[String] = processor.linkedIn_urls(file)

  def writeLO (list: Seq[LinkedInOwner], fileName : String) = processor.writeLinkedInOwners(list, fileName)

  def writeBI (list: Seq[BusinessInstitution], fileName : String) = processor.writeBusinessInstitution(list,fileName)

  def writeBB (list: Seq[BusinessBackground], fileName : String) = processor.writeBusinessBackground(list,fileName)

  def writeAI (list: Seq[AcademicInstitution], fileName : String) = processor.writeAcademicInstitution(list,fileName)

  def writeAB (list: Seq[AcademicBackground], fileName : String) = processor.writeAcademicBackground(list,fileName)
}
