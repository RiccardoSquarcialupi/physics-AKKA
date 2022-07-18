import akka.actor.typed.ActorSystem

object SimulationMainNoGUI extends App:
  println("Starting the simulation....")
  // worker bodies iter
  val system = ActorSystem(Master(10, 1000, 1000, Option.empty, true), "master")
  system ! Master.Command.Start