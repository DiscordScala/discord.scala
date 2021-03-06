package org.discordscala.core.models

import org.discordscala.core.models.Permission._

object Permissions {

  def apply(perms: Permission*): Permissions = new Permissions(perms.toSet)

  def unapply(arg: Permissions): Option[Set[Permission]] = Some(arg.permissions)

  def unapply(arg: Long): Option[Permissions] = Some(Permissions(arg))

  def apply(perms: Long): Permissions = new Permissions(Permissions.all.permissions.filter(x => (x.value & perms) > 0))

  def all = new Permissions(Set(CreateInstantInvite, KickMembers, BanMembers, Administrator, ManageChannels, ManageGuild, AddReactions, ViewAuditLog, ViewChannel, SendMessages, SendTtsMessages, EmbedLinks, AttachFiles, ReadMessageHistory, MentionEveryone, UseExternalEmojis, Connect, Speak, MuteMembers, DeafenMembers, MoveMembers, UseVad, ChangeNickname, ManageNicknames, ManageRoles, ManageWebhooks, ManageEmoji))

}

case class Permissions(permissions: Set[Permission]) {

  def has(permission: Permission): Boolean = permissions.contains(permission)

  def has(permissions: Long): Boolean = (this.toLong & permissions) > 0

  def +(other: Permissions): Permissions = |(other)

  def |(other: Permissions): Permissions = Permissions(other.toLong | this.toLong)

  def -(other: Permissions): Permissions = \(other)

  def \(other: Permissions): Permissions = Permissions(this.toLong - (this.toLong & other.toLong))

  def +(other: Permission): Permissions = |(other)

  def |(other: Permission): Permissions = Permissions(other.value | this.toLong)

  def toLong: Long = permissions.foldLeft(0l)((acc: Long, p: Permission) => acc | p.value)

  def -(other: Permission): Permissions = \(other)

  def \(other: Permission): Permissions = Permissions(this.toLong - (this.toLong & other.value))

  def +(other: Long): Permissions = |(other)

  def |(other: Long): Permissions = Permissions(other | this.toLong)

  def -(other: Long): Permissions = \(other)

  def \(other: Long): Permissions = Permissions(this.toLong - (this.toLong & other))

  override def toString: String = s"Permission(${permissions.mkString(", ")})"

}

sealed trait Permission {

  val id: String
  val value: Long

}

object Permission {

  case object CreateInstantInvite extends Permission {
    val id = "CREATE_INSTANT_INVITE"
    val value = 0x00000001l
  }

  case object KickMembers extends Permission {
    val id = "KICK_MEMBERS"
    val value = 0x00000002l
  }

  case object BanMembers extends Permission {
    val id = "BAN_MEMBERS"
    val value = 0x00000004l
  }

  case object Administrator extends Permission {
    val id = "ADMINISTRATOR"
    val value = 0x00000008l
  }

  case object ManageChannels extends Permission {
    val id = "MANAGE_CHANNELS"
    val value = 0x00000010l
  }

  case object ManageGuild extends Permission {
    val id = "MANAGE_GUILD"
    val value = 0x00000020l
  }

  case object AddReactions extends Permission {
    val id = "ADD_REACTIONS"
    val value = 0x00000040l
  }

  case object ViewAuditLog extends Permission {
    val id = "VIEW_AUDIT_LOG"
    val value = 0x00000080l
  }

  case object ViewChannel extends Permission {
    val id = "VIEW_CHANNEL"
    val value = 0x00000400l
  }

  case object SendMessages extends Permission {
    val id = "SEND_MESSAGES"
    val value = 0x00000800l
  }

  case object SendTtsMessages extends Permission {
    val id = "SEND_TTS_MESSAGES"
    val value = 0x00001000l
  }

  case object ManageMessages extends Permission {
    val id = "MANAGE_MESSAGES"
    val value = 0x00002000l
  }

  case object EmbedLinks extends Permission {
    val id = "EMBED_LINKS"
    val value = 0x00004000l
  }

  case object AttachFiles extends Permission {
    val id = "ATTACH_FILES"
    val value = 0x00008000l
  }

  case object ReadMessageHistory extends Permission {
    val id = "READ_MESSAGE_HISTORY"
    val value = 0x00010000l
  }

  case object MentionEveryone extends Permission {
    val id = "MENTION_EVERYONE"
    val value = 0x00020000l
  }

  case object UseExternalEmojis extends Permission {
    val id = "USE_EXTERNAL_EMOJIS"
    val value = 0x00040000l
  }

  case object Connect extends Permission {
    val id = "CONNECT"
    val value = 0x00100000l
  }

  case object Speak extends Permission {
    val id = "SPEAK"
    val value = 0x00200000l
  }

  case object MuteMembers extends Permission {
    val id = "MUTE_MEMBERS"
    val value = 0x00400000l
  }

  case object DeafenMembers extends Permission {
    val id = "DEAFEN_MEMBERS"
    val value = 0x00800000l
  }

  case object MoveMembers extends Permission {
    val id = "MOVE_MEMBERS"
    val value = 0x01000000l
  }

  case object UseVad extends Permission {
    val id = "USE_VAD"
    val value = 0x02000000l
  }

  case object ChangeNickname extends Permission {
    val id = "CHANGE_NICKNAME"
    val value = 0x04000000l
  }

  case object ManageNicknames extends Permission {
    val id = "MANAGE_NICKNAMES"
    val value = 0x08000000l
  }

  case object ManageRoles extends Permission {
    val id = "MANAGE_ROLES"
    val value = 0x10000000l
  }

  case object ManageWebhooks extends Permission {
    val id = "MANAGE_WEBHOOKS"
    val value = 0x20000000l
  }

  case object ManageEmoji extends Permission {
    val id = "MANAGE_EMOJI"
    val value = 0x40000000l
  }

}
