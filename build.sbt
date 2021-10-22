import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}
import _root_.io.chrisdavenport.sbtmimaversioncheck.MimaVersionCheckKeys.mimaVersionCheckExcludedVersions

val catsV = "2.6.1"
val catsEffectV = "3.2.9"

val scala213 = "2.13.6" 
ThisBuild / scalaVersion := scala213
ThisBuild / crossScalaVersions := Seq("2.12.15", scala213, "3.0.2")

lazy val `progressbar` = project.in(file("."))
  .disablePlugins(MimaPlugin)
  .enablePlugins(NoPublishPlugin)
  .aggregate(core.jvm, core.js)

lazy val core = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("core"))
  .settings(
    name := "progressbar",
    libraryDependencies ++= Seq(
      "org.typelevel"               %%% "cats-core"                  % catsV,
      "org.typelevel"               %%% "cats-effect"                % catsEffectV,
      // "org.jline"                    % "jline"                      % "3.21.0",
      "org.typelevel" %%% "munit-cats-effect-3" % "1.0.6" % Test,
    ),
  )
  .jsSettings(
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule)},
    scalaJSUseMainModuleInitializer := true,
    Compile / mainClass := Some("io.chrisdavenport.progressbar.Main"),
  )