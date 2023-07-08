package Auth.model

sealed trait Role

case object Customer extends Role

case object Seller extends Role
