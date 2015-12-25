package scrapper.strategies.spiders

import org.jsoup.nodes.Element

/**
 * Created by franco on 1/9/2015.
 */
object TitleSpider {

  def run(element : Element): String = {
    try{
      element.child(0).getElementsByClass("item-subtitle").text()
    }
    catch {
      case iob: IndexOutOfBoundsException => ""
    }
//    try {
//      if (element.children().get(0).children().get(0).text().equals("")) {
//        return element.children().get(0).children().get(1).children().get(1).text()
//      }
//
//      if (element.children().get(0).children().get(0).children().get(0).text().equals("")) {
//        return element.children().get(0).children().get(0).children().get(1).children().get(1).text()
//      }
//      element.children().get(0).children().get(0).children().get(0).children().get(1).text()
//    }
  }

}
