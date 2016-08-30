package module

import com.google.inject._
import model._
import service._

/**
  * DI設定Module.
  */
class BindModule extends AbstractModule{
  /**
    * DI設定定義
    */
  override def configure() = {
    //Serviceはclassなのでto、Singleton
    bind(classOf[StaffService]).to(classOf[StaffServiceImpl]).in(classOf[Singleton])

    //Daoは、objectなので、インスタンスを設定
    bind(classOf[StaffDao]).toInstance(Staff)
  }
}

/**
  * DI設定インスタンス管理.
  */
object BindModule {
  val injector = Guice.createInjector(new BindModule())
}
