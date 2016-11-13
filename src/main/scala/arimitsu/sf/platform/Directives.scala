package arimitsu.sf.platform

import arimitsu.sf.platform.directive.{ AuthenticationDirective, TemplateDirective }

trait Directives extends AnyRef
    with TemplateDirective
    with AuthenticationDirective {

}

object Directives extends Directives

