package scrapper.strategies.spiders

import org.jsoup.nodes.Element

/**
 * Created by franco on 1/9/2015.
 */
object IntervalSpider {

  def runBusiness(element : Element): String = {
    try {
      element.getElementsByClass("experience-date-locale").text()
//      var item = element.child(0).getElementsByClass("date-range").text()
//      if(item.isEmpty) item = element.getElementsByClass("date-range").text()
//      item
    } catch {
      case iob: IndexOutOfBoundsException => ""
    }
  }


  def runAcademy(element : Element): String = {
    element.getElementsByClass("date-range").text()
//    try {
//      if (element.children().get(0).children().get(0).text().equals("")) {
//        return element.children().get(0).children().get(2).text()
//      }
//
//      if (element.children().get(0).children().get(0).children().get(0).text().equals("")) {
//        return element.children().get(0).children().get(0).children().get(2).text()
//      }
//      element.children().get(0).children().get(0).children().get(1).text()
//    } catch {
//      case iob: IndexOutOfBoundsException => ""
//    }
  }
}
