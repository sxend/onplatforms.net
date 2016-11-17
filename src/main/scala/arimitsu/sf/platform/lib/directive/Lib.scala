package arimitsu.sf.platform.lib.directive

import com.typesafe.config.Config

trait Lib {
  private[lib] val namespace = "arimitsu.sf.platform.lib"
  private[lib] def withNamespace(suffix: String) = s"$namespace.$suffix"
  private[lib] def getConfigInNamespace(suffix: String)(implicit env: {
    val config: Config
  }) = env.config.getConfig(withNamespace(suffix))
}
