package scrapper.strategies.spiders

import org.jsoup.nodes.Element

/**
 * Created by franco on 1/9/2015.
 */
object DescriptionSpider {

  def run(element: Element): String = {
    try {
      element.getElementsByClass("description summary-field-show-more").text()
//      var item = element.child(2).getElementsByClass("description").text()
//      if(item.isEmpty) item = element.getElementsByClass("description").text()
//      item
    } catch {
      case iob: IndexOutOfBoundsException => ""
    }
  }

}
