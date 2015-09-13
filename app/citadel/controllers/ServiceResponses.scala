package citadel.controllers

/**
 * Represents a service response.
 * 
 * @author Aniket
 *
 */
case class ServiceResponse(status: ServiceStatus, payload: Any) {

}

/**
 * Helper to creates ServiceResponse instances.
 * 
 * @author Aniket
 *
 */
object ServiceResponseBuilder {

  /**
   * Generates successful service response.
   * @return ServiceResponse instance.
   */
  def generateGenericSuccessResponse() = {
    new ServiceResponse(SuccessServiceStatus, "success")
  }

  /**
   * Generates successful service response.
   * @param payload Payload in response.
   * @return ServiceResponse instance.
   */
  def generateSuccessResponse(payload: Any) = {
    new ServiceResponse(SuccessServiceStatus, payload)
  }

  /**
   * Generates generic error service response.
   * @param payload Payload in response.
   * @return ServiceResponse instance.
   */
  def generateErrorResponse(payload: Any) = {
    new ServiceResponse(GenericErrorServiceStatus, payload)
  }
  
  /**
   * Generates generic error service response.
   * @param statusCode status code returned by service.
   * @param payload Payload in response.
   * @return ServiceResponse instance.
   */
  def generateErrorResponse(statusCode: Integer, payload: String) = {
    new ServiceResponse(new ServiceStatus(statusCode, payload), None)
  }
}