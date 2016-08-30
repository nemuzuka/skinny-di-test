package controller

import java.util.Date
import scalikejdbc.DB
import service.StaffService

class RootController extends ApplicationController with DiInjector {

  def index = {
    DB.localTx { implicit session =>

      //Serviceインスタンス取得 & 呼び出し
      val staffService = injector.getInstance(classOf[StaffService])
      val id = staffService.createStaff("DIも良いもんだぜ！" + new Date())
      println(id)

      render("/root/index")
    }
  }

}
