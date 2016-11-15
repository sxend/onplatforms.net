package arimitsu.sf.platform.www.directive

trait Directives extends AnyRef
    with TemplateDirective
    with AuthenticationDirective
    with SessionDirective {

}

object Directives extends Directives

