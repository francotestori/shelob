package processors

import java.io.File

import com.github.tototoshi.csv.CSVReader

/**
 * Created by franco on 2/9/2015.
 */
class CSVProcessor {


  def process (file : String): List[String]= {

    val reader = CSVReader.open(new File(file))

    val inputs: List[Map[String, String]] = reader.allWithHeaders()

    inputs.map(e => e.get("linkedin_url")).distinct.map(i => i.get)

  }

}

object CSVProcessor {
  def process (file : String): List[String] = new CSVProcessor().process(file)
}
