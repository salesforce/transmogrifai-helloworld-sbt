import de.heikoseeberger.sbtheader.FileType

lazy val root = project.in(file(".")).enablePlugins(AutomateHeaderPlugin)

name := "transmogrify-helloworld"

scalaVersion := "2.11.12"

val transmogrifaiVersion = "0.7.0"

val sparkVersion = "2.4.7"

resolvers += Resolver.bintrayRepo("salesforce", "maven")

lazy val sparkDependencies = Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-mllib" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion
)

libraryDependencies += "com.salesforce.transmogrifai" %% "transmogrifai-core" % transmogrifaiVersion

libraryDependencies ++= sparkDependencies.map(_ % Provided)

libraryDependencies ++= sparkDependencies.map(_ % Test)

(version in avroConfig) := "1.8.2"

(stringType in avroConfig) := "String"

// license header stuff

organizationName := "Salesforce.com, Inc."

startYear := Some(2018)

licenses += "BSD-3-Clause" -> url("https://opensource.org/licenses/BSD-3-Clause")

headerMappings += FileType("html") -> HeaderCommentStyle.twirlStyleBlockComment

headerLicense := Some(
  HeaderLicense.Custom(
    """|Copyright (c) 2018, Salesforce.com, Inc.
       |All rights reserved.
       |SPDX-License-Identifier: BSD-3-Clause
       |For full license text, see the LICENSE.txt file in the repo root or https://opensource.org/licenses/BSD-3-Clause
       |""".stripMargin
  )
)

sparkSubmitMaster := {(_, _) => "local[*]"}
