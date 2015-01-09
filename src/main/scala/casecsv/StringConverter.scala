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

import casecsv.result._


trait StringConverter[A] {
  def from(s: String): Result[A]
  def to(a: A): String
}

object StringConverter {

  def apply[A](implicit conv: StringConverter[A]): StringConverter[A] = conv

  def baseConverter[A](fromF: String => Result[A], toF: A => String) = new StringConverter[A] {
    def from(s: String): Result[A] = fromF(s)
    def to(a: A): String = toF(a)
  }

  implicit val bst = new StringConverter[Boolean] {
    def from(s: String): Result[Boolean] = s match {
      case "1" | "true" | "True" | "TRUE" => success(true)
      case "0" | "false" | "False" | "FALSE" => success(false)
      case _ => error("'" ++ s ++ "' is not a valid boolean")
    }
    def to(b: Boolean): String = b.toString
  }

  implicit val dst: StringConverter[Double] = baseConverter(s => tryOn(s.toDouble), _.toString)
  implicit val fst: StringConverter[Float] = baseConverter(s => tryOn(s.toFloat), _.toString)
  implicit val isc: StringConverter[Int] = baseConverter(s => tryOn(s.toInt), _.toString)
  implicit val lsc: StringConverter[Long] = baseConverter(s => tryOn(s.toLong), _.toString)
  implicit val ssc: StringConverter[String] = baseConverter(success, identity)

  implicit def osc[A](implicit conv: StringConverter[A]) = new StringConverter[Option[A]] {
    def from(s: String): Result[Option[A]] = s match {
      case "" => success(None)
      case ne => conv.from(ne).right.map(Some(_))
    }
    def to(o: Option[A]): String = o match {
      case None => ""
      case Some(a) => conv.to(a)
    }
  }
}
