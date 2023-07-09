package ServerLogic.controllers

import Auth.logic.SsoAdder
import Auth.model.Customer
import ServerLogic.endpoints.ExampleEndpoints.SomeLogicEndpoint
import ServerLogic.services.ExampleService
import cats.Monad
import cats.implicits.toFunctorOps
import sttp.tapir.server.ServerEndpoint
import tethys.derivation.auto.{jsonReaderMaterializer, jsonWriterMaterializer}

trait ExampleController[F[_]] {
  def all: List[ServerEndpoint[Any, F]]
}

class ExampleControllerImpl[F[_]: Monad](
    ssoBuilder: SsoAdder[F, String],
    service: ExampleService[F]
) extends ExampleController[F] {
  override def all: List[ServerEndpoint[Any, F]] = List(
    ssoBuilder.add[Unit, Unit, String, Any](
      SomeLogicEndpoint,
      _ => _ => service.exampleLogic().map(res => Right(res)),
      Customer
    )
  )
}
