package net.minecraft.block.entity;

import java.util.Random;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.EnchantingScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.MathHelper;

public class EnchantingTableBlockEntity extends BlockEntity implements Tickable, NamedScreenHandlerFactory {
	public int ticks;
	public float nextPageAngle;
	public float pageAngle;
	public float nextPageTurningVelocity;
	public float pageTurningVelocity;
	public float nextPageTurningSpeed;
	public float pageTurningSpeed;
	public float openBookAngle;
	public float openBookAnglePrev;
	public float closedBookAngle;
	private static Random RANDOM = new Random();
	private String customName;

	@Override
	public void toNbt(NbtCompound nbt) {
		super.toNbt(nbt);
		if (this.hasCustomName()) {
			nbt.putString("CustomName", this.customName);
		}
	}

	@Override
	public void fromNbt(NbtCompound nbt) {
		super.fromNbt(nbt);
		if (nbt.contains("CustomName", 8)) {
			this.customName = nbt.getString("CustomName");
		}
	}

	@Override
	public void tick() {
		this.pageTurningSpeed = this.nextPageTurningSpeed;
		this.openBookAnglePrev = this.openBookAngle;
		PlayerEntity playerEntity = this.world
			.getClosestPlayer((double)((float)this.pos.getX() + 0.5F), (double)((float)this.pos.getY() + 0.5F), (double)((float)this.pos.getZ() + 0.5F), 3.0);
		if (playerEntity != null) {
			double d = playerEntity.x - (double)((float)this.pos.getX() + 0.5F);
			double e = playerEntity.z - (double)((float)this.pos.getZ() + 0.5F);
			this.closedBookAngle = (float)MathHelper.atan2(e, d);
			this.nextPageTurningSpeed += 0.1F;
			if (this.nextPageTurningSpeed < 0.5F || RANDOM.nextInt(40) == 0) {
				float f = this.nextPageTurningVelocity;

				do {
					this.nextPageTurningVelocity = this.nextPageTurningVelocity + (float)(RANDOM.nextInt(4) - RANDOM.nextInt(4));
				} while (f == this.nextPageTurningVelocity);
			}
		} else {
			this.closedBookAngle += 0.02F;
			this.nextPageTurningSpeed -= 0.1F;
		}

		while (this.openBookAngle >= (float) Math.PI) {
			this.openBookAngle -= (float) (Math.PI * 2);
		}

		while (this.openBookAngle < (float) -Math.PI) {
			this.openBookAngle += (float) (Math.PI * 2);
		}

		while (this.closedBookAngle >= (float) Math.PI) {
			this.closedBookAngle -= (float) (Math.PI * 2);
		}

		while (this.closedBookAngle < (float) -Math.PI) {
			this.closedBookAngle += (float) (Math.PI * 2);
		}

		float g = this.closedBookAngle - this.openBookAngle;

		while (g >= (float) Math.PI) {
			g -= (float) (Math.PI * 2);
		}

		while (g < (float) -Math.PI) {
			g += (float) (Math.PI * 2);
		}

		this.openBookAngle += g * 0.4F;
		this.nextPageTurningSpeed = MathHelper.clamp(this.nextPageTurningSpeed, 0.0F, 1.0F);
		this.ticks++;
		this.pageAngle = this.nextPageAngle;
		float h = (this.nextPageTurningVelocity - this.nextPageAngle) * 0.4F;
		float i = 0.2F;
		h = MathHelper.clamp(h, -i, i);
		this.pageTurningVelocity = this.pageTurningVelocity + (h - this.pageTurningVelocity) * 0.9F;
		this.nextPageAngle = this.nextPageAngle + this.pageTurningVelocity;
	}

	@Override
	public String getTranslationKey() {
		return this.hasCustomName() ? this.customName : "container.enchant";
	}

	@Override
	public boolean hasCustomName() {
		return this.customName != null && this.customName.length() > 0;
	}

	public void setCustomName(String customName) {
		this.customName = customName;
	}

	@Override
	public Text getName() {
		return (Text)(this.hasCustomName() ? new LiteralText(this.getTranslationKey()) : new TranslatableText(this.getTranslationKey()));
	}

	@Override
	public ScreenHandler createScreenHandler(PlayerInventory inventory, PlayerEntity player) {
		return new EnchantingScreenHandler(inventory, this.world, this.pos);
	}

	@Override
	public String getId() {
		return "minecraft:enchanting_table";
	}
}
