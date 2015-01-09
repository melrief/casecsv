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

import shapeless._

import java.io.{FileWriter, PrintWriter}

import casecsv.result._


trait CSVConverter[A] {
  def from(s: String, sep: Char = ','): Result[A]
  def to(a: A, sep: Char = ','): String

  def fromLines(lines: TraversableOnce[String], sep: Char = ',')
      : TraversableOnce[Result[A]] = {
    lines.map(x => from(x, sep))
  }

  def toLines(vals: TraversableOnce[A], sep: Char = ',')
      : String = {
    vals.map(x => to(x, sep)).mkString(CSVConverter.lineSep)
  }

  def fromFile(filePath: String, sep: Char = ','): Iterator[Result[A]] = {
    io.Source.fromFile(filePath).getLines.map(x => from(x, sep))
  }

  def toFile(filePath: String, vals: TraversableOnce[A], sep: Char = ',')
      : Unit = {
    val writer = new PrintWriter(new FileWriter(filePath))
    vals.map(x => to(x, sep)).foreach(writer.println)
    writer.close()
  }
}

object CSVConverter {
  val lineSep = System.getProperty("line.separator")

  def apply[A](implicit conv: CSVConverter[A]): CSVConverter[A] = conv


  // HList
  
  implicit def deriveHNil: CSVConverter[HNil] =
    new CSVConverter[HNil] {
      def from(s: String, sep: Char = ','): Result[HNil] = s match {
        case "" => success(HNil)
        case s => error("Cannot convert '" ++ s ++ "' to HNil")
      }
      def to(n: HNil, sep: Char = ',') = ""
    }
  
  implicit def deriveHCons[V, T <: HList](implicit cv: StringConverter[V], ct: CSVConverter[T])
        : CSVConverter[V :: T] = new CSVConverter[V :: T] {

        def from(s: String, sep: Char = ','): Result[V :: T] = {
          val (before, after) = s.span(_ != sep)
          for {
            front <- cv.from(before).right
            back <- ct.from(if (after.isEmpty) after else after.tail, sep).right
          } yield front :: back
        }

        def to(ft: V :: T, sep: Char = ','): String = {
          val hv = cv.to(ft.head)
          ct.to(ft.tail) match {
            case "" => hv
            case tv => hv ++ "," ++ tv
          }
        }
      }


  // case class

  implicit def deriveClass[A,R](implicit gen: Generic.Aux[A,R], conv: CSVConverter[R])
      : CSVConverter[A] = new CSVConverter[A] {
    
    def from(s: String, sep: Char = ','): Result[A] = {
      conv.from(s, sep).right.map(gen.from)
    }

    def to(a: A, sep: Char = ','): String = conv.to(gen.to(a), sep)
  }
}
