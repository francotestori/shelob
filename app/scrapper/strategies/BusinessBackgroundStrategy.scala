package scrapper.strategies

import org.jsoup.nodes.Element
import scrapper.strategies.spiders.{RoleSpider, IntervalSpider, InstituteSpider, DescriptionSpider}

/**
 * Created by franco on 2/9/2015.
 */
class BusinessBackgroundStrategy {

  def apply(element : Element) : (String,String,String,String)={
    val info : String = scrapInfo(element)
    val interval : String = scrapInterval(element)
    val institute : String = scrapInstitute(element)
    val role : String = scrapRole(element)

    (role,institute,interval,info)
  }

  private def scrapInfo(element: Element) : String = {
    DescriptionSpider.run(element)
  }

  private def scrapInterval(element: Element) : String = {
    IntervalSpider.runAcademy(element)
  }

  private def scrapInstitute(element: Element) : (String) = {
    InstituteSpider.run(element)
  }

  private def scrapRole(element: Element) : String = {
    RoleSpider.run(element)
  }

}

object BusinessBackgroundStrategy {

  def apply(element: Element): (String,String,String,String)={
    new BusinessBackgroundStrategy().apply(element)
  }

}
