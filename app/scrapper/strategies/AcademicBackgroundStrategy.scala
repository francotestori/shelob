package scrapper.strategies

import org.jsoup.nodes.Element
import scrapper.strategies.spiders.{IntervalSpider, TitleSpider, AcademySpider}

/**
 * Created by franco on 2/9/2015.
 */
class AcademicBackgroundStrategy {

  def apply(element:Element) : (String, String,String) = {
    val academy = scrapAcademy(element)
    val title = scrapTitle(element)
    val interval = scrapInterval(element)

    (academy,title,interval)
  }

  private def scrapAcademy(element: Element) : String =  AcademySpider.run(element)

  private def scrapTitle(element: Element) : (String) = TitleSpider.run(element)

  private def scrapInterval(element: Element) : String = IntervalSpider.runAcademy(element)
}

object AcademicBackgroundStrategy {

  def apply(element: Element): (String,String,String)= new AcademicBackgroundStrategy().apply(element)
}
