package scrapper.strategies.spiders

import org.jsoup.nodes.Element

/**
 * Created by franco on 14/12/2015.
 */
object AcademicDescriptionSpider {

  def run(element: Element): String = {
    element.getElementsByClass("description").text()
  }

}
