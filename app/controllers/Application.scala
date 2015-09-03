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
//
//    val urls : List[String] = CSVProcessor.process("C:\\users-argentina.csv")
//
//      LinkedInWizard.run(urls)
//
//      CSVProcessor.writeLO(LinkedInWizard.getOwnerTable, "C:\\Users\\franco\\shelobItems\\owners.csv")
//      CSVProcessor.writeBI(LinkedInWizard.getInstitutionTable, "C:\\Users\\franco\\shelobItems\\institution.csv")
//      CSVProcessor.writeBB(LinkedInWizard.getBBTable, "C:\\Users\\franco\\shelobItems\\bbackground.csv")
//      CSVProcessor.writeAI(LinkedInWizard.getAcademyTable, "C:\\Users\\franco\\shelobItems\\academy.csv")
//      CSVProcessor.writeAB(LinkedInWizard.getABTable, "C:\\Users\\franco\\shelobItems\\abackground.csv")

      ZipGenerator.zip("C:\\Users\\franco\\shelobItems\\shelob.zip", Iterable("C:\\Users\\franco\\shelobItems\\owners.csv",
        "C:\\Users\\franco\\shelobItems\\institution.csv",
        "C:\\Users\\franco\\shelobItems\\bbackground.csv",
        "C:\\Users\\franco\\shelobItems\\academy.csv",
        "C:\\Users\\franco\\shelobItems\\abackground.csv"
      ))

    }finally db.close()

    Ok(views.html.index("Your new application is ready."))
  }

}