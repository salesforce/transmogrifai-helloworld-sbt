/*
 * Copyright (c) 2018, Salesforce.com, Inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE.txt file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.hw.boston

import com.salesforce.hw.boston.BostonFeatures._
import com.salesforce.op.features.FeatureBuilder
import com.salesforce.op.features.types._

trait BostonFeatures extends Serializable {
  val rowId = FeatureBuilder.Integral[BostonHouse].extract(new RowId).asPredictor
  val crim = FeatureBuilder.RealNN[BostonHouse].extract(new Crim).asPredictor
  val zn = FeatureBuilder.RealNN[BostonHouse].extract(new Zn).asPredictor
  val indus = FeatureBuilder.RealNN[BostonHouse].extract(new Indus).asPredictor
  val chas = FeatureBuilder.PickList[BostonHouse].extract(new Chas).asPredictor
  val nox = FeatureBuilder.RealNN[BostonHouse].extract(new Nox).asPredictor
  val rm = FeatureBuilder.RealNN[BostonHouse].extract(new RM).asPredictor
  val age = FeatureBuilder.RealNN[BostonHouse].extract(new Age).asPredictor
  val dis = FeatureBuilder.RealNN[BostonHouse].extract(new Dis).asPredictor
  val rad = FeatureBuilder.Integral[BostonHouse].extract(new Rad).asPredictor
  val tax = FeatureBuilder.RealNN[BostonHouse].extract(new Tax).asPredictor
  val ptratio = FeatureBuilder.RealNN[BostonHouse].extract(new PTRatio).asPredictor
  val b = FeatureBuilder.RealNN[BostonHouse].extract(new B).asPredictor
  val lstat = FeatureBuilder.RealNN[BostonHouse].extract(new Lstat).asPredictor
  val medv = FeatureBuilder.RealNN[BostonHouse].extract(new Medv).asResponse
}

object BostonFeatures {

  abstract class BostonFeatureFunc[T] extends Function[BostonHouse, T] with Serializable

  class RealNNExtract(f: BostonHouse => Double) extends BostonFeatureFunc[RealNN] {
    override def apply(v1: BostonHouse): RealNN = f(v1).toRealNN
  }

  class IntegralExtract(f: BostonHouse => Int) extends BostonFeatureFunc[Integral] {
    override def apply(v1: BostonHouse): Integral = f(v1).toIntegral
  }

  class RowId extends IntegralExtract(_.rowId)

  class Rad extends IntegralExtract(_.rad)

  class Crim extends RealNNExtract(_.crim)

  class Zn extends RealNNExtract(_.zn)

  class Indus extends RealNNExtract(_.indus)

  class Nox extends RealNNExtract(_.nox)

  class RM extends RealNNExtract(_.rm)

  class Age extends RealNNExtract(_.age)

  class Dis extends RealNNExtract(_.dis)

  class Tax extends RealNNExtract(_.tax)

  class PTRatio extends RealNNExtract(_.ptratio)

  class B extends RealNNExtract(_.b)

  class Lstat extends RealNNExtract(_.lstat)

  class Medv extends RealNNExtract(_.medv)

  class Chas extends BostonFeatureFunc[PickList] {
    override def apply(v1: BostonHouse): PickList = Option(v1.chas).toPickList
  }
}
