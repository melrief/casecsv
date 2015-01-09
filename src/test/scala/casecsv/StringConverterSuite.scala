/*
* Copyright (c) 2015 Mario Pastorelli (pastorelli.mario@gmail.com)
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package casecsv

import org.scalatest.FunSuite
import org.scalatest.Matchers._

import casecsv._
 

class SimpleSuite extends FunSuite {
  def testConverter[A](desc: String)(value: A, valueString: String)
                      (implicit conv: StringConverter[A]) = {
    test(desc ++ " '" ++ valueString ++ "' <-> " ++ value.toString) {
      conv.from(valueString) should be (Right(value))
      conv.to(value) should be (valueString)
    }
  }

  def testsFromConverter[A](desc: String)
                           (valuesAndStrings: (String,A)*)
                           (implicit conv: StringConverter[A]) = {
    valuesAndStrings.foreach { case (valueString, value) =>
      test(desc ++ " '" ++ valueString ++ "' -> " ++ value.toString ) {
        conv.from(valueString) should be (Right(value))
      }
    }
  }
}

class ReadSuite extends SimpleSuite {
  testsFromConverter("Boolean is instance of StringConverter") ("1"     -> true,
                                                                "true"  -> true,
                                                                "True"  -> true,
                                                                "TRUE"  -> true,
                                                                "0"     -> false,
                                                                "false" -> false,
                                                                "False" -> false,
                                                                "FALSE" -> false)
  testConverter("Boolean is instance of StringConverter")(true, "true")
  testConverter("Boolean is instance of StringConverter")(false, "false")
  testConverter("Double is instance of StringConverter") (1.1,   "1.1")
  testConverter("Float is instance of StringConverter")  (1.1f,  "1.1")
  testConverter("Int is instance of StringConverter")    (1,     "1")
  testConverter("Long is instance of StringConverter")   (1l,     "1")
  testConverter("String is instance of StringConverter") ("foo", "foo")
  testConverter[Option[Int]]("Option is instance of StringConverter") (Some(1),"1")
  testConverter[Option[Int]]("Option is instance of StringConverter") (None,"")
}
