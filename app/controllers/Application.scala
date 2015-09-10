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

  object ExtraInfo {
    implicit val ExtraInfoWrites: Writes[ExtraInfo] = {
      new Writes[ExtraInfo] {
        def writes(obj: ExtraInfo) = {
          obj match {
            case c: Contact => Contact.contactWrites.writes(c)
          }
        }
      }
    }

  }

  object Contact {
    implicit val contactReads: Reads[Contact] = (
      (JsPath \ "email").read[String] and
      (JsPath \ "phone_number").read[String]
    )(Contact.apply _)

    implicit val contactWrites: Writes[Contact] = (
      (JsPath \ "email").write[String] and
      (JsPath \ "phone_number").write[String]
    )(unlift(Contact.unapply))
  }

  case class Person(name: String, age: Int, extraInfo: ExtraInfo)
  object Person {

    private def isEmail(str: String) = true // fake test

    implicit object ExtraInfoReadFormat extends Reads[ExtraInfo] {
      def reads(js: JsValue) = {
        (js \ "email").as[String] match {
          case c if isEmail(c) => Json.fromJson[Contact](js)
        }
      }
    }

    implicit val personReads: Reads[Person] = (
      (JsPath \ "name").read[String] and
      (JsPath \ "age").read[Int] and
      (JsPath \ "extra_info").read[ExtraInfo]
    )(Person.apply _)

    implicit val personWrites: Writes[Person] = (
      (JsPath \ "email").write[String] and
      (JsPath \ "age").write[Int] and
      (JsPath \ "extra_info").write[ExtraInfo]
    )(unlift(Person.unapply))

    // implicit val personFormat = Json.format[Person]
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
