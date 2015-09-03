package processors

import java.io.File

import com.github.tototoshi.csv.{CSVWriter, CSVReader}
import models.LinkedInOwner

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

}

object CSVProcessor {
  def process (file : String): List[String] = new CSVProcessor().process(file)
  def write (tableType : String, list: Seq[LinkedInOwner], fileName : String) = tableType match {
    case "LinkedInOwner" => new CSVProcessor().writeLinkedInOwners(list, fileName)
  }
}
