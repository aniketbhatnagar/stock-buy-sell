package citadel.services.impl

import scala.annotation.tailrec

/**
 * Extension of a list to support binary search.
 */
class BinarySearchableList[T](list: List[T]) {

  /**
   * Binary searches on the list assuming the list is sorted.
   * @param comapareFunc The function decides whether at a given row, should the search move towards first half or
   *                     last half of remaining list.
   *                     If the function returns -1, search proceeds to first half of remaining list else if it returns -1,
   *                     then it proceeds to last half. If the function returns 0, then a match is found.
   * @return Optional matched value.
   */
  def binarySearch(comapareFunc: T => Int): Option[T] = {
    @tailrec
    def searchItr(start: Int, end: Int): Option[T] = {
      if (start > end) {
        None
      } else {
        val mid = start + (end - start) / 2
        val midElement = list(mid)
        val compared = comapareFunc(midElement)
        compared match {
          case -1 => searchItr(start, mid - 1)
          case  0 => Option(midElement)
          case  1 => searchItr(mid + 1, end)
        }
      }
    }
    searchItr(0, list.length - 1)
  }
}
