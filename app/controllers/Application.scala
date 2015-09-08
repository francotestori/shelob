package controllers

import generators.ZipGenerator
import play.api.mvc.{Action, Controller}
import processors.CSVProcessor
import scrapper.LinkedInWizard

import slick.driver.H2Driver.api._

object Application extends Controller {
  def index = Action {

    val db = Database.forURL("jdbc:h2:file:~/projects/shelob/db/db","sa","")

    try{

    val urls : List[String] = CSVProcessor.process("C:\\users-argentina.csv")

      LinkedInWizard.run(urls)

      CSVProcessor.writeLO(LinkedInWizard.getOwnerTable, "C:\\Users\\franco\\shelobItems\\personas.csv")
      CSVProcessor.writeBI(LinkedInWizard.getInstitutionTable, "C:\\Users\\franco\\shelobItems\\negocio.csv")
      CSVProcessor.writeBB(LinkedInWizard.getBBTable, "C:\\Users\\franco\\shelobItems\\experiencia.csv")
      CSVProcessor.writeAI(LinkedInWizard.getAcademyTable, "C:\\Users\\franco\\shelobItems\\academia.csv")
      CSVProcessor.writeAB(LinkedInWizard.getABTable, "C:\\Users\\franco\\shelobItems\\historial-academico.csv")

      ZipGenerator.zip("~\\shelob.zip",

        Iterable("C:\\Users\\franco\\shelobItems\\personas.csv",
        "C:\\Users\\franco\\shelobItems\\negocio.csv",
        "C:\\Users\\franco\\shelobItems\\experiencia.csv",
        "C:\\Users\\franco\\shelobItems\\academia.csv",
        "C:\\Users\\franco\\shelobItems\\historial-academico.csv"
      ))

    }finally db.close()

    Ok(views.html.index("Your new application is ready."))
  }

}