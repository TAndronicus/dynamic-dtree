package jb

import scala.concurrent.Future

object Playground {

  def main(args: Array[String]): Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global
    val f = Future {
      Thread.sleep(1000); 1 / 0
    }
    f.onComplete(println)
    Thread.sleep(2000)
  }

}
