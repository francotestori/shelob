package scrapper.strategies

import org.jsoup.nodes.Element

/**
 * Created by franco on 1/9/2015.
 */
object IntervalStrategy {

  implicit class IntervalStrategies(element : Element) {

    def businessFirst(): String = {
      try {
        element.children().get(0).children().get(1).text()
      } catch {
        case iob: IndexOutOfBoundsException => ""
      }
    }

    def academyFirst(): String = {
      try {
        if (element.children().get(0).children().get(0).text().equals("")) {
          return element.children().get(0).children().get(2).text()
        }

        if (element.children().get(0).children().get(0).children().get(0).text().equals("")) {
          return element.children().get(0).children().get(0).children().get(2).text()
        }
        element.children().get(0).children().get(0).children().get(1).text()
      } catch {
        case iob: IndexOutOfBoundsException => ""
      }
    }
  }

}
