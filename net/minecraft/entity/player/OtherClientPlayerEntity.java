package net.minecraft.entity.player;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class OtherClientPlayerEntity extends AbstractClientPlayerEntity {
	public OtherClientPlayerEntity(World world, GameProfile gameProfile) {
		super(world, gameProfile);
		this.stepHeight = 1.0F;
		this.noClip = true;
		this.field_4009 = 0.25F;
	}

	@Override
	public boolean shouldRender(double distance) {
		double d = this.getBoundingBox().getAverage() * 10.0;
		if (Double.isNaN(d)) {
			d = 1.0;
		}

		d *= 64.0 * getRenderDistanceMultiplier();
		return distance < d * d;
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		return true;
	}

	@Override
	public void tick() {
		this.field_4009 = 0.0F;
		super.tick();
		this.field_6748 = this.field_6749;
		double d = this.x - this.prevX;
		double e = this.z - this.prevZ;
		float f = MathHelper.sqrt(d * d + e * e) * 4.0F;
		if (f > 1.0F) {
			f = 1.0F;
		}

		this.field_6749 = this.field_6749 + (f - this.field_6749) * 0.4F;
		this.field_6750 = this.field_6750 + this.field_6749;
	}

	@Override
	public void tickMovement() {
		if (this.bodyTrackingIncrements > 0) {
			double d = this.x + (this.serverPitch - this.x) / (double)this.bodyTrackingIncrements;
			double e = this.y + (this.serverY - this.y) / (double)this.bodyTrackingIncrements;
			double f = this.z + (this.serverZ - this.z) / (double)this.bodyTrackingIncrements;
			this.yaw = (float)((double)this.yaw + MathHelper.wrapDegrees(this.serverYaw - (double)this.yaw) / (double)this.bodyTrackingIncrements);
			this.pitch = (float)((double)this.pitch + (this.serverX - (double)this.pitch) / (double)this.bodyTrackingIncrements);
			this.bodyTrackingIncrements--;
			this.updatePosition(d, e, f);
			this.setRotation(this.yaw, this.pitch);
		}

		if (this.field_16816 > 0) {
			this.headYaw = (float)((double)this.headYaw + MathHelper.wrapDegrees(this.field_16815 - (double)this.headYaw) / (double)this.field_16816);
			this.field_16816--;
		}

		this.prevStrideDistance = this.strideDistance;
		this.tickHandSwing();
		float g = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
		float h = (float)Math.atan(-this.velocityY * 0.2F) * 15.0F;
		if (g > 0.1F) {
			g = 0.1F;
		}

		if (!this.onGround || this.getHealth() <= 0.0F) {
			g = 0.0F;
		}

		if (this.onGround || this.getHealth() <= 0.0F) {
			h = 0.0F;
		}

		this.strideDistance = this.strideDistance + (g - this.strideDistance) * 0.4F;
		this.field_6753 = this.field_6753 + (h - this.field_6753) * 0.8F;
		this.world.profiler.push("push");
		this.tickCramming();
		this.world.profiler.pop();
	}

	@Override
	public void method_5505(Text text) {
		MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(text);
	}
}
