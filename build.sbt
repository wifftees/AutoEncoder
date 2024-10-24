import Dependencies.*
// import sbt.addCompilerPlugin

ThisBuild / scalaVersion := "3.3.1"
ThisBuild / version      := "0.1.0-SNAPSHOT"

// addCompilerPlugin("org.typelevel" % "kind-projector"     % "0.13.3" cross CrossVersion.full)
// addCompilerPlugin("com.olegpy"   %% "better-monadic-for" % "0.3.1")

scalacOptions ++= List(
  "-deprecation",
  "-encoding",
  "utf-8",
  "-explaintypes",
  "-feature",
  "-language:implicitConversions",
  "-unchecked",
)

lazy val root = (project in file("."))
  .settings(
    name := "hw9",
    libraryDependencies ++= List(
      catsEffect,
      shapeless,
      scalaTest  % Test,
      catsTest   % Test,
      scalaCheck % Test
    ),
    coverageEnabled                 := true,
    coverageFailOnMinimum           := true,
    coverageMinimumStmtTotal        := 70,
    coverageMinimumBranchTotal      := 70,
    coverageMinimumStmtPerPackage   := 70,
    coverageMinimumBranchPerPackage := 65,
    coverageMinimumStmtPerFile      := 65,
    coverageMinimumBranchPerFile    := 60
  )
