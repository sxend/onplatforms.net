package arimitsu.sf.platform.www.directive

import arimitsu.sf.platform.lib.directive.TemplateDirective

trait Directives extends AnyRef
    with TemplateDirective
    with AuthenticationDirective {

}

object Directives extends Directives
