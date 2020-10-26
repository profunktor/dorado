# munit-golden

[![CI Status](https://github.com/profunktor/munit-golden/workflows/Scala/badge.svg)](https://github.com/profunktor/munit-golden/actions)
[![MergifyStatus](https://img.shields.io/endpoint.svg?url=https://gh.mergify.io/badges/profunktor/munit-golden&style=flat)](https://mergify.io)
[![Maven Central](https://img.shields.io/maven-central/v/dev.profunktor/munit-golden-core_2.13.svg)](https://search.maven.org/search?q=dev.profunktor.munit-golden)
[![Scala Steward badge](https://img.shields.io/badge/Scala_Steward-helping-brightgreen.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)
<a href="https://typelevel.org/cats/"><img src="https://raw.githubusercontent.com/typelevel/cats/master/docs/src/main/resources/microsite/img/cats-badge.svg" height="40px" align="right" alt="Cats friendly" /></a>

Generic golden testing library based on [MUnit](https://scalameta.org/munit/).

## Goal

The main idea behind golden tests is to make it as explicitly as possible when we are breaking the JSON protocol, either by changing or deleting a field, or by adding new event or removing an existing one.

Ideally, we would achieve this goal by keeping it simple and have meaningful diffs when submitting a PR that breaks the protocol one way or another.

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
{"Two":{"id" :"a1651f08-72f2-4264-85be-6ee6b1b77e6c","bar":123,"createdAt":"2020-10-25T09:27:26.270734Z"}}
```

Yes! As you can see, the format does not matter, as long as the JSON is valid and can be decoded to our existing datatype.

All we have to do next to create a *roundtrip JSON conversion test* is the following.

```scala
import munit.golden.circe.CirceGoldenSuite

class EventGoldenSuite extends CirceGoldenSuite[Event]("/event")
```

It will read all the JSON files under `test/resources/event/`, it will try to parse every one of them with the decoder for `Event`, and it will finally compare the decoded values against the original inputs (disregarding formatting) to make valuable tests.

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

`GoldenSuite` abstracts over any JSON library. All we need is to `extends GoldenSuite` and implement the following methods.

```scala
def jsonDecoder: String => Either[String, A]
def jsonEncoder: A => String

/**
  * The path of the directory under the test/resources folder.
 **/
def path: String
```

Adding new modules for other JSON libraries would be really easy, PRs welcome!

## Similar libraries

AFAIK there's only [circe-golden](https://github.com/circe/circe-golden), but please correct me if I'm mistaken by either opening an issue or even better, by creating a PR.

Circe Golden, besides only supporting Circe as the JSON library, it creates a bunch of serialized files with data generated with Scalacheck's generators. These files are then read in subsequent runs of the tests and compared against the newly serialized data, by using the same Scalacheck seed.

The idea is great. However, whenever you make any changes to your model, you need to delete all the generated JSON files so that they are created again with the new version of the protocol. This is far from ideal, since we immediately lose meaningful diffs and it gets harder to keep track of the actual changes in any PR.

Conversely, `munit-golden` supports *any* JSON library as well as meaningful diffs when breaking the protocol. This is the essence of the project.
