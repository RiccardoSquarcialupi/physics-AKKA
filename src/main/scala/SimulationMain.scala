import akka.actor.typed.ActorSystem

object SimulationMain extends App:
  println("Starting the simulation....")
  val system = ActorSystem(ViewActor(SimulationParams(10, 1000, 1000)), "view")
  system ! ViewActor.Command.Start
