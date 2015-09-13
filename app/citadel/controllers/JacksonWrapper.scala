package citadel.controllers

import java.io.{ByteArrayOutputStream, InputStream}
import java.lang.reflect.{ParameterizedType, Type}

import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule

/**
 * Wrapper over Java's Jackson library to parse JSON.
 *
 * @author Aniket
 *
 */
object JacksonWrapper {

  /* Create ObjectMapper and register scala specific modules. */
  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  /**
   * Serializes a value into JSON.
   * @param value Value to serialize.
   * @return String representing JSON.
   */
  def serialize(value: Any): String = {
    import java.io.StringWriter
    val writer = new StringWriter()
    mapper.writeValue(writer, value)
    writer.toString
  }

  /**
   * Serializes a value into JSON.
   * @param value Value to serialize.
   * @return bytes representing JSON.
   */
  def serializeAsBytes(value: Any): Array[Byte] = {
    val outputStream = new ByteArrayOutputStream()
    mapper.writeValue(outputStream, value)
    outputStream.toByteArray
  }

  /**
   * Deserializes JSON into object.
   * @param value Byte array representing JSON.
   * @param instanceType Expected deserialized type.
   * @return Object instance.
   */
  def deserialize[T <: Any](value: Array[Byte], instanceType: Class[T]): T =
    mapper.readValue(value, instanceType)

  /**
   * Deserializes JSON into object.
   * @param value String representing JSON.
   * @return Object instance.
   */
  def deserialize[T: Manifest](value: String): T =
    mapper.readValue(value, typeReference[T])

  /**
   * Deserializes JSON raw bytes into object.
   * @param raw String representing JSON.
   * @return Object instance.
   */
  def deserialize[T: Manifest](raw: Array[Byte]): T =
    mapper.readValue(raw, typeReference[T])
    
  /**
   * Deserializes JSON into object.
   * @param stream Stream representing JSON.
   * @return Object instance.
   */
  def deserialize[T: Manifest](stream: InputStream): T =
    mapper.readValue(stream, typeReference[T])  

  private[this] def typeReference[T: Manifest] = new TypeReference[T] {
    override def getType = typeFromManifest(manifest[T])
  }

  private[this] def typeFromManifest(m: Manifest[_]): Type = {
    if (m.typeArguments.isEmpty) { m.runtimeClass }
    else new ParameterizedType {
      def getRawType = m.runtimeClass
      def getActualTypeArguments = m.typeArguments.map(typeFromManifest).toArray
      def getOwnerType = null
    }
  }
}