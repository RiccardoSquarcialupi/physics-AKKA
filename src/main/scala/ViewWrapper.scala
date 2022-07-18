import akka.actor.typed.{ActorRef, ActorSystem, Behavior, Signal}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import model.SimulationModel
import view.SimulationView

import java.awt.event.{ActionEvent, ActionListener}

case class ViewWrapper(w: Int, h: Int, act: ActorRef[ViewActor.Command]):
  val view = new SimulationView(w, h)

  view.getStop.addActionListener(new ActionListener {
    override def actionPerformed(e: ActionEvent): Unit =
      view.getStart.setEnabled(true)
      view.getStop.setEnabled(false)
      act ! ViewActor.Command.Stop
  })

  view.getStart.addActionListener(new ActionListener {
    override def actionPerformed(e: ActionEvent): Unit =
      view.getStart.setEnabled(false)
      view.getStop.setEnabled(true)
      act ! ViewActor.Command.Restart
  })

  def updateView(model: SimulationModel): Unit =
    view.modelUpdated(model)

