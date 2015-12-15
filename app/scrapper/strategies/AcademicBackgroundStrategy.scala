package scrapper.strategies

import org.jsoup.nodes.Element
import scrapper.strategies.spiders.{AcademicDescriptionSpider, IntervalSpider, TitleSpider, AcademySpider}

/**
 * Created by franco on 2/9/2015.
 */
class AcademicBackgroundStrategy {

  def apply(element:Element) : (String, String,String,String) = {
    val academy = scrapAcademy(element)
    val title = scrapTitle(element)
    val interval = scrapInterval(element)
    val description = scrapDescription(element)

    (academy,title,interval,description)
  }

  private def scrapAcademy(element: Element) : String =  AcademySpider.run(element)

  private def scrapTitle(element: Element) : (String) = TitleSpider.run(element)

  private def scrapInterval(element: Element) : String = IntervalSpider.runAcademy(element)

  private def scrapDescription(element: Element) : String = AcademicDescriptionSpider.run(element);


}

object AcademicBackgroundStrategy {

  def apply(element: Element): (String,String,String,String)= new AcademicBackgroundStrategy().apply(element)
}
