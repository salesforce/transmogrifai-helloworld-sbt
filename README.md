TransmogrifAI Hello World for SBT
---------------------------------

First, [Download Spark 2.3.2](https://spark.apache.org/downloads.html)

Run TitanicSimple:

`SPARK_HOME=your_spark_home_dir ./sbt "sparkSubmit --class com.salesforce.hw.OpTitanicSimple -- /full-path-to-project/src/main/resources/TitanicDataset/TitanicPassengersTrainData.csv"`
