package citadel.controllers

import scala.beans.BeanProperty

/**
 * Represents status code and message returned in service response.
 * @author Aniket
 *
 */
case class ServiceStatus(@BeanProperty code: Integer, @BeanProperty message: String) {}

/**
 * Object representing successful status in service response.
 * @author Aniket
 *
 */
object SuccessServiceStatus extends ServiceStatus(200, "Ok")

/**
 * Object representing generic error status in service response.
 * @author Aniket
 *
 */
object GenericErrorServiceStatus extends ServiceStatus(500, "Generic error") 