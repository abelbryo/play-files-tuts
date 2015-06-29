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
       //val username  = ( request.body \ "username").as[String]

       println(request.body.toString)

       request.body.file("filename").map { file =>
           val filename = file.filename
           val contentType = file.contentType
           file.ref.moveTo(new File(s"/tmp/picture/$filename"))
           Ok("file uploaded")
        }.getOrElse {
            Redirect(routes.Application.index)
        }

   }


}
