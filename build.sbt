name := "transmogrify-helloworld"

scalaVersion := "2.11.12"

val transmogrifaiVersion = "0.3.4"

val sparkVersion = "2.2.1"

resolvers += Resolver.bintrayRepo("salesforce", "maven")

lazy val sparkDependencies = Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-mllib" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion
)

libraryDependencies += "com.salesforce.transmogrifai" %% "transmogrifai-core" % transmogrifaiVersion

libraryDependencies ++= sparkDependencies.map(_ % Provided)

libraryDependencies ++= sparkDependencies.map(_ % Test)

(version in avroConfig) := "1.7.7"

(stringType in avroConfig) := "String"
