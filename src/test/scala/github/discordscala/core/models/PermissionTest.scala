package github.discordscala.core.models
import org.scalatest._

class PermissionTest extends FlatSpec with Matchers {

  "A Permission" should "serialize to long" in {
    val permission = Permissions.all
    permission.toLong should be (2146950399l)
  }

  it should "be queryable" in {
    val permission = Permissions(Permission.ManageEmoji, Permission.Speak)
    permission.has(Permission.ManageEmoji) should be (true)
    permission.has(0x00200000) should be (true)
    permission.has(0x00020000) should be (false)
    permission.has(Permission.UseExternalEmojis) should be (false)
  }

  it should "deserialize from long" in {
    val permissions = Permissions(0x00220000)
    permissions should be (Permissions(Permission.Speak, Permission.MentionEveryone))
  }

  it should "add correctly" in {
    val permissions1 = Permissions(0x00200000)
    val permissions2 = Permissions(0x00020000)
    (permissions1 + permissions2) should be (Permissions(0x00220000))
    (permissions1 + Permission.MentionEveryone) should be (Permissions(0x00220000))
    (permissions1 + 0x00020000) should be (Permissions(0x00220000))
  }

  it should "subtract correctly" in {
    val permissions1 = Permissions(0x00220000)
    val permissions2 = Permissions(0x00020000)
    (permissions1 - permissions2) should be (Permissions(0x00200000))
    (permissions1 - Permission.MentionEveryone) should be (Permissions(0x00200000))
    (permissions1 - 0x00020000) should be (Permissions(0x00200000))
  }

}
