package service

import com.google.inject.Inject
import model.{Staff, StaffDao}
import scalikejdbc.DBSession

/**
  * Staffに関するService.
  */
trait StaffService {

  @Inject
  private val staffDao:StaffDao = null

  /**
    * staff登録.
    * @param name 名称
    * @param session Session
    * @return 生成ID
    */
  def createStaff(name:String)(implicit session:DBSession) = {
    staffDao.create(Staff(id = -1L, staffName = name))
  }

  /***
    * Staff取得.
    * @param id ID
    * @return 該当データ(存在しない場合、None)
    */
  def getStaff(id:Long)(implicit session:DBSession) = {
    staffDao.findById(id)
  }
}

class StaffServiceImpl extends StaffService