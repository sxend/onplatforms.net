package arimitsu.sf.platform

import arimitsu.sf.platform.directive.{ AuthenticationDirective, SessionDirective, TemplateDirective }

trait Directives extends AnyRef
    with TemplateDirective
    with AuthenticationDirective
    with SessionDirective {

}

object Directives extends Directives

