/*
 * Copyright (c) 2018, Salesforce.com, Inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE.txt file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.hw.iris

import com.salesforce.op.features.FeatureBuilder
import com.salesforce.op.features.types._

trait IrisFeatures extends Serializable {
  val sepalLength = FeatureBuilder.Real[Iris].extract(_.sepalLength.toReal).asPredictor
  val sepalWidth = FeatureBuilder.Real[Iris].extract(_.sepalWidth.toReal).asPredictor
  val petalLength = FeatureBuilder.Real[Iris].extract(_.petalLength.toReal).asPredictor
  val petalWidth = FeatureBuilder.Real[Iris].extract(_.petalWidth.toReal).asPredictor
  val irisClass = FeatureBuilder.Text[Iris].extract(_.irisClass.toText).asResponse
}
