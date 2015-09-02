package controllers

import play.api.mvc.{Action, Controller}
import scrapper.LinkedInWizard

import slick.driver.H2Driver.api._

object Application extends Controller {
  def index = Action {

    val db = Database.forURL("jdbc:h2:file:~/projects/shelob/db/db","sa","")

    try{

    val urls : List[String] = List("http://ar.linkedin.com/in/arielDarioPerez/es",
      "https://ar.linkedin.com/in/horaciorodriguezlarreta",
      "http://ar.linkedin.com/in/ricardoanibalpasquini",
      "https://ar.linkedin.com/in/luisrgarcia",
      "https://ar.linkedin.com/pub/franco-testori/38/814/197")

    LinkedInWizard.run(urls)

    }finally db.close()

    Ok(views.html.index("Your new application is ready."))
  }

}