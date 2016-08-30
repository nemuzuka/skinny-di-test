package controller

import java.util.Date
import scalikejdbc.DB
import service.StaffService

class RootController extends ApplicationController with DiInjector {

  def index = {
    DB.localTx { implicit session =>

      //Serviceインスタンス取得 & 呼び出し
      val staffService = injector.getInstance(classOf[StaffService])
      staffService.createStaff("DIも良いもんだぜ！" + new Date())

      render("/root/index")
    }
  }

}
