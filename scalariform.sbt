import com.typesafe.sbt.SbtScalariform.ScalariformKeys
import scalariform.formatter.preferences._

ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(DoubleIndentClassDeclaration, true)
  .setPreference(DanglingCloseParenthesis, Preserve)
  .setPreference(AlignArguments, true)
  .setPreference(AlignParameters, true)
  .setPreference(SpacesAroundMultiImports, false)
  .setPreference(SpacesWithinPatternBinders, false)
