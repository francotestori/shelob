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
      val dir = new File("/home/lucas/shelobUploads/")
      var roleFile : List[File] = List()
      var rolesFilePath : String = ""
      val roles: Array[File] = dir.listFiles(new FileFilter {
        override def accept(pathname: File): Boolean = pathname.getName.startsWith("roles+users-")
      })

      if (roles.nonEmpty) {
        roleFile = roles.toList
        rolesFilePath = roleFile.head.getAbsolutePath
      }

      //List all users with null urls to get id, startupName & role
      val rolesNullUrl: List[(String, String, String)] = CSVProcessor.getNullURLTuplesWithRoles(rolesFilePath)

      //Gets valid urls & ids tupled
      val urls : List[(String,String)] = CSVProcessor.process(ShelobConstants.UPLOADER_PATH + file)

      //Make with GoogleSearcher a list of (url,id) so as to be used with LinkedIngWizard
      var rolesOfSpecificUser: List[(String, String)] = List()
      var searchedURLs = List[(String, String)]()

      GoogleSearcher.printTime()
      GoogleSearcher.startTorClient()

      //For each user with the linkedin url null
      namesNullUrl.foreach{tupleUserFile =>
        var url: List[String] = List()
        val (idUserFile, name) = tupleUserFile
        rolesOfSpecificUser = List()

        //For each user with the linkedin url null
        rolesNullUrl.foreach{ tupleRoleFile =>
          val (idRoleFile, startupName, role) = tupleRoleFile
          //Compare ids to get the startup and role of a user
          if (idUserFile == idRoleFile) {
            rolesOfSpecificUser ::= (startupName, role)
          }
        }
        //If there is a role for a specific user, use it to search the linkedin url
        if (rolesOfSpecificUser.nonEmpty) {
          url ::= GoogleSearcher.searchLinkedinUrl(name, null, null)
          rolesOfSpecificUser.foreach { tuple =>
            val (startupName, role) = tuple
            url ::= GoogleSearcher.searchLinkedinUrl(name, startupName, role)
          }
        }
        else {
          url ::= GoogleSearcher.searchLinkedinUrl(name, null, null)
        }
        //Generate the tuple with the searched url and the user id
        searchedURLs ::= (url.groupBy(identity).maxBy(_._2.size)._1, idUserFile)
      }

      GoogleSearcher.printTime()
      GoogleSearcher.stopTorClient()

      //Scrap generation of data with LinkedInWizard for non-searched urls
      LinkedInWizard.run(urls,false)

      searchedURLs.filter{ case (url, _ ) => !url.equals("") }
      //Scrap generation of data with LinkedInWizard for searched urls
      LinkedInWizard.run(searchedURLs,true)

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
        ShelobConstants.UPLOADER_PATH + rolesFilePath
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
