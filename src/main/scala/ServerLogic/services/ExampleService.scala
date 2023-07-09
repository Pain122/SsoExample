package ServerLogic.services

trait ExampleService[F[_]] {
  def exampleLogic(): F[String]
}
