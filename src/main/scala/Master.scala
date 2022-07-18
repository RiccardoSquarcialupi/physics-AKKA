import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, Signal}
import lib.Body
import model.SimulationModel

import java.util
import java.util.ArrayList

object Master:

  enum Command:
    case Start
    case Stop
    case VelocitiesUpdated(result: TaskResult)
    case PositionsUpdated(result: TaskResult)
    case ViewUpdated

  export Command.*

  def apply(nWorkers: Int, nBodies: Int, totalIter: Int, view: Option[ActorRef[ViewActor.Command]], timer: Boolean): Behavior[Command] =

    var workers = Seq.empty[ActorRef[WorkerActor.Command]]
    val model: SimulationModel = new SimulationModel(nBodies, totalIter)
    val bodiesForWorker = Math.ceil((nBodies / nWorkers).toDouble).toInt
    var numberOfUpdatedVelocities = 0
    var numberOfUpdatedPositions = 0
    val newBodies = new ArrayList[Body]()
    val timer = Timer()

    Behaviors.receive {
      (context, msg) =>
        msg match {
          case Start =>
            model.init()
            workers = for {
              i <- 1 to nWorkers
            } yield context.spawn(WorkerActor(), "Worker" + i)
            timer.start()
            sendUpdateVelocities(workers, model, bodiesForWorker, context.self)
            Behaviors.same
          case Stop =>
            sendStop(workers)
            Behaviors.stopped
          case ViewUpdated =>
            checkIterAndStop(workers, model.getIter, totalIter, Option.empty)
            sendUpdateVelocities(workers, model, bodiesForWorker, context.self)
            Behaviors.same
          case VelocitiesUpdated(result) =>
            numberOfUpdatedVelocities = numberOfUpdatedVelocities + 1
            newBodies.addAll(result.bodies)
            if (numberOfUpdatedVelocities == nWorkers) then
              numberOfUpdatedVelocities = 0
              model.setBodies(newBodies)
              newBodies.clear()
              for
                w <- workers
                i = workers.indexOf(w)
                start = i * bodiesForWorker
              yield w ! WorkerActor.UpdatePositions(Task(new ArrayList(model.getBodies), start, bodiesForWorker, model.getBounds), context.self)
            Behaviors.same
          case PositionsUpdated(result) =>
            numberOfUpdatedPositions = numberOfUpdatedPositions + 1
            newBodies.addAll(result.bodies)
            if (numberOfUpdatedPositions == nWorkers) then
              numberOfUpdatedPositions = 0
              model.setBodies(newBodies)
              newBodies.clear()
              if view.isDefined then
                view.get ! ViewActor.Command.UpdateView(model, context.self)
                model.updateVirtualTime()
              else
                model.updateVirtualTime()
                checkIterAndStop(workers, model.getIter, totalIter, Option(timer))
                sendUpdateVelocities(workers, model, bodiesForWorker, context.self)
            Behaviors.same
        }
    }

  def checkIterAndStop(workers: Seq[ActorRef[WorkerActor.Command]], currentIter: Long, maxIter: Int, timer: Option[Timer]): Unit =
    if (currentIter == maxIter) {
      sendStop(workers)

      if timer.isDefined then
        val time = timer.get.stop()
        println("Simulation time:" + time + " ms")
      Behaviors.stopped
    }

  def sendUpdateVelocities(workers: Seq[ActorRef[WorkerActor.Command]], model: SimulationModel, bodiesForWorker: Int, myself: ActorRef[Command]): Unit =
    for
      w <- workers
      i = workers.indexOf(w)
      start = i * bodiesForWorker
    yield w ! WorkerActor.UpdateVelocities(Task(new ArrayList(model.getBodies), start, bodiesForWorker, model.getBounds), myself)

  def sendStop(workers: Seq[ActorRef[WorkerActor.Command]]): Unit =
    for
      w <- workers
    yield w ! WorkerActor.Command.Stop
