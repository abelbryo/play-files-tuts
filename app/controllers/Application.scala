package controllers

import play.api._
import play.api.mvc._
import java.io.File

import play.api.data._
import play.api.data.Forms._



class Application extends Controller {


   def index = Action { request =>
       Ok(views.html.index("Your new application is ready."))
   }

   def save = Action( parse.multipartFormData) { implicit request =>
     val uploadedFiles: Seq[MultipartFormData.FilePart[play.api.libs.Files.TemporaryFile]] = request.body.files
     val data: Map[String, Seq[String]] = request.body.asFormUrlEncoded
     println(data)

     uploadedFiles foreach {
       case MultipartFormData.FilePart(key, filename, contentType, ref) => {
         val tmpFile = new File("/tmp/pictures")
         if(!tmpFile.exists) tmpFile.mkdirs()
         ref.moveTo(new File(s"/tmp/pictures/$filename"))
         Redirect(routes.Application.index)
       }
       case _ => Ok("Something wong with file!")
     }

     Redirect(routes.Application.index)

   }

   def save1 = Action(parse.temporaryFile) { request =>
     val result  = request.body.moveTo(new File("/tmp/picture"))
     println(result)
     Ok("File uploaded")
   }


}
