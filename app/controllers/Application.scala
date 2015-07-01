package controllers

import play.api._
import play.api.mvc._
import java.io.File

import play.api.data._
import play.api.data.Forms._

import play.api.libs.json._
import play.api.libs.json.Json._

class Application extends Controller {

  def index = Action { request =>
    Ok(views.html.index("Your new application is ready."))
  }

  def save = Action(parse.multipartFormData) { implicit request =>
    val uploadedFiles: Seq[MultipartFormData.FilePart[play.api.libs.Files.TemporaryFile]] = request.body.files
    val otherData: Map[String, Seq[String]] = request.body.asFormUrlEncoded

    val result = uploadedFiles map {
      case MultipartFormData.FilePart(key, filename, contentType, ref) => {
        val tmpFile = new File("/tmp/pictures")
        if (!tmpFile.exists) tmpFile.mkdirs()
        val file = new File(s"/tmp/pictures/$filename")
        ref.moveTo(file)

        obj("file_size_bytes" -> file.length, "filename" -> filename)
      }
      case _ => obj("file_size_bytes" -> -1)
    }

    Ok(obj("data" -> result))

  }


}
