import akka.actor.typed.ActorSystem

object SimulationMainNoGUI extends App:
  println("Starting the simulation....")
  // worker bodies iter
  val system = ActorSystem(Master(1, 5000, 10000, Option.empty, true), "master")
  system ! Master.Command.Start