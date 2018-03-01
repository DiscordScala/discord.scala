package github.discordscala.core.models

import github.discordscala.core.models.Permission._

object Permissions {

  def apply(perms: Permission*): Permissions = new Permissions(perms)

  def all = new Permissions(Seq(CreateInstantInvite, KickMembers, BanMembers, Administrator, ManageChannels, ManageGuild, AddReactions, ViewAuditLog, ViewChannel, SendMessages, SendTtsMessages, EmbedLinks, AttachFiles, ReadMessageHistory, MentionEveryone, UseExternalEmojis, Connect, Speak, MuteMembers, DeafenMembers, MoveMembers, UseVad, ChangeNickname, ManageNicknames, ManageRoles, ManageWebhooks, ManageEmoji))

  def apply(perms: Long): Permissions = new Permissions(Permissions.all.permissions.filter(x => (x.value & perms) > 0))

  def unapply(arg: Permissions): Option[Seq[Permission]] = Some(arg.permissions)

  def unapply(arg: Long): Option[Permissions] = Some(Permissions(arg))

}

class Permissions(val permissions: Seq[Permission]) {

  def has(permission: Permission): Boolean = permissions.contains(permission)

  def has(permissions: Long): Boolean = (this.toLong | permissions) > 0

  def toLong: Long = permissions.foldLeft(0l)((acc: Long, p: Permission) => acc | p.value)

  override def toString: String = s"Permission(${permissions.mkString(", ")})"

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

  case object ManageChannels extends Permission {
    val id = "MANAGE_CHANNELS"
    val value = 0x00000010
  }

  case object ManageGuild extends Permission {
    val id = "MANAGE_GUILD"
    val value = 0x00000020
  }

  case object AddReactions extends Permission {
    val id = "ADD_REACTIONS"
    val value = 0x00000040
  }

  case object ViewAuditLog extends Permission {
    val id = "VIEW_AUDIT_LOG"
    val value = 0x00000080
  }

  case object ViewChannel extends Permission {
    val id = "VIEW_CHANNEL"
    val value = 0x00000400
  }

  case object SendMessages extends Permission {
    val id = "SEND_MESSAGES"
    val value = 0x00000800
  }

  case object SendTtsMessages extends Permission {
    val id = "SEND_TTS_MESSAGES"
    val value = 0x00001000
  }

  case object ManageMessages extends Permission {
    val id = "MANAGE_MESSAGES"
    val value = 0x00002000
  }

  case object EmbedLinks extends Permission {
    val id = "EMBED_LINKS"
    val value = 0x00004000
  }

  case object AttachFiles extends Permission {
    val id = "ATTACH_FILES"
    val value = 0x00008000
  }

  case object ReadMessageHistory extends Permission {
    val id = "READ_MESSAGE_HISTORY"
    val value = 0x00010000
  }

  case object MentionEveryone extends Permission {
    val id = "MENTION_EVERYONE"
    val value = 0x00020000
  }

  case object UseExternalEmojis extends Permission {
    val id = "USE_EXTERNAL_EMOJIS"
    val value = 0x00040000
  }

  case object Connect extends Permission {
    val id = "CONNECT"
    val value = 0x00100000
  }

  case object Speak extends Permission {
    val id = "SPEAK"
    val value = 0x00200000
  }

  case object MuteMembers extends Permission {
    val id = "MUTE_MEMBERS"
    val value = 0x00400000
  }

  case object DeafenMembers extends Permission {
    val id = "DEAFEN_MEMBERS"
    val value = 0x00800000
  }

  case object MoveMembers extends Permission {
    val id = "MOVE_MEMBERS"
    val value = 0x01000000
  }

  case object UseVad extends Permission {
    val id = "USE_VAD"
    val value = 0x02000000
  }

  case object ChangeNickname extends Permission {
    val id = "CHANGE_NICKNAME"
    val value = 0x04000000
  }

  case object ManageNicknames extends Permission {
    val id = "MANAGE_NICKNAMES"
    val value = 0x08000000
  }

  case object ManageRoles extends Permission {
    val id = "MANAGE_ROLES"
    val value = 0x10000000
  }

  case object ManageWebhooks extends Permission {
    val id = "MANAGE_WEBHOOKS"
    val value = 0x20000000
  }

  case object ManageEmoji extends Permission {
    val id = "MANAGE_EMOJI"
    val value = 0x40000000
  }

}
