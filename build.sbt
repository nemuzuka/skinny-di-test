import sbt._, Keys._
import skinny.scalate.ScalatePlugin._, ScalateKeys._
import skinny.servlet._, ServletPlugin._, ServletKeys._
import org.sbtidea.SbtIdeaPlugin._

import scala.language.postfixOps

// -------------------------------------------------------
// Common Settings
// -------------------------------------------------------

val appOrganization = "net.jp.vss"
val appName = "skinny-di-test-app"
val appVersion = "0.1.0-SNAPSHOT"

val skinnyVersion = "2.2.0"
val theScalaVersion = "2.11.8"
val jettyVersion = "9.2.17.v20160517"

lazy val baseSettings = servletSettings ++ Seq(
  organization := appOrganization,
  name         := appName,
  version      := appVersion,
  scalaVersion := theScalaVersion,
  dependencyOverrides := Set(
    "org.scala-lang"         %  "scala-library"            % scalaVersion.value,
    "org.scala-lang"         %  "scala-reflect"            % scalaVersion.value,
    "org.scala-lang"         %  "scala-compiler"           % scalaVersion.value,
    "org.scala-lang.modules" %% "scala-xml"                % "1.0.5",
    "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4",
    "org.slf4j"              %  "slf4j-api"                % "1.7.21"
  ),
  libraryDependencies ++= Seq(
    "org.skinny-framework"    %% "skinny-framework"     % skinnyVersion,
    "org.skinny-framework"    %% "skinny-assets"        % skinnyVersion,
    "org.skinny-framework"    %% "skinny-task"          % skinnyVersion,
    "org.skinny-framework"    %  "skinny-logback"       % "1.0.9",
    "org.postgresql"          %  "postgresql"           % "9.4-1206-jdbc42",
    "com.google.inject"       %  "guice"                % "4.1.0",
    "org.skinny-framework"    %% "skinny-factory-girl"  % skinnyVersion   % "test",
    "org.skinny-framework"    %% "skinny-test"          % skinnyVersion   % "test",
    "org.eclipse.jetty"       %  "jetty-webapp"         % jettyVersion    % "container",
    "org.eclipse.jetty"       %  "jetty-plus"           % jettyVersion    % "container",
    "javax.servlet"           %  "javax.servlet-api"    % "3.1.0"         % "container;provided;test"
  ),
  // ------------------------------
  // for ./skinnny console
  initialCommands := """
import skinny._
import _root_.controller._, model._
import org.joda.time._
import scalikejdbc._, config._
DBSettings.initialize()
""",
  resolvers ++= Seq(
    "sonatype releases"  at "https://oss.sonatype.org/content/repositories/releases"
    //, "sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
  ),
  // Faster "./skinny idea"
  transitiveClassifiers in Global := Seq(Artifact.SourceClassifier),
  // the name-hashing algorithm for the incremental compiler.
  incOptions := incOptions.value.withNameHashing(true),
  updateOptions := updateOptions.value.withCachedResolution(true),
  logBuffered in Test := false,
  javaOptions in Test ++= Seq("-Dskinny.env=test"),
  fork in Test := true,
  scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature"),
  ideaExcludeFolders := Seq(".idea", ".idea_modules", "db", "target", "task/target", "build", "standalone-build", "node_modules")
)
// ------------------------------
// Automated code formatter before compilaion
// Disabled by default because this is confusing for beginners
// ++ scalariformSettings

lazy val scalatePrecompileSettings = scalateSettings ++ Seq(
  scalateTemplateConfig in Compile <<= (sourceDirectory in Compile){ base =>
    Seq( TemplateConfig(file(".") / "src" / "main" / "webapp" / "WEB-INF",
      // These imports should be same as src/main/scala/templates/ScalatePackage.scala
      Seq("import controller._", "import model._"),
      Seq(Binding("context", "_root_.skinny.micro.contrib.scalate.SkinnyScalateRenderContext", importMembers = true, isImplicit = true)),
      Some("templates")))
  }
)

// -------------------------------------------------------
// Development
// -------------------------------------------------------

lazy val devBaseSettings = baseSettings ++ Seq(
  unmanagedClasspath in Test <+= (baseDirectory) map { bd =>  Attributed.blank(bd / "src/main/webapp") },
  // Integration tests become slower when multiple controller tests are loaded in the same time
  parallelExecution in Test := false,
  port in container.Configuration := 8080
)
lazy val dev = (project in file(".")).settings(devBaseSettings).settings(
  name := appName + "-dev",
  target := baseDirectory.value / "target" / "dev"
)

// --------------------------------------------------------
// Enable this sub project when you'd like to use --precompile option
// --------------------------------------------------------
/*
lazy val precompileDev = (project in file(".")).settings(devBaseSettings, scalatePrecompileSettings).settings(
  name := appName + "-precompile-dev",
  target := baseDirectory.value / "target" / "precompile-dev",
  ideaIgnoreModule := true
)
*/

// -------------------------------------------------------
// Task Runner
// -------------------------------------------------------

lazy val task = (project in file("task")).settings(baseSettings).settings(
  mainClass := Some("TaskRunner"),
  name := appName + "-task",
  libraryDependencies += "javax.servlet" % "javax.servlet-api" % "3.1.0"
) dependsOn(dev)

// -------------------------------------------------------
// Packaging
// -------------------------------------------------------

lazy val packagingBaseSettings = baseSettings ++ scalatePrecompileSettings ++ Seq(
  sources in doc in Compile := List(),
  publishTo <<= version { (v: String) =>
    val base = "https://oss.sonatype.org/"
    if (v.trim.endsWith("SNAPSHOT")) Some("snapshots" at base + "content/repositories/snapshots")
    else Some("releases" at base + "service/local/staging/deploy/maven2")
  }
)
lazy val build = (project in file("build")).settings(packagingBaseSettings).settings(
  name := appName,
  ideaIgnoreModule := true
)
lazy val standaloneBuild = (project in file("standalone-build")).settings(packagingBaseSettings).settings(
  name := appName + "-standalone",
  libraryDependencies += "org.skinny-framework" %% "skinny-standalone" % skinnyVersion,
  ideaIgnoreModule := true,
  ivyXML := <dependencies><exclude org="org.eclipse.jetty.orbit" /></dependencies>
)
