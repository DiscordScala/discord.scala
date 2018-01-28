package github.discordscala.core.models

object Permissions {

  def apply(perms: Permission*): Permissions = new Permissions(perms)
  def unapply(arg: Permissions): Option[Seq[Permission]] = Some(arg.permissions)

}

class Permissions(val permissions: Seq[Permission]) {

  def toLong: Long = permissions.foldLeft(0l)((acc: Long, p: Permission) => acc | p.value)

}

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
