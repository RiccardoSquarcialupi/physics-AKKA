import lib.{Body, Boundary}

import java.util
import java.util.ArrayList

case class Task(bodies: util.ArrayList[Body], start: Int, bodiesForWorker: Int, bounds: Boundary)
