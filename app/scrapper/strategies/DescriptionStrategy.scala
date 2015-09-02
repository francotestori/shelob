package scrapper.strategies

import org.jsoup.nodes.Element

/**
 * Created by franco on 1/9/2015.
 */
object DescriptionStrategy {

  def first(element: Element): String = {
    try {
      element.children().get(0).children().get(2).text()
    } catch {
      case iob: IndexOutOfBoundsException => ""
    }
  }
}
