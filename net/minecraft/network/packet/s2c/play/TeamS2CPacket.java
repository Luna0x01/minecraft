package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.PacketByteBuf;

public class TeamS2CPacket implements Packet<ClientPlayPacketListener> {
	private String teamName = "";
	private String displayName = "";
	private String playerPrefix = "";
	private String nameTagVisibilityRule = "";
	private String visibilityRule = AbstractTeam.VisibilityRule.ALWAYS.name;
	private String collisionRule = AbstractTeam.CollisionRule.ALWAYS.name;
	private int teamFormatting = -1;
	private final Collection<String> playerList = Lists.newArrayList();
	private int mode;
	private int flags;

	public TeamS2CPacket() {
	}

	public TeamS2CPacket(Team team, int i) {
		this.teamName = team.getName();
		this.mode = i;
		if (i == 0 || i == 2) {
			this.displayName = team.getDisplayName();
			this.playerPrefix = team.getPrefix();
			this.nameTagVisibilityRule = team.getSuffix();
			this.flags = team.getFriendlyFlagsBitwise();
			this.visibilityRule = team.getNameTagVisibilityRule().name;
			this.collisionRule = team.method_12129().name;
			this.teamFormatting = team.method_12130().getColorIndex();
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
			this.displayName = buf.readString(32);
			this.playerPrefix = buf.readString(16);
			this.nameTagVisibilityRule = buf.readString(16);
			this.flags = buf.readByte();
			this.visibilityRule = buf.readString(32);
			this.collisionRule = buf.readString(32);
			this.teamFormatting = buf.readByte();
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
			buf.writeString(this.displayName);
			buf.writeString(this.playerPrefix);
			buf.writeString(this.nameTagVisibilityRule);
			buf.writeByte(this.flags);
			buf.writeString(this.visibilityRule);
			buf.writeString(this.collisionRule);
			buf.writeByte(this.teamFormatting);
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

	public String getDisplayName() {
		return this.displayName;
	}

	public String getPlayerPrefix() {
		return this.playerPrefix;
	}

	public String getNameTagVisibilityRule() {
		return this.nameTagVisibilityRule;
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

	public int getFormatting() {
		return this.teamFormatting;
	}

	public String getVisibilityRule() {
		return this.visibilityRule;
	}

	public String getCollisionRule() {
		return this.collisionRule;
	}
}
