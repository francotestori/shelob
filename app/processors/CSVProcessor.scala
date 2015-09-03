package processors

import java.io.File

import com.github.tototoshi.csv.{CSVWriter, CSVReader}
import models._

/**
 * Created by franco on 2/9/2015.
 */
class CSVProcessor {


  def process (file : String): List[String]= {

    val reader = CSVReader.open(new File(file))

    val inputs: List[Map[String, String]] = reader.allWithHeaders()

    inputs.map(e => e.get("linkedin_url")).distinct.map(i => i.get)

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

  def process (file : String): List[String] = new CSVProcessor().process(file)

  def writeLO (list: Seq[LinkedInOwner], fileName : String) = new CSVProcessor().writeLinkedInOwners(list, fileName)

  def writeBI (list: Seq[BusinessInstitution], fileName : String) = new CSVProcessor().writeBusinessInstitution(list,fileName)

  def writeBB (list: Seq[BusinessBackground], fileName : String) = new CSVProcessor().writeBusinessBackground(list,fileName)

  def writeAI (list: Seq[AcademicInstitution], fileName : String) = new CSVProcessor().writeAcademicInstitution(list,fileName)

  def writeAB (list: Seq[AcademicBackground], fileName : String) = new CSVProcessor().writeAcademicBackground(list,fileName)
}
