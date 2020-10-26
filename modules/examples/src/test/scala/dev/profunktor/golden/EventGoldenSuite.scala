package dev.profunktor.golden

import munit.golden.circe.CirceGoldenSuite

class EventGoldenSuite extends CirceGoldenSuite[Event]("/event")
