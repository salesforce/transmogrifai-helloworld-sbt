/*
 * Copyright (c) 2018, Salesforce.com, Inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE.txt file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.hw.iris

import com.salesforce.op._
import com.salesforce.op.evaluators.Evaluators
import com.salesforce.op.readers.CustomReader
import com.salesforce.op.stages.impl.classification.MultiClassificationModelSelector
import com.salesforce.op.stages.impl.tuning.DataCutter
import com.salesforce.op.utils.kryo.OpKryoRegistrator
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Dataset, SparkSession}

/**
 * TransmogrifAI MultiClass Classification example on the Iris Dataset
 */
object OpIris extends OpAppWithRunner with IrisFeatures {

  override def kryoRegistrator: Class[_ <: OpKryoRegistrator] = classOf[IrisKryoRegistrator]

  ////////////////////////////////////////////////////////////////////////////////
  // READER DEFINITIONS
  /////////////////////////////////////////////////////////////////////////////////

  val randomSeed = 42

  val irisReader = new CustomReader[Iris](key = _.getID.toString){
    def readFn(params: OpParams)(implicit spark: SparkSession): Either[RDD[Iris], Dataset[Iris]] = {
      val path = getFinalReadPath(params)
      val myFile = spark.sparkContext.textFile(path)

      Left(myFile.filter(_.nonEmpty).zipWithIndex.map { case (x, number) =>
        val words = x.split(",")
        new Iris(number.toInt, words(0).toDouble, words(1).toDouble, words(2).toDouble, words(3).toDouble, words(4))
      })
    }
  }

  ////////////////////////////////////////////////////////////////////////////////
  // WORKFLOW DEFINITION
  /////////////////////////////////////////////////////////////////////////////////

  val labels = irisClass.indexed()

  val features = Seq(sepalLength, sepalWidth, petalLength, petalWidth).transmogrify()

  val cutter = DataCutter(reserveTestFraction = 0.2, seed = randomSeed)

  val prediction = MultiClassificationModelSelector
    .withCrossValidation(splitter = Option(cutter), seed = randomSeed)
    .setInput(labels, features).getOutput()

  val evaluator = Evaluators.MultiClassification.f1().setLabelCol(labels).setPredictionCol(prediction)

  val workflow = new OpWorkflow().setResultFeatures(prediction, labels)

  def runner(opParams: OpParams): OpWorkflowRunner =
    new OpWorkflowRunner(
      workflow = workflow,
      trainingReader = irisReader,
      scoringReader = irisReader,
      evaluationReader = Option(irisReader),
      evaluator = Option(evaluator),
      featureToComputeUpTo = Option(features)
    )
}
