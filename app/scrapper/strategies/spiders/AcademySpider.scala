package scrapper.strategies.spiders

import org.jsoup.nodes.Element

/**
 * Created by franco on 1/9/2015.
 */
object AcademySpider {

  def run(element: Element): String = {
    try{
      element.child(0).getElementsByClass("item-title").text()
    }
    catch {
      case iob: IndexOutOfBoundsException => ""
    }
  }
}
