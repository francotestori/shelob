package scrapper

import org.jsoup.nodes.Element

/**
 * Created by franco on 1/9/2015.
 */
object LinkedInValidator {

  def validateUrl(url : String) : Boolean = {
    !url.isEmpty && !"".equals(url) && url.contains("linkedin")
  }

  def validateBusiness(role : String, institute : String, interval : String): Boolean = {
    !role.isEmpty && !institute.isEmpty && !interval.isEmpty
  }

  def validateAcademic(academy : String, title : String, interval : String): Boolean = {
    !academy.isEmpty && !title.isEmpty && !interval.isEmpty
  }

  def validateOwner(name : String, location : String) : Boolean = {
    !name.isEmpty && !location.isEmpty
  }
}
