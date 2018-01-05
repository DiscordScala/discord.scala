package github.discordscala.core.models

sealed trait Region {

  val id: String
  val vip: Boolean

}

object Region {

  case object EuropeCentral extends Region {
    val id = "eu-central"
    val vip = false
  }

  case object EuropeWest extends Region {
    val id = "eu-west"
    val vip = false
  }

  case object Japan extends Region {
    val id = "japan"
    val vip = false
  }

  case object Brazil extends Region {
    val id = "brazil"
    val vip = false
  }

  case object HongKong extends Region {
    val id = "hongkong"
    val vip = false
  }

  case object Sydney extends Region {
    val id = "sydney"
    val vip = false
  }

  case object Singapore extends Region {
    val id = "singapore"
    val vip = false
  }

  case object UnitedStatesCentral extends Region {
    val id = "us-central"
    val vip = false
  }

  case object UnitedStatesEast extends Region {
    val id = "us-east"
    val vip = false
  }

  case object UnitedStatesWest extends Region {
    val id = "us-west"
    val vip = false
  }

  case object UnitedStatesSouth extends Region {
    val id = "us-south"
    val vip = false
  }

  case object Russia extends Region {
    val id = "russia"
    val vip = false
  }

  case object VipAmsterdam extends Region {
    val id = "vip-amsterdam"
    val vip = true
  }

  case object VipUnitedStatesEast extends Region {
    val id = "vip-us-east"
    val vip = true
  }

  case object VipUnitedStatesWest extends Region {
    val id = "vip-us-west"
    val vip = true
  }

  @Deprecated
  case object Amsterdam extends Region {
    val id = "amsterdam"
    val vip = false
  }

  @Deprecated
  case object Frankfurt extends Region {
    val id = "frankfurt"
    val vip = false
  }

  @Deprecated
  case object London extends Region {
    val id = "london"
    val vip = false
  }

  //FIXME lazy vals pulling regions from api

  def apply(id: String): Region = {
    case "eu-central" => EuropeCentral
    case "eu-west" => EuropeWest
    case "japan" => Japan
    case "brazil" => Brazil
    case "hongkong" => HongKong
    case "sydney" => Sydney
    case "singapore" => Singapore
    case "us-central" => UnitedStatesCentral
    case "us-west" => UnitedStatesWest
    case "us-east" => UnitedStatesEast
    case "us-south" => UnitedStatesSouth
    case "russia" => Russia
    case "vip-amsterdam" => VipAmsterdam
    case "vip-us-west" => VipUnitedStatesWest
    case "vip-us-east" => VipUnitedStatesEast
    case "amsterdam" => Amsterdam
    case "frankfurt" => Frankfurt
    case "london" => London
    case _ => throw new IllegalArgumentException(s"$id is not a valid region.")
  }

}
