package controller

import com.google.inject.{AbstractModule, Guice}
import com.google.inject.util.Modules
import module.BindModule
import org.scalatest._
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import org.mockito.Matchers._
import service.StaffService
import skinny._
import skinny.test.MockController

class RootControllerSpec extends FunSpec with Matchers with MockitoSugar with DBSettings {

  describe("RootController") {
    it("shows top page") {
      val controller = new RootController with MockController with MockDiInjector
      controller.index
      controller.contentType should equal("text/html; charset=utf-8")
      controller.renderCall.map(_.path) should equal(Some("/root/index"))
    }
  }

  /** Mock使用時のInjectorとして上書きする. */
  trait MockDiInjector extends DiInjector {
    override val injector = Guice.createInjector(
      Modules.`override`(new BindModule()).`with`(createTestModule())
    )
  }

  /**
    * Mock用DI定義上書き.
    * @return Module
    */
  private def createTestModule() = new AbstractModule() {
    override def configure() = {
      //Mockの定義
      val m = mock[StaffService]
      when(m.createStaff(anyString())(anyObject())).thenReturn(123456L)
      bind(classOf[StaffService]).toInstance(m)
    }
  }
}
