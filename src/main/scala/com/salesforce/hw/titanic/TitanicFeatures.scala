/*
 * Copyright (c) 2018, Salesforce.com, Inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE.txt file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.hw.titanic

import java.io.Serializable

import com.salesforce.hw.titanic.TitanicFeatures._
import com.salesforce.op.features.FeatureBuilder
import com.salesforce.op.features.types._

trait TitanicFeatures extends Serializable {
  val survived = FeatureBuilder.RealNN[Passenger].extract(new Survived).asResponse
  val pClass = FeatureBuilder.PickList[Passenger].extract(new PClass).asPredictor
  val name = FeatureBuilder.Text[Passenger].extract(new Name).asPredictor
  val sex = FeatureBuilder.PickList[Passenger].extract(new Sex).asPredictor
  val age = FeatureBuilder.Real[Passenger].extract(new Age).asPredictor
  val sibSp = FeatureBuilder.PickList[Passenger].extract(new SibSp).asPredictor
  val parch = FeatureBuilder.PickList[Passenger].extract(new Parch).asPredictor
  val ticket = FeatureBuilder.PickList[Passenger].extract(new Ticket).asPredictor
  val fare = FeatureBuilder.Real[Passenger].extract(new Fare).asPredictor
  val cabin = FeatureBuilder.PickList[Passenger].extract(new Cabin).asPredictor
  val embarked = FeatureBuilder.PickList[Passenger].extract(new Embarked).asPredictor
}

object TitanicFeatures {
  abstract class TitanicFeatureFunc[T] extends Function[Passenger, T] with Serializable

  class RealExtract[T <: Real](f: Passenger => Option[Double], f1: Option[Double] => T) extends TitanicFeatureFunc[T] {
    override def apply(v1: Passenger): T = f1(f(v1))
  }

  class PickListExtract(f: Passenger => Option[_]) extends TitanicFeatureFunc[PickList] {
    override def apply(v1: Passenger): PickList = f(v1).map(_.toString).toPickList
  }

  class Survived extends RealExtract(p => Option(p.getSurvived).map(_.toDouble), _.get.toRealNN)

  class PClass extends PickListExtract(p => Option(p.getPclass))

  class Sex extends PickListExtract(p => Option(p.getSex))

  class SibSp extends PickListExtract(p => Option(p.getSibSp))

  class Parch extends PickListExtract(p => Option(p.getParch))

  class Ticket extends PickListExtract(p => Option(p.getTicket))

  class Embarked extends PickListExtract(p => Option(p.getEmbarked))

  class Cabin extends PickListExtract(p => Option(p.getCabin))

  class Name extends TitanicFeatureFunc[Text] with Serializable {
    override def apply(v1: Passenger): Text = Option(v1.getName).toText
  }

  class Age extends RealExtract(p => Option(Double.unbox(p.getAge)), _.toReal)

  class Fare extends RealExtract(p => Option(Double.unbox(p.getFare)), _.toReal)
}
