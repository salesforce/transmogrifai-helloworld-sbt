/*
 * Copyright (c) 2018, Salesforce.com, Inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE.txt file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.hw.titanic

import com.esotericsoftware.kryo.Kryo
import com.salesforce.op.utils.kryo.OpKryoRegistrator


class TitanicKryoRegistrator extends OpKryoRegistrator {

  override def registerCustomClasses(kryo: Kryo): Unit = {
    doAvroRegistration[com.salesforce.hw.titanic.Passenger](kryo)
  }

}
