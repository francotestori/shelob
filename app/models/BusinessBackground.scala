package models

import java.util.Date

/**
 * Created by franco on 22/05/2015.
 */
case class BusinessBackground(id:Long,role:String,businessInstitution:BusinessInstitution,startDate:Date,
                              finishDate:Date,description:String) {


}
