/*
 * Copyright (c) 2018, Salesforce.com, Inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE.txt file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.hw.iris

import com.salesforce.op._
import com.salesforce.op.evaluators.Evaluators
import com.salesforce.op.readers.DataReaders
import com.salesforce.op.stages.impl.classification.MultiClassificationModelSelector
import com.salesforce.op.stages.impl.tuning.DataCutter
import org.apache.spark.sql.Encoders

/**
 * TransmogrifAI MultiClass Classification example on the Iris Dataset
 */
object OpIris extends OpAppWithRunner with IrisFeatures {

  implicit val irisEncoder = Encoders.product[Iris]

  ////////////////////////////////////////////////////////////////////////////////
  // READER DEFINITIONS
  /////////////////////////////////////////////////////////////////////////////////


  val irisReader = DataReaders.Simple.csvCase[Iris]()

  ////////////////////////////////////////////////////////////////////////////////
  // WORKFLOW DEFINITION
  /////////////////////////////////////////////////////////////////////////////////

  val labels = irisClass.indexed()

  val features = Seq(sepalLength, sepalWidth, petalLength, petalWidth).transmogrify()

  val randomSeed = 42L

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
