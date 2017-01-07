package net.onplatforms.lib.directive

import com.typesafe.config.Config

trait Lib {
  private[lib] val namespace = "net.onplatforms.lib"
  private[lib] def withNamespace(suffix: String) = s"$namespace.$suffix"
  private[lib] def getConfigInNamespace(suffix: String)(
    implicit
    env: {
      val config: Config
    }) = env.config.getConfig(withNamespace(suffix))
}
