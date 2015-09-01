package scrapper

import org.jsoup.nodes.Element

/**
 * Created by franco on 1/9/2015.
 */
class LinkedInValidator {

  private def validateBusiness(role : String, institute : String, when : String, desc : String): Boolean = {
    !role.isEmpty && !institute.isEmpty && !when.isEmpty && !desc.isEmpty
  }

  private def validateAcademic(academy : String, title : String, interval : String): Boolean = {
    !academy.isEmpty && !title.isEmpty && !interval.isEmpty
  }
}
