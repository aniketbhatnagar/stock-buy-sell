package citadel.controllers

import org.slf4j.{LoggerFactory, Logger}
import play.api.mvc.{BodyParser, Result, Controller}

import scala.concurrent.{ExecutionContext, Future}

trait BaseController extends Controller {

  private val LOGGER: Logger = LoggerFactory.getLogger(this.getClass)

  def jackson[T](clazz: Class[T])(implicit executionContext: ExecutionContext): BodyParser[T] = parse.raw.map(buffer => {
    JacksonWrapper.deserialize(buffer.asBytes().get, clazz)
  })

  protected def buildResult[T](futureValue: Future[T])(implicit executionContext: ExecutionContext): Future[Result] = {
    futureValue.map(value => {
      ServiceResponseBuilder.generateSuccessResponse(value)
    }).recover({
      case t: Throwable => {
        LOGGER.error("Exception in future: ", t)
        ServiceResponseBuilder.generateErrorResponse(t.getMessage)
      }
    }).map(response => {
      Ok(JacksonWrapper.serialize(response)).withHeaders("content-type" -> "application/json")
    })
  }

}
