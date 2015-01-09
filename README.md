# CaseCSV

This project aims to simplify and automatize the conversion of case classes and
heterogeneous lists from/to CSV. CaseCSV is a small library based on
[shapeless](https://github.com/milessabin/shapeless) and the idea is to keep
it small and easy to integrate in other projects.
To use it, import the `CSVConverter` and create one for your case class (or
[HList](#hlist)):

```scala
import casecsv.CSVConverter

case class Person(name: String, age: Int)

object Main extends App {
  val peopleSerialized = """john,19
smith,26
carl,20"""

  CSVConverter[Person].fromLines(peopleSerialized.split("\n")).foreach(println)
}
```

This will print

```scala
Right(Person(john, 19))
Right(Person(smith, 26))
Right(Person(carl, 20))
```


## Serialization

To convert one value to CSV call the `to` method

```scala
> CSVConverter[Person].to(Person("john", 19))
res0: String = john,19
```

To convert multiple values to CSV call the `toLines` method

```scala
> CSVconverter[Person].toLines(Seq(Person("smith", 26), Person("carl", 20)))
res1: String =
smith,26
carl,20
```

To write the converted lines to file call the `toFile` method

```scala
CSVConverter[Person].toFile(filePath, Seq(Person("smith", 26), Person("carl", 20)))
```


## De-serialization

To convert a CSV String to a value call the `from` method

```scala
> CSVConverter[Person].from("john,19")
res0: Result[Person] = Right(Person(john, 19))
```

The result of de-serialization is a `Result`, that is an alias
for `Either[String,A]` where `A` is the type of the value to de-serialize. `Left`
values are errors while `Right` are successfully de-serialized values.

To convert a list of CSV lines to values, call the `fromLines` method

```scala
> CSVConverter[Person].fromLines(Seq("john","smith,25")).foreach(println)
Left(java.lang.NumberFormatException: For input string: "")
Right(Person(smith,25))
```

To read a file into values call the `fromFile` method

```scala
CSVConverter[Person].fromFile(filePath)
```


## Separator

Every method has an optional parameter to specify the separator character

```scala
> CSVConverter[Person].from("john|19", '|')
res0: Result[Person] = Right(Person(john, 19))
```


## HList<a name="hlist"></a>

It is possible to work with heterogeneous lists instead of case classes with
casecsv. The API is exactly the same:

```scala
> CSVConverter[Int :: String :: HNil].from("1,test")
Right(1 :: "test" :: HNil)
```


## Extend the Library

The library is composed by two traits: the `StringConverter`, that converts a
value from/to a string, and the `CSVConverter`, that converts value a from/to a
CSV String. `StringConverter` is used for base types, such as `Float` and
`Boolean`, and `CSVConverter` uses `StringConver` for serialized and
de-serialized the fields of case classes or hlists.

The `StringConverter` can be extended with new types for, for example, supporting
new field types. Let's do an example: we want to be able to de-serialize the `Foo`
datatype

```scala
case class Foo(i: Int)
```

We add the `StringConverter` for it with

```scala
import casecsv.StringConverter

implicit def fooStringConverter = new StringConverter[Foo] {
  def from(s: String): Result[Foo] = tryOn(Foo(s.toInt))
  def to(f: Foo): String = f.i.toString
}
```

and that's it. We can then use `Foo` as field for our case class or hlist

```scala
> case class MyData(s: String, f: Foo)
> import casecsv.CSVConverter
> CSVConverter[MyData].from("foo,1")
Right(MyData("foo", 1))
```

More examples can be found in the [StringConverter Object](src/main/scala/casecsv/StringConverter.scala).


## Contributing

We accept contribution via pull requests.


## License

This library is provided under the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt).
