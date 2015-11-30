package controllers

import java.io.{FileFilter, File}

import engine.GoogleSearcher
import generators.ZipGenerator
import play.api.mvc.{Action, Controller}
import processors.CSVProcessor
import scrapper.LinkedInWizard

import slick.driver.H2Driver.api._
import utils.{FileApocalypse, ShelobConstants}

import play.api.Play.current
import play.api.Play

/**
 * Created by franco on 1/10/2015.
 */
object Shelob extends Controller {

  def index(file : String) = Action {
    Ok(views.html.shelob(file,"Shelob"))
  }

  /**Populates db and generate zip File with Table CSVs included*/
  def shelob(file : String) = Action{ implicit request =>

    val db = Database.forURL(Play.application.configuration.getString("db.default.url").get,"sa","")

    try {

      //Lists all users with null urls to get id & name
      val namesNullUrl: List[(String, String)] = CSVProcessor.getNullURLTuples(ShelobConstants.UPLOADER_PATH + file)

      //Filters and gets roles' file pathName
      val dir = new File(ShelobConstants.UPLOADER_PATH)
      val roles: List[File] = dir.listFiles(new FileFilter {
        override def accept(pathname: File): Boolean = pathname.getName.startsWith("roles+users-")
      }).toList
      val rolesFile = roles.head.getAbsolutePath

      //List all users with null urls to get id, startupName & role
      val rolesNullUrl: List[(String, String, String)] = CSVProcessor.getNullURLTuplesWithRoles(rolesFile)

      //Gets valid urls & ids tupled
      val urls : List[(String,String)] = CSVProcessor.process(ShelobConstants.UPLOADER_PATH + file)

      //TODO Make with GoogleSearcher a list of (url,id) so as to be used with LinkedIngWizard
      GoogleSearcher.createTextFile(ShelobConstants.UPLOADER_PATH + "resultadoCrawler.txt")
            namesNullUrl.foreach(name => GoogleSearcher.searchLinkedinUrl(name._2))

      for (i <- 0 to 5) {
        GoogleSearcher.searchLinkedinUrl(namesNullUrl(i)._2)
      }

      GoogleSearcher.closeWriter()

      //Scrap generation of data with LinkedInWizard for non-searched urls
      LinkedInWizard.run(urls,false)

      //File generation
      generateCSVs

      val files = Iterable(
        ShelobConstants.ZIPPER_PATH + "personas.csv",
        ShelobConstants.ZIPPER_PATH + "negocio.csv",
        ShelobConstants.ZIPPER_PATH + "experiencia.csv",
        ShelobConstants.ZIPPER_PATH + "academia.csv",
        ShelobConstants.ZIPPER_PATH + "historial-academico.csv"
      )

      //Zip generation
      createZip(ShelobConstants.SHELOB_ZIP, files)

      val delete = Iterable(
        ShelobConstants.ZIPPER_PATH + "personas.csv",
        ShelobConstants.ZIPPER_PATH + "negocio.csv",
        ShelobConstants.ZIPPER_PATH + "experiencia.csv",
        ShelobConstants.ZIPPER_PATH + "academia.csv",
        ShelobConstants.ZIPPER_PATH + "historial-academico.csv",
        ShelobConstants.UPLOADER_PATH + file,
        ShelobConstants.UPLOADER_PATH + rolesFile
      )

      //Clean up files and database
      FileApocalypse.judgement_day2
      FileApocalypse.restartIdentities
      FileApocalypse.file_anihilation(delete)

      Redirect(routes.Shelob.download())

    }

    finally db.close()

  }

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
