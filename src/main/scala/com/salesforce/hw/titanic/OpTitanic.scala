/*
 * Copyright (c) 2018, Salesforce.com, Inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE.txt file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.hw.titanic

import com.salesforce.op._
import com.salesforce.op.evaluators.Evaluators
import com.salesforce.op.readers.DataReaders
import com.salesforce.op.stages.impl.classification._
import com.salesforce.op.stages.impl.tuning.DataSplitter
import com.salesforce.op.utils.kryo.OpKryoRegistrator
import org.apache.spark.ml.tuning.ParamGridBuilder

/**
 * TransmogrifAI example classification app using the Titanic dataset
 */
object OpTitanic extends OpAppWithRunner with TitanicFeatures {

  ////////////////////////////////////////////////////////////////////////////////
  // READER DEFINITION
  /////////////////////////////////////////////////////////////////////////////////

  val randomSeed = 112233L
  val simpleReader = DataReaders.Simple.csv[Passenger](
    schema = Passenger.getClassSchema.toString, key = _.getPassengerId.toString
  )

  ////////////////////////////////////////////////////////////////////////////////
  // WORKFLOW DEFINITION
  /////////////////////////////////////////////////////////////////////////////////

  // Automated feature engineering
  val featureVector = Seq(pClass, name, sex, age, sibSp, parch, ticket, cabin, embarked).transmogrify()

  // Automated feature selection
  val checkedFeatures = survived.sanityCheck(
    featureVector = featureVector, checkSample = 1.0, sampleSeed = randomSeed, removeBadFeatures = true
  )

  // Automated model selection
  val lr = new OpLogisticRegression()
  val rf = new OpRandomForestClassifier()
  val models = Seq(
    lr -> new ParamGridBuilder()
      .addGrid(lr.regParam, Array(0.05, 0.1))
      .addGrid(lr.elasticNetParam, Array(0.01))
      .build(),
    rf -> new ParamGridBuilder()
      .addGrid(rf.maxDepth, Array(5, 10))
      .addGrid(rf.minInstancesPerNode, Array(10, 20, 30))
      .addGrid(rf.seed, Array(randomSeed))
      .build()
  )
  val splitter = DataSplitter(seed = randomSeed, reserveTestFraction = 0.1)
  val prediction = BinaryClassificationModelSelector
    .withCrossValidation(splitter = Option(splitter), seed = randomSeed, modelsAndParameters = models)
    .setInput(survived, checkedFeatures)
    .getOutput()

  val workflow = new OpWorkflow().setResultFeatures(prediction)

  val evaluator = Evaluators.BinaryClassification.auPR().setLabelCol(survived).setPredictionCol(prediction)

  ////////////////////////////////////////////////////////////////////////////////
  // APPLICATION RUNNER DEFINITION
  /////////////////////////////////////////////////////////////////////////////////
  def runner(opParams: OpParams): OpWorkflowRunner =
    new OpWorkflowRunner(
      workflow = workflow,
      trainingReader = simpleReader,
      scoringReader = simpleReader,
      evaluationReader = Option(simpleReader),
      evaluator = Option(evaluator),
      featureToComputeUpTo = Option(featureVector)
    )

  override def kryoRegistrator: Class[_ <: OpKryoRegistrator] = classOf[TitanicKryoRegistrator]

}
