import java.util.Calendar

class Timer {
  var startTime: Long = _
  var stopTime: Long = _

  def start(): Unit =
    startTime = Calendar.getInstance().getTimeInMillis()

  def stop(): Long =
    stopTime = Calendar.getInstance().getTimeInMillis()
    stopTime - startTime
}
