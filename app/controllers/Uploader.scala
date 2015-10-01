package controllers

import java.io.File

import controllers.Application._
import play.api.mvc.{Action, Controller}
import utils.ShelobConstants

/**
 * Created by franco on 28/9/2015.
 */
object Uploader extends Controller{

  def index = Action {
    Ok(views.html.uploader("Shelob"))
  }

  /**Adds input CSV into a determined path so it can later be analyzed*/
  def upload() = Action(parse.multipartFormData) {  request =>
    request.body.file("fileUpload").map { file =>
      val filename = file.filename
      val contentType = file.contentType.get
      file.ref.moveTo(new File(ShelobConstants.UPLOADER_PATH + filename))
      Redirect(routes.Application.index(filename))
    }.getOrElse {
      Redirect(routes.Uploader.index()).flashing(
        "error" -> "Missing file"
      )
    }
  }

  /** Ajax uploader. Needs testing*/
  def upload2 = Action(parse.temporaryFile) { request =>
    request.body.moveTo(new File(ShelobConstants.ZIPPER_PATH))
    Ok("File uploaded")
  }

}
