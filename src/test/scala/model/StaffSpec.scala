package model

import com.google.inject.Inject
import org.scalatest._
import scalikejdbc.scalatest._
import skinny._
import _root_.module.BindModule

/***
  * DIを使用したDaoのテスト.
  * Daoのテストは本物のインスタンスを使用する
  */
class StaffSpec extends fixture.FunSpec with AutoRollback with Matchers with DBSettings with BeforeAndAfter {

  @Inject
  private val staffDao:StaffDao = null

  before {
    //@Injectアノテーションが付与されているtraitに対してインスタンス割当
    BindModule.injector.injectMembers(this)
  }

  describe("create") {
    it("社員を作成し、更新して、削除するテスト") { implicit session =>

      val createdId = staffDao.create(Staff(id=1L, staffName = "とーろく社員！！"))
      staffDao.findById(createdId).get.staffName should be ("とーろく社員！！")

      val staffDi = Staff(id=createdId, staffName = "更新後社員名！！")
      staffDao.update(staffDi)

      staffDao.findById(createdId).get.staffName should be ("更新後社員名！！")

      staffDao.deleteById(createdId)
      staffDao.findById(createdId) should be (None)
    }
  }
}
