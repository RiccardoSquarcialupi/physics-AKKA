import akka.actor.typed.{ActorRef, ActorSystem, Behavior, Signal}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import model.SimulationModel
import view.SimulationView

import java.awt.event.{ActionEvent, ActionListener}

object ViewActor:

  enum Command:
    case Start
    case UpdateView(model: SimulationModel,  replyTo: ActorRef[Master.Command])
    case Stop
    case Restart
  export Command.*

  def apply(params: SimulationParams): Behavior[Command] =

    var view: Option[ViewWrapper] = Option.empty
    var master: Option[ActorRef[Master.Command]] = Option.empty
    var timer: Timer = Timer()
    Behaviors.receive {
      (context, msg) =>
        msg match{
          case Start =>
            timer.start()
            view = Option(ViewWrapper(620, 620, context.self))
            master = Option(context.spawn(Master(params.nWorkers, params.nBodies, params.nIter, Option(context.self), false),  "master"))
            master.get ! Master.Command.Start
            Behaviors.same
          case UpdateView(model, replyTo) =>
            view.get.updateView(model)
            replyTo ! Master.Command.ViewUpdated
            Behaviors.same
          case Stop =>
            master.get ! Master.Command.Stop
            timer.stop()
            println("Simulation time: " + timer.stopTime)
            Behaviors.same
          case Restart =>
            master = Option(context.spawn(Master(params.nWorkers, params.nBodies, params.nIter, Option(context.self), false),  "master"))
            master.get ! Master.Command.Start
            Behaviors.same
        }
    }
