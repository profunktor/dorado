# munit-golden

Generic golden testing library based on [MUnit](https://scalameta.org/munit/).

## Goal

The main idea behind golden tests is to make it as explicitly as possible when we are breaking the JSON protocol, either by changing or deleting a field, or by adding new event or removing an existing one.

So the goal is to keep it simple and have meaningful diffs when submitting a PR that breaks the protocol one way or another.

## Dependencies

*NOTE: Not yet published to Maven Central*

```scala
libraryDependencies += "dev.profunktor" %% "munit-golden-core" % Version
libraryDependencies += "dev.profunktor" %% "munit-golden-circe" % Version
```

## About

This is the most minimal library that supports golden testing. As such, it expects a few things from the user.

Let's say we have the following ADT, namely `Event`, as demonstrated under `modules/examples`.

```scala
sealed trait Event
object Event {
  @newtype case class Id(value: UUID)
  @newtype case class Timestamp(value: Instant)

  final case class One(id: Id, foo: String, createdAt: Timestamp) extends Event
  final case class Two(id: Id, bar: Int, createdAt: Timestamp) extends Event

  implicit val jsonEncoder: Encoder[Event] = deriveEncoder
  implicit val jsonDecoder: Decoder[Event] = deriveDecoder
}
```

In this case, we use Circe's semi-automatic derivation for our JSON instances.

So, to add the first tests, we need to create `json` files for each event's format, under the `test/resources/` folder. We can create sub-directories to organize things a little better. For example, we would create an `event` folder and the files `One.json` and `Two.json` within it.

```
.
├── resources
│   └── event
│       ├── One.json
│       └── Two.json
```

In our example, we can generate the JSON from a simple example, or even from Scalacheck's generators, if we want to. That's up to the user. In this case, we have created the following content for such JSON files.

This is `One.json`.

```json
{
  "One" : {
    "id" : "a1651f08-72f2-4264-85be-6ee6b1b77e6c",
    "foo" : "test1",
    "createdAt" : "2020-10-25T09:27:26.270734Z"
  }
}
```

And this is `Two.json`.

```json
{
  "Two" : {
    "id" : "a1651f08-72f2-4264-85be-6ee6b1b77e6c",
    "bar" : 123,
    "createdAt" : "2020-10-25T09:27:26.270734Z"
  }
}
```

Then all we have to do to create a "roundtrip JSON conversion test", which will read the existing files and try to decode it using the existing decoders, is the following.

```scala
import munit.golden.circe.CirceGoldenSuite

class EventGoldenSuite extends CirceGoldenSuite[Event]("/event")
```

This is the output, in case of success.

```scala
sbt:examples> testdev.profunktor.golden.EventGoldenSuite:
  + dev.profunktor.golden.Event roundtrip conversion 0.18s
[info] Passed: Total 1, Failed 0, Errors 0, Passed 1
```

In case of failure, you might see something like this.

```scala
sbt:examples> test
dev.profunktor.golden.EventGoldenSuite:
==> X dev.profunktor.golden.EventGoldenSuite.dev.profunktor.golden.Event roundtrip conversion  0.165s munit.FailException: /home/gvolpe/workspace/golden/modules/core/src/main/scala/munit/golden/GoldenSuite.scala:42 Error: Attempt to decode value on failed cursor: DownField(foo),DownField(One). Input: {"One":{"id":"a1651f08-72f2-4264-85be-6ee6b1b77e6c","fo":"test1","createdAt":"2020-10-25T09:27:26.270734Z"}}
41:        jsonDecoder(json) match {
42:          case Left(e)  => fail(e)
43:          case Right(e) => assertEquals(jsonEncoder(e), json)
```

The `CirceGoldenSuite` is a convenient modules you can use by adding `munit-golden-circe` to your dependencies, though, we could use `GoldenSuite` directly.

For now, there is only a Circe module supported out of the box but `GoldenSuite` abstracts over any JSON library. All we need is to `extends GoldenSuite` and implement the following two methods.

```scala
def jsonDecoder: String => Either[String, A]
def jsonEncoder: A => String
```
