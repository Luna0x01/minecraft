package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class PlayerAbilitiesS2CPacket implements Packet<ClientPlayPacketListener> {
	private boolean invulnerable;
	private boolean flying;
	private boolean allowFlying;
	private boolean creativeMode;
	private float flySpeed;
	private float fovModifier;

	public PlayerAbilitiesS2CPacket() {
	}

	public PlayerAbilitiesS2CPacket(PlayerAbilities playerAbilities) {
		this.setInvulnerable(playerAbilities.invulnerable);
		this.setFlying(playerAbilities.flying);
		this.setAllowFlying(playerAbilities.allowFlying);
		this.setCreativeMode(playerAbilities.creativeMode);
		this.setFlySpeed(playerAbilities.getFlySpeed());
		this.setFovModifier(playerAbilities.getWalkSpeed());
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		byte b = buf.readByte();
		this.setInvulnerable((b & 1) > 0);
		this.setFlying((b & 2) > 0);
		this.setAllowFlying((b & 4) > 0);
		this.setCreativeMode((b & 8) > 0);
		this.setFlySpeed(buf.readFloat());
		this.setFovModifier(buf.readFloat());
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		byte b = 0;
		if (this.isInvulnerable()) {
			b = (byte)(b | 1);
		}

		if (this.isFlying()) {
			b = (byte)(b | 2);
		}

		if (this.allowFlying()) {
			b = (byte)(b | 4);
		}

		if (this.isCreativeMode()) {
			b = (byte)(b | 8);
		}

		buf.writeByte(b);
		buf.writeFloat(this.flySpeed);
		buf.writeFloat(this.fovModifier);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onPlayerAbilities(this);
	}

	public boolean isInvulnerable() {
		return this.invulnerable;
	}

	public void setInvulnerable(boolean invulnerable) {
		this.invulnerable = invulnerable;
	}

	public boolean isFlying() {
		return this.flying;
	}

	public void setFlying(boolean flying) {
		this.flying = flying;
	}

	public boolean allowFlying() {
		return this.allowFlying;
	}

	public void setAllowFlying(boolean allowFlying) {
		this.allowFlying = allowFlying;
	}

	public boolean isCreativeMode() {
		return this.creativeMode;
	}

	public void setCreativeMode(boolean creativeMode) {
		this.creativeMode = creativeMode;
	}

	public float getFlySpeed() {
		return this.flySpeed;
	}

	public void setFlySpeed(float flySpeed) {
		this.flySpeed = flySpeed;
	}

	public float getFovModifier() {
		return this.fovModifier;
	}

	public void setFovModifier(float fovModifier) {
		this.fovModifier = fovModifier;
	}
}
