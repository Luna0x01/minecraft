package net.minecraft.block.entity;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.EnchantingScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.MathHelper;

public class EnchantingTableBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, Tickable {
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
	private static final Random RANDOM = new Random();
	private Text field_18634;

	public EnchantingTableBlockEntity() {
		super(BlockEntityType.ENCHANTING_TABLE);
	}

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		super.toNbt(nbt);
		if (this.hasCustomName()) {
			nbt.putString("CustomName", Text.Serializer.serialize(this.field_18634));
		}

		return nbt;
	}

	@Override
	public void fromNbt(NbtCompound nbt) {
		super.fromNbt(nbt);
		if (nbt.contains("CustomName", 8)) {
			this.field_18634 = Text.Serializer.deserializeText(nbt.getString("CustomName"));
		}
	}

	@Override
	public void tick() {
		this.pageTurningSpeed = this.nextPageTurningSpeed;
		this.openBookAnglePrev = this.openBookAngle;
		PlayerEntity playerEntity = this.world
			.method_16361((double)((float)this.pos.getX() + 0.5F), (double)((float)this.pos.getY() + 0.5F), (double)((float)this.pos.getZ() + 0.5F), 3.0, false);
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
		h = MathHelper.clamp(h, -0.2F, 0.2F);
		this.pageTurningVelocity = this.pageTurningVelocity + (h - this.pageTurningVelocity) * 0.9F;
		this.nextPageAngle = this.nextPageAngle + this.pageTurningVelocity;
	}

	@Override
	public Text method_15540() {
		return (Text)(this.field_18634 != null ? this.field_18634 : new TranslatableText("container.enchant"));
	}

	@Override
	public boolean hasCustomName() {
		return this.field_18634 != null;
	}

	public void method_16811(@Nullable Text text) {
		this.field_18634 = text;
	}

	@Nullable
	@Override
	public Text method_15541() {
		return this.field_18634;
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
