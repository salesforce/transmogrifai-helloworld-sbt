/*
 * Copyright (c) 2018, Salesforce.com, Inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE.txt file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.hw.titanic

import com.salesforce.op._
import com.salesforce.op.features._
import com.salesforce.op.features.types._
import com.salesforce.op.evaluators.Evaluators
import com.salesforce.op.readers.DataReaders
import com.salesforce.op.stages.impl.classification.ClassificationModelsToTry._
import com.salesforce.op.stages.impl.classification._
import com.salesforce.op.stages.impl.tuning.DataSplitter
import com.salesforce.op.utils.kryo.OpKryoRegistrator

/**
 * TransmogrifAI example classification app using the Titanic dataset
 */
object OpTitanic extends OpAppWithRunner with TitanicFeatures {

  ////////////////////////////////////////////////////////////////////////////////
  // READER DEFINITION
  /////////////////////////////////////////////////////////////////////////////////

  val randomSeed = 112233
  val simpleReader = DataReaders.Simple.csv[Passenger](
    schema = Passenger.getClassSchema.toString, key = _.getPassengerId.toString
  )

  ////////////////////////////////////////////////////////////////////////////////
  // WORKFLOW DEFINITION
  /////////////////////////////////////////////////////////////////////////////////

  val featureVector = Seq(pClass, name, sex, age, sibSp, parch, ticket, cabin, embarked).transmogrify()

  val checkedFeatures = survived.sanityCheck(
    featureVector = featureVector, checkSample = 1.0, sampleSeed = randomSeed, removeBadFeatures = true
  )

  val splitter = DataSplitter(seed = randomSeed, reserveTestFraction = 0.1)

  val (pred, raw, prob) = BinaryClassificationModelSelector
    .withCrossValidation(splitter = Option(splitter), seed = randomSeed)
    .setLogisticRegressionRegParam(0.05, 0.1)
    .setLogisticRegressionElasticNetParam(0.01)
    .setRandomForestMaxDepth(5, 10)
    .setRandomForestMinInstancesPerNode(10, 20, 30)
    .setRandomForestSeed(randomSeed)
    .setModelsToTry(LogisticRegression, RandomForest)
    .setInput(survived, checkedFeatures)
    .getOutput()

  val workflow = new OpWorkflow().setResultFeatures(pred, raw)

  val evaluator = Evaluators.BinaryClassification.auPR()
    .setLabelCol(survived)
    .setPredictionCol(pred)
    .setRawPredictionCol(raw)

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
