/*
 * Copyright (c) 2018, Salesforce.com, Inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE.txt file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.hw.iris

import com.salesforce.hw.iris.IrisFeatures._
import com.salesforce.op.features.FeatureBuilder
import com.salesforce.op.features.types._

trait IrisFeatures extends Serializable {
  val sepalLength = FeatureBuilder.Real[Iris].extract(new SepalLength).asPredictor
  val sepalWidth = FeatureBuilder.Real[Iris].extract(new SepalWidth).asPredictor
  val petalLength = FeatureBuilder.Real[Iris].extract(new PetalLength).asPredictor
  val petalWidth = FeatureBuilder.Real[Iris].extract(new PetalWidth).asPredictor
  val irisClass = FeatureBuilder.Text[Iris].extract(new IrisClass).asResponse
}

object IrisFeatures {
  abstract class IrisFeatureFunc[T] extends Function[Iris, T] with Serializable

  class RealExtract(f: Iris => Double) extends IrisFeatureFunc[Real] {
    override def apply(v1: Iris): Real = f(v1).toReal
  }

  class SepalLength extends RealExtract(_.sepalLength)

  class SepalWidth extends RealExtract(_.sepalWidth)

  class PetalLength extends RealExtract(_.petalLength)

  class PetalWidth extends RealExtract(_.petalWidth)

  class IrisClass extends IrisFeatureFunc[Text] {
    override def apply(v1: Iris): Text = v1.irisClass.toText
  }
}
