ThisBuild / scalaVersion := "2.13.18"

ThisBuild / classLoaderLayeringStrategy := ClassLoaderLayeringStrategy.Flat

lazy val root = (project in file("."))
  .settings(
    name := "spark-scala-demo",
    version := "0.1.0",

    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-sql"  % "3.5.1"
      // spark-core est déjà transitif via spark-sql (tu peux le laisser si tu veux, mais inutile)
    ),

    Compile / run / fork := true,
    Compile / run / javaOptions ++= Seq(
      "--add-exports=java.base/sun.nio.ch=ALL-UNNAMED",
      "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED",
      "-Xmx8g",  // Mémoire maximale: 8GB (ajustez selon vos besoins)
      "-Xms2g"   // Mémoire initiale: 2GB
    )
  )
