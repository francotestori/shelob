package scrapper.strategies.spiders

import org.jsoup.nodes.Element

/**
 * Created by franco on 1/9/2015.
 */
object InstituteSpider {

  def first(element: Element): String = {
    try {
      element.children().get(0).children().get(0).children().get(2).text()
    } catch {
      case iob: IndexOutOfBoundsException => ""
    }
  }

  def second(element: Element): String = {
    try {
      element.children().get(0).children().get(2).text()
    } catch {
      case iob: IndexOutOfBoundsException => ""
    }
  }

  def third(element: Element): String = {
    try {
      element.children().get(0).children().get(0).children().get(1).text()
    } catch {
      case iob: IndexOutOfBoundsException => ""
    }
  }

  def fourth(element: Element): String = {
    try {
      element.children().get(0).children().get(1).text()
    } catch {
      case iob: IndexOutOfBoundsException => ""
    }
  }



}
