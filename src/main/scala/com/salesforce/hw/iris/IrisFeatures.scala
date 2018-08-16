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
  val id = FeatureBuilder.Integral[Iris].extract(_.getID.toIntegral).asPredictor
  val sepalLength = FeatureBuilder.Real[Iris].extract(_.getSepalLength.toReal).asPredictor
  val sepalWidth = FeatureBuilder.Real[Iris].extract(_.getSepalWidth.toReal).asPredictor
  val petalLength = FeatureBuilder.Real[Iris].extract(_.getPetalLength.toReal).asPredictor
  val petalWidth = FeatureBuilder.Real[Iris].extract(_.getPetalWidth.toReal).asPredictor
  val irisClass = FeatureBuilder.Text[Iris].extract(_.getClass$.toText).asResponse
}
