package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.PacketByteBuf;

public class TeamS2CPacket implements Packet<ClientPlayPacketListener> {
	private String teamName = "";
	private Text field_21566 = new LiteralText("");
	private Text field_21567 = new LiteralText("");
	private Text field_21568 = new LiteralText("");
	private String visibilityRule = AbstractTeam.VisibilityRule.ALWAYS.name;
	private String collisionRule = AbstractTeam.CollisionRule.ALWAYS.name;
	private Formatting field_21569 = Formatting.RESET;
	private final Collection<String> playerList = Lists.newArrayList();
	private int mode;
	private int flags;

	public TeamS2CPacket() {
	}

	public TeamS2CPacket(Team team, int i) {
		this.teamName = team.getName();
		this.mode = i;
		if (i == 0 || i == 2) {
			this.field_21566 = team.method_18101();
			this.flags = team.getFriendlyFlagsBitwise();
			this.visibilityRule = team.getNameTagVisibilityRule().name;
			this.collisionRule = team.method_12129().name;
			this.field_21569 = team.method_12130();
			this.field_21567 = team.method_18104();
			this.field_21568 = team.method_18105();
		}

		if (i == 0) {
			this.playerList.addAll(team.getPlayerList());
		}
	}

	public TeamS2CPacket(Team team, Collection<String> collection, int i) {
		if (i != 3 && i != 4) {
			throw new IllegalArgumentException("Method must be join or leave for player constructor");
		} else if (collection != null && !collection.isEmpty()) {
			this.mode = i;
			this.teamName = team.getName();
			this.playerList.addAll(collection);
		} else {
			throw new IllegalArgumentException("Players cannot be null/empty");
		}
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.teamName = buf.readString(16);
		this.mode = buf.readByte();
		if (this.mode == 0 || this.mode == 2) {
			this.field_21566 = buf.readText();
			this.flags = buf.readByte();
			this.visibilityRule = buf.readString(40);
			this.collisionRule = buf.readString(40);
			this.field_21569 = buf.readEnumConstant(Formatting.class);
			this.field_21567 = buf.readText();
			this.field_21568 = buf.readText();
		}

		if (this.mode == 0 || this.mode == 3 || this.mode == 4) {
			int i = buf.readVarInt();

			for (int j = 0; j < i; j++) {
				this.playerList.add(buf.readString(40));
			}
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeString(this.teamName);
		buf.writeByte(this.mode);
		if (this.mode == 0 || this.mode == 2) {
			buf.writeText(this.field_21566);
			buf.writeByte(this.flags);
			buf.writeString(this.visibilityRule);
			buf.writeString(this.collisionRule);
			buf.writeEnumConstant(this.field_21569);
			buf.writeText(this.field_21567);
			buf.writeText(this.field_21568);
		}

		if (this.mode == 0 || this.mode == 3 || this.mode == 4) {
			buf.writeVarInt(this.playerList.size());

			for (String string : this.playerList) {
				buf.writeString(string);
			}
		}
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onTeam(this);
	}

	public String getTeamName() {
		return this.teamName;
	}

	public Text method_7887() {
		return this.field_21566;
	}

	public Collection<String> getPlayerList() {
		return this.playerList;
	}

	public int getMode() {
		return this.mode;
	}

	public int getFlags() {
		return this.flags;
	}

	public Formatting method_7888() {
		return this.field_21569;
	}

	public String getVisibilityRule() {
		return this.visibilityRule;
	}

	public String getCollisionRule() {
		return this.collisionRule;
	}

	public Text method_20262() {
		return this.field_21567;
	}

	public Text method_20263() {
		return this.field_21568;
	}
}
