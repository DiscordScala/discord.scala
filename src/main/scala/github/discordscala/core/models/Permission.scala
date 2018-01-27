package github.discordscala.core.models

sealed trait Permission {

  val id: String
  val value: Long

}

object Permission {

  case object CreateInstantInvite extends Permission {
    val id = "CREATE_INSTANT_INVITE"
    val value = 0x00000001
  }

  case object KickMembers extends Permission {
    val id = "KICK_MEMBERS"
    val value = 0x00000002
  }

  case object BanMembers extends Permission {
    val id = "BAN_MEMBERS"
    val value = 0x00000004
  }

  case object Administrator extends Permission {
    val id = "ADMINISTRATOR"
    val value = 0x00000008
  }

}
