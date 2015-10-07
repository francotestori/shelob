package controllers

import java.io.File

import generators.ZipGenerator
import play.api.mvc.{Action, Controller}
import processors.CSVProcessor
import scrapper.LinkedInWizard

import slick.driver.H2Driver.api._
import utils.{TableApocalypse, ShelobConstants}

/**
 * Created by franco on 1/10/2015.
 */
object Shelob extends Controller {

  def index(file : String) = Action {
    Ok(views.html.shelob(file,"Shelob"))
  }

  /**Populates db and generate zip File with Table CSVs included*/
  def shelob(file : String) = Action{ implicit request =>

    val db = Database.forURL("jdbc:h2:file:~/projects/uploader/db/db","sa","")

    try{

      val urls : List[String] = CSVProcessor.process(ShelobConstants.UPLOADER_PATH + file)

      LinkedInWizard.run(urls)

      generateCSVs

      createZip(ShelobConstants.SHELOB_ZIP,
        Iterable(
          ShelobConstants.ZIPPER_PATH + "personas.csv",
          ShelobConstants.ZIPPER_PATH + "negocio.csv",
          ShelobConstants.ZIPPER_PATH + "experiencia.csv",
          ShelobConstants.ZIPPER_PATH + "academia.csv",
          ShelobConstants.ZIPPER_PATH + "historial-academico.csv"
        ))

      TableApocalypse.judgement_day

      Redirect(routes.Shelob.download())

    }
    finally db.close()


  }

  /**Gets computation progress value*/
  def progress = LinkedInWizard.progress

  /**get the size value for progress bar*/
  def getSize = LinkedInWizard.getSize

  /**Generate CSVs from tables*/
  private def generateCSVs = {
    CSVProcessor.writeLO(LinkedInWizard.getOwnerTable, ShelobConstants.ZIPPER_PATH + "personas.csv")
    CSVProcessor.writeBI(LinkedInWizard.getInstitutionTable, ShelobConstants.ZIPPER_PATH + "negocio.csv")
    CSVProcessor.writeBB(LinkedInWizard.getBBTable, ShelobConstants.ZIPPER_PATH + "experiencia.csv")
    CSVProcessor.writeAI(LinkedInWizard.getAcademyTable, ShelobConstants.ZIPPER_PATH + "academia.csv")
    CSVProcessor.writeAB(LinkedInWizard.getABTable, ShelobConstants.ZIPPER_PATH + "historial-academico.csv")
  }

  /**Redirects to ZipGenerator to create zip in a determined path with a series of includes*/
  private def createZip(zipFilePath : String, includes : Iterable[String] ) = {
    ZipGenerator.zip(zipFilePath,includes)
  }

  def download() = Action{
    Ok.sendFile(content = new File(ShelobConstants.SHELOB_ZIP), fileName = _ => "shelob.zip")
  }

}
