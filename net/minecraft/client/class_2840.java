package net.minecraft.client;

import net.minecraft.class_2957;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.util.math.MathHelper;

public class class_2840 extends class_2957 {
	protected float field_13310 = 0.0F;
	protected long field_13311 = 0L;

	public class_2840(BossBarS2CPacket bossBarS2CPacket) {
		super(bossBarS2CPacket.getUuid(), bossBarS2CPacket.getTitle(), bossBarS2CPacket.getColor(), bossBarS2CPacket.getDivision());
		this.field_13310 = bossBarS2CPacket.getHealth();
		this.health = bossBarS2CPacket.getHealth();
		this.field_13311 = MinecraftClient.getTime();
		this.method_12921(bossBarS2CPacket.method_12639());
		this.method_12922(bossBarS2CPacket.method_12640());
		this.method_12923(bossBarS2CPacket.method_12641());
	}

	@Override
	public void setHealth(float health) {
		this.health = this.getHealth();
		this.field_13310 = health;
		this.field_13311 = MinecraftClient.getTime();
	}

	@Override
	public float getHealth() {
		long l = MinecraftClient.getTime() - this.field_13311;
		float f = MathHelper.clamp((float)l / 100.0F, 0.0F, 1.0F);
		return this.health + (this.field_13310 - this.health) * f;
	}

	public void method_12175(BossBarS2CPacket packet) {
		switch (packet.getAction()) {
			case UPDATE_NAME:
				this.setTitle(packet.getTitle());
				break;
			case UPDATE_PCT:
				this.setHealth(packet.getHealth());
				break;
			case UPDATE_STYLE:
				this.setColor(packet.getColor());
				this.setDivision(packet.getDivision());
				break;
			case UPDATE_PROPERTIES:
				this.method_12921(packet.method_12639());
				this.method_12922(packet.method_12640());
		}
	}
}
