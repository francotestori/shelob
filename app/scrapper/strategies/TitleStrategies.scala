package scrapper.strategies

import org.jsoup.nodes.Element

/**
 * Created by franco on 1/9/2015.
 */
object TitleStrategy {

  implicit class TitleStrategies(element : Element) {

    def first(): String = {
      try {
        if (element.children().get(0).children().get(0).text().equals("")) {
          return element.children().get(0).children().get(1).children().get(1).text()
        }

        if (element.children().get(0).children().get(0).children().get(0).text().equals("")) {
          return element.children().get(0).children().get(0).children().get(1).children().get(1).text()
        }
        element.children().get(0).children().get(0).children().get(0).children().get(1).text()
      } catch {
        case iob: IndexOutOfBoundsException => ""
      }
    }
  }

}
