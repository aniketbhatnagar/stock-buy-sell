package citadel.services

package object impl {
  implicit def toBinarySearchable[T](list: List[T]) = new BinarySearchableList[T](list)
}
