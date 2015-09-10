package controllers

import play.api._
import play.api.mvc._
import java.io.File

import play.api.data._
import play.api.data.Forms._
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

class Application extends Controller {

  sealed trait ExtraInfo

  case class Contact(email: String, phoneNumber: String) extends ExtraInfo
  object Contact {
    // implicit val contactFormat = Json.format[Contact]
    implicit val contactReads: Reads[Contact] = (
      (JsPath \ "email").read[String] and
      (JsPath \ "phoneNumber").read[String]
    )(Contact.apply _)

    implicit val contactWrites: Writes[Contact] = (
      (JsPath \ "email").write[String] and
      (JsPath \ "phoneNumber").write[String]
    )(unlift(Contact.unapply))
  }

  case class Person(name: String, age: Int, extraInfo: ExtraInfo)
  object Person {

    implicit object ExtraInfoReadFormat extends Reads[ExtraInfo] {
      def reads(js: JsValue) = {
        val e = (js \ "email").as[String]
        val p = (js \ "phoneNumber").as[String]
        Contact(e, p) match {
          case c: Contact => Json.fromJson[Contact](js)
        }
      }
    }

    implicit object ExtraInfoWriteFormat extends Writes[ExtraInfo] {
      def writes(obj: ExtraInfo) = {
        obj match {
          case c: Contact => Json.obj("email" -> c.email, "phoneNumber" -> c.phoneNumber)
        }
      }
    }

    implicit val personFormat = Json.format[Person]

  }

  def index = Action { request =>

    val contact = Contact("abelbryo@gmail.com", "+358449323823")
    val person = Person("Abel Terefe", 30, contact)

    println(Json.toJson(person))

    Ok(views.html.index("Your new application is ready."))
  }

  def save = Action(parse.multipartFormData) { implicit request =>
    val uploadedFiles: Seq[MultipartFormData.FilePart[play.api.libs.Files.TemporaryFile]] = request.body.files
    val data: Map[String, Seq[String]] = request.body.asFormUrlEncoded
    println(data)

    uploadedFiles foreach {
      case MultipartFormData.FilePart(key, filename, contentType, ref) => {
        val tmpFile = new File("/tmp/pictures")
        if (!tmpFile.exists) tmpFile.mkdirs()
        ref.moveTo(new File(s"/tmp/pictures/$filename"))
        Redirect(routes.Application.index)
      }
      case _ => Ok("Something wong with file!")
    }

    Redirect(routes.Application.index)

  }

  def save1 = Action(parse.temporaryFile) { request =>
    val result = request.body.moveTo(new File("/tmp/picture"))
    println(result)
    Ok("File uploaded")
  }

}
