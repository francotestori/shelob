package scrapper.strategies.spiders

import org.jsoup.nodes.Element

/**
 * Created by franco on 1/9/2015.
 */
object RoleSpider {

  def first(element : Element, n : Int): String = {
    try {
      element.children().get(0).children().get(0).children().get(n).text()
    } catch {
      case iob: IndexOutOfBoundsException => ""
    }
  }

  def second(element : Element, n : Int): String = {
    try {
      element.children().get(0).children().get(n).text()
    } catch {
      case iob: IndexOutOfBoundsException => ""
    }
  }
}
