package net.minecraft.entity.player;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class OtherClientPlayerEntity extends AbstractClientPlayerEntity {
	private boolean field_1769;
	private int field_1770;
	private double field_1771;
	private double field_1772;
	private double field_1773;
	private double field_1774;
	private double field_1775;

	public OtherClientPlayerEntity(World world, GameProfile gameProfile) {
		super(world, gameProfile);
		this.stepHeight = 0.0F;
		this.noClip = true;
		this.field_4009 = 0.25F;
		this.renderDistanceMultiplier = 10.0;
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		return true;
	}

	@Override
	public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
		this.field_1771 = x;
		this.field_1772 = y;
		this.field_1773 = z;
		this.field_1774 = (double)yaw;
		this.field_1775 = (double)pitch;
		this.field_1770 = interpolationSteps;
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
		if (!this.field_1769 && this.isSwimming() && this.inventory.main[this.inventory.selectedSlot] != null) {
			ItemStack itemStack = this.inventory.main[this.inventory.selectedSlot];
			this.setUseItem(this.inventory.main[this.inventory.selectedSlot], itemStack.getItem().getMaxUseTime(itemStack));
			this.field_1769 = true;
		} else if (this.field_1769 && !this.isSwimming()) {
			this.resetUseItem();
			this.field_1769 = false;
		}
	}

	@Override
	public void tickMovement() {
		if (this.field_1770 > 0) {
			double d = this.x + (this.field_1771 - this.x) / (double)this.field_1770;
			double e = this.y + (this.field_1772 - this.y) / (double)this.field_1770;
			double f = this.z + (this.field_1773 - this.z) / (double)this.field_1770;
			double g = this.field_1774 - (double)this.yaw;

			while (g < -180.0) {
				g += 360.0;
			}

			while (g >= 180.0) {
				g -= 360.0;
			}

			this.yaw = (float)((double)this.yaw + g / (double)this.field_1770);
			this.pitch = (float)((double)this.pitch + (this.field_1775 - (double)this.pitch) / (double)this.field_1770);
			this.field_1770--;
			this.updatePosition(d, e, f);
			this.setRotation(this.yaw, this.pitch);
		}

		this.prevStrideDistance = this.strideDistance;
		this.tickHandSwing();
		float h = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
		float i = (float)Math.atan(-this.velocityY * 0.2F) * 15.0F;
		if (h > 0.1F) {
			h = 0.1F;
		}

		if (!this.onGround || this.getHealth() <= 0.0F) {
			h = 0.0F;
		}

		if (this.onGround || this.getHealth() <= 0.0F) {
			i = 0.0F;
		}

		this.strideDistance = this.strideDistance + (h - this.strideDistance) * 0.4F;
		this.field_6753 = this.field_6753 + (i - this.field_6753) * 0.8F;
	}

	@Override
	public void setArmorSlot(int armorSlot, ItemStack item) {
		if (armorSlot == 0) {
			this.inventory.main[this.inventory.selectedSlot] = item;
		} else {
			this.inventory.armor[armorSlot - 1] = item;
		}
	}

	@Override
	public void sendMessage(Text text) {
		MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(text);
	}

	@Override
	public boolean canUseCommand(int permissionLevel, String commandLiteral) {
		return false;
	}

	@Override
	public BlockPos getBlockPos() {
		return new BlockPos(this.x + 0.5, this.y + 0.5, this.z + 0.5);
	}
}
