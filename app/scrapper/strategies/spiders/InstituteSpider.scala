package scrapper.strategies.spiders

import org.jsoup.nodes.Element

/**
 * Created by franco on 1/9/2015.
 */
object InstituteSpider {

  def run(element: Element): String = {
    try {
            element.getElementsByTag("h5").text()
//      val item = element.child(0).getElementsByClass("item-subtitle").text()
//      item
    } catch {
      case iob: IndexOutOfBoundsException => ""
    }
  }

}
