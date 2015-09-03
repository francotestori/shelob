package controllers

import play.api.mvc.{Action, Controller}
import processors.CSVProcessor
import scrapper.LinkedInWizard

import slick.driver.H2Driver.api._

object Application extends Controller {
  def index = Action {

    val db = Database.forURL("jdbc:h2:file:~/projects/shelob/db/db","sa","")

    try{

    val urls : List[String] = CSVProcessor.process("/home/lucas/Documents/users-argentina.csv")

//    LinkedInWizard.run(urls)

    CSVProcessor.write("LinkedInOwner", LinkedInWizard.getTableData("LinkedInOwner"), "/home/lucas/Documents/resultado.csv")

    }finally db.close()

    Ok(views.html.index("Your new application is ready."))
  }

}