package service

import com.google.inject._
import com.google.inject.util._
import org.scalatest.{fixture, _}
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import scalikejdbc.scalatest._
import skinny._
import _root_.module.BindModule
import model.{Staff, StaffDao}

/**
  * StaffServiceの依存するDaoをMockで差し替えてテストするパターン.
  */
class StaffServiceSpec extends fixture.FunSpec with AutoRollback with Matchers with DBSettings with BeforeAndAfter with MockitoSugar {

  @Inject
  private val staffService:StaffService = null

  //前処理
  before {
    //差し替えたMockで設定する
    Guice.createInjector(
      Modules.`override`(new BindModule()).`with`(createTestModule())
    ).injectMembers(this)
  }

  /**
    * MockのDaoを呼ぶパターン
    */
  describe("getStaff") {
    it("DB取得") { implicit session =>
      val actual = staffService.getStaff(2L).get
      actual.id should be (2L)
      actual.staffName should be ("Mock社員ン！")
      staffService.getStaff(1L) should be (None)
    }
  }

  /**
    * DI定義上書き.
    * @return Module
    */
  private def createTestModule() = new AbstractModule() {
    override def configure() = {
      //Mockの定義
      val m = mock[StaffDao]
      when(m.findById(org.mockito.Matchers.eq(1L))(org.mockito.Matchers.anyObject())).thenReturn(None)
      when(m.findById(org.mockito.Matchers.eq(2L))(org.mockito.Matchers.anyObject())).thenReturn(Option(
        Staff(id=2L, staffName="Mock社員ン！")
      ))

      bind(classOf[StaffDao]).toInstance(m)
    }
  }

}
