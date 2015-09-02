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
    val institute : (String,Int) = scrapInstitute(element)
    val role : String = scrapRole(element,institute._2)

    (role,institute._1,interval,info)
  }

  private def scrapInfo(element: Element) : String = {
    var info : String = DescriptionSpider.first(element)
    if(info.isEmpty) info = DescriptionSpider.second(element)
    info
  }

  private def scrapInterval(element: Element) : String = {
    var info : String = IntervalSpider.businessFirst(element)
    if(info.isEmpty) info = IntervalSpider.businessSecond(element)
    info
  }

  private def scrapInstitute(element: Element) : (String,Int) = {
    var n : Int = 1

    var info : String = InstituteSpider.first(element)
    if(info.isEmpty) info = InstituteSpider.second(element)
    if(info.isEmpty) {
      info = InstituteSpider.third(element)
      n = 0
    }
    if(info.isEmpty) info = InstituteSpider.fourth(element)

    (info,n)
  }

  private def scrapRole(element: Element, n : Int) : String = {
    var info : String = RoleSpider.first(element,n)
    if(info.isEmpty) info = RoleSpider.second(element,n)
    info
  }

}

object BusinessBackgroundStrategy {

  def apply(element: Element): (String,String,String,String)={
    apply(element)
  }

}
