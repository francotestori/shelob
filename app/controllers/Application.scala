package controllers

import java.io.File

import generators.ZipGenerator
import play.api.mvc.{Action, Controller}
import processors.CSVProcessor
import scrapper.LinkedInWizard

import slick.driver.H2Driver.api._
import utils.ShelobConstants

object Application extends Controller {

  def index(file : String) = Action {
    Ok(views.html.shelob(file,"Shelob"))
  }

  /**Populates db and generate zip File with Table CSVs included*/
  def apply(file : String) = {
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

    }
    finally db.close()
  }



  /**Get the index value for progress bar*/
  def indexValue = LinkedInWizard.getIndex

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

  /**ZIP Downloader for AJAX request*/
  def downloadZIP = Action{Ok.sendFile(content = new File(ShelobConstants.SHELOB_ZIP), fileName = _=> ShelobConstants.SHELOB_ZIP)}

}