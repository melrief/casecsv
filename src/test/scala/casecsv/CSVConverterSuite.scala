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
import shapeless._

import casecsv._
import casecsv.CSVConverter.lineSep


class CSVConverterSuite extends FunSuite {


  // HList

  test("CSVConverter for HNil") {
    val conv = CSVConverter[HNil]
    conv.from("") should be (Right(HNil))
    conv.to(HNil) should be ("")
    conv.fromLines(Seq("", "")).toSeq should be(Seq(Right(HNil), Right(HNil)))
    conv.toLines(Seq(HNil, HNil)) should be (lineSep)
  }

  test("CSVConverter for Int :: HNil") {
    val conv = CSVConverter[Int :: HNil]
    conv.from("1") should be (Right(1 :: HNil))
    conv.to(1 :: HNil) should be ("1")
    conv.fromLines(Seq("1","2")).toSeq should be(Seq(Right(1 :: HNil), Right(2 :: HNil)))
    conv.toLines(Seq(1 :: HNil, 2 :: HNil)) should be ("1" ++ lineSep ++ "2")
  }

  test("CSVConverter for Int :: String :: Float :: HNil") {
    val conv = CSVConverter[Int :: String :: Float :: HNil]
    conv.from("1,foo,2.3") should be (Right(1 :: "foo" :: 2.3f :: HNil))
    conv.to(1 :: "foo" :: 2.3f :: HNil) should be ("1,foo,2.3")
    val vals = Seq(1 :: "foo" :: 2.3f :: HNil, 4 :: "bar" :: 5.6f :: HNil)
    conv.fromLines(Seq("1,foo,2.3","4,bar,5.6")).toSeq should be(vals.map(x => Right(x)))
    conv.toLines(vals) should be ("1,foo,2.3" ++ lineSep ++ "4,bar,5.6")
  }


  // case classes

  case class Foo(i: Int, s: String, d: Double)

  test("CSVConverter for case class (Int, String, Double)") {
    val conv = CSVConverter[Foo]
    conv.from("1,foo,2.3") should be (Right(Foo(1,"foo",2.3d)))
    conv.to(Foo(1,"foo",2.3d)) should be ("1,foo,2.3")
    val vals = Seq(Foo(1, "foo", 2.3d), Foo(4, "bar", 5.6d))
    conv.fromLines(Seq("1,foo,2.3", "4,bar,5.6")).toSeq should be (vals.map(x => Right(x)))
    conv.toLines(vals) should be ("1,foo,2.3" ++ lineSep ++ "4,bar,5.6")
  }
}
