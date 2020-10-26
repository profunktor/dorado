package dev.profunktor.golden

import cats.effect._
import cats.implicits._
import io.circe.syntax._

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    (Event.Id.make[IO], Timestamp.make[IO]).parTupled
      .flatMap {
        case (id, ts) =>
          val e1: Event = Event.One(id, "test1", ts)
          val e2: Event = Event.Two(id, 123, ts)
          IO(println(e1.asJson)) >> IO(println(e2.asJson))
      }
      .as(ExitCode.Success)

}
