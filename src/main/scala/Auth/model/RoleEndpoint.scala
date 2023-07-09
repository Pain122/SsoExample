package Auth.model

import sttp.tapir.Endpoint

case class RoleEndpoint[Unit, INPUT, ERROR_OUTPUT, OUTPUT, -R](
    endpoint: Endpoint[Unit, INPUT, ERROR_OUTPUT, OUTPUT, R],
    role: Role
)
