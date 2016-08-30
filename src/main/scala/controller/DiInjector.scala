package controller

import module.BindModule

/**
  * Serviceインスタンスを取得する為のtrait.
  * ControllerのUnitテスト時はvalを上書きします
  */
trait DiInjector {

  /** Injector. */
  val injector = BindModule.injector

}
