package controllers

import generators.ZipGenerator
import play.api.mvc.{Action, Controller}
import processors.CSVProcessor
import scrapper.LinkedInWizard

import slick.driver.H2Driver.api._

object Application extends Controller {

  def index = Action {

//    apply("C:\\users-argentina.csv")

//    createZip

    Ok(views.html.shelob("Shelob"))
  }

  def apply(file : String) = {
    val db = Database.forURL("jdbc:h2:file:~/projects/shelob/db/db","sa","")

    try{

      val urls : List[String] = CSVProcessor.process(file)

      LinkedInWizard.run(urls)

      CSVProcessor.writeLO(LinkedInWizard.getOwnerTable, "C:\\Users\\franco\\shelobItems\\personas.csv")
      CSVProcessor.writeBI(LinkedInWizard.getInstitutionTable, "C:\\Users\\franco\\shelobItems\\negocio.csv")
      CSVProcessor.writeBB(LinkedInWizard.getBBTable, "C:\\Users\\franco\\shelobItems\\experiencia.csv")
      CSVProcessor.writeAI(LinkedInWizard.getAcademyTable, "C:\\Users\\franco\\shelobItems\\academia.csv")
      CSVProcessor.writeAB(LinkedInWizard.getABTable, "C:\\Users\\franco\\shelobItems\\historial-academico.csv")

      createZip

    }finally db.close()
  }

  def createZip = {
    ZipGenerator.zip("~\\shelob.zip",

      Iterable("C:\\Users\\franco\\shelobItems\\personas.csv",
        "C:\\Users\\franco\\shelobItems\\negocio.csv",
        "C:\\Users\\franco\\shelobItems\\experiencia.csv",
        "C:\\Users\\franco\\shelobItems\\academia.csv",
        "C:\\Users\\franco\\shelobItems\\historial-academico.csv"
      ))
  }

  def upload = Action(parse.multipartFormData) { request =>
    request.body.file("picture").map { picture =>
      import java.io.File
      val filename = picture.filename
      val contentType = picture.contentType
      picture.ref.moveTo(new File("/tmp/picture"))
      Ok("File uploaded")
    }.getOrElse {
      Redirect(routes.Application.index).flashing(
        "error" -> "Missing file"
      )
    }
  }

}