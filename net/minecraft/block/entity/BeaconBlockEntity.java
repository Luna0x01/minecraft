package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StainedGlassBlock;
import net.minecraft.block.StainedGlassPaneBlock;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.BeaconScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class BeaconBlockEntity extends LockableContainerBlockEntity implements Tickable, Inventory {
	public static final StatusEffect[][] EFFECTS = new StatusEffect[][]{
		{StatusEffect.SPEED, StatusEffect.HASTE}, {StatusEffect.RESISTANCE, StatusEffect.JUMP_BOOST}, {StatusEffect.STRENGTH}, {StatusEffect.REGENERATION}
	};
	private final List<BeaconBlockEntity.BeamSegment> beamSegments = Lists.newArrayList();
	private long lastBeamRenderTime;
	private float beamSpeed;
	private boolean hasValidBase;
	private int levels = -1;
	private int primary;
	private int secondary;
	private ItemStack priceStack;
	private String customName;

	@Override
	public void tick() {
		if (this.world.getLastUpdateTime() % 80L == 0L) {
			this.tickBeacon();
		}
	}

	public void tickBeacon() {
		this.updateBeam();
		this.addPlayerEffects();
	}

	private void addPlayerEffects() {
		if (this.hasValidBase && this.levels > 0 && !this.world.isClient && this.primary > 0) {
			double d = (double)(this.levels * 10 + 10);
			int i = 0;
			if (this.levels >= 4 && this.primary == this.secondary) {
				i = 1;
			}

			int j = this.pos.getX();
			int k = this.pos.getY();
			int l = this.pos.getZ();
			Box box = new Box((double)j, (double)k, (double)l, (double)(j + 1), (double)(k + 1), (double)(l + 1))
				.expand(d, d, d)
				.stretch(0.0, (double)this.world.getMaxBuildHeight(), 0.0);
			List<PlayerEntity> list = this.world.getEntitiesInBox(PlayerEntity.class, box);

			for (PlayerEntity playerEntity : list) {
				playerEntity.addStatusEffect(new StatusEffectInstance(this.primary, 180, i, true, true));
			}

			if (this.levels >= 4 && this.primary != this.secondary && this.secondary > 0) {
				for (PlayerEntity playerEntity2 : list) {
					playerEntity2.addStatusEffect(new StatusEffectInstance(this.secondary, 180, 0, true, true));
				}
			}
		}
	}

	private void updateBeam() {
		int i = this.levels;
		int j = this.pos.getX();
		int k = this.pos.getY();
		int l = this.pos.getZ();
		this.levels = 0;
		this.beamSegments.clear();
		this.hasValidBase = true;
		BeaconBlockEntity.BeamSegment beamSegment = new BeaconBlockEntity.BeamSegment(SheepEntity.getDyedColor(DyeColor.WHITE));
		this.beamSegments.add(beamSegment);
		boolean bl = true;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int m = k + 1; m < 256; m++) {
			BlockState blockState = this.world.getBlockState(mutable.setPosition(j, m, l));
			float[] fs;
			if (blockState.getBlock() == Blocks.STAINED_GLASS) {
				fs = SheepEntity.getDyedColor(blockState.get(StainedGlassBlock.COLOR));
			} else {
				if (blockState.getBlock() != Blocks.STAINED_GLASS_PANE) {
					if (blockState.getBlock().getOpacity() >= 15 && blockState.getBlock() != Blocks.BEDROCK) {
						this.hasValidBase = false;
						this.beamSegments.clear();
						break;
					}

					beamSegment.increaseHeight();
					continue;
				}

				fs = SheepEntity.getDyedColor(blockState.get(StainedGlassPaneBlock.COLOR));
			}

			if (!bl) {
				fs = new float[]{(beamSegment.getColor()[0] + fs[0]) / 2.0F, (beamSegment.getColor()[1] + fs[1]) / 2.0F, (beamSegment.getColor()[2] + fs[2]) / 2.0F};
			}

			if (Arrays.equals(fs, beamSegment.getColor())) {
				beamSegment.increaseHeight();
			} else {
				beamSegment = new BeaconBlockEntity.BeamSegment(fs);
				this.beamSegments.add(beamSegment);
			}

			bl = false;
		}

		if (this.hasValidBase) {
			for (int n = 1; n <= 4; this.levels = n++) {
				int o = k - n;
				if (o < 0) {
					break;
				}

				boolean bl2 = true;

				for (int p = j - n; p <= j + n && bl2; p++) {
					for (int q = l - n; q <= l + n; q++) {
						Block block = this.world.getBlockState(new BlockPos(p, o, q)).getBlock();
						if (block != Blocks.EMERALD_BLOCK && block != Blocks.GOLD_BLOCK && block != Blocks.DIAMOND_BLOCK && block != Blocks.IRON_BLOCK) {
							bl2 = false;
							break;
						}
					}
				}

				if (!bl2) {
					break;
				}
			}

			if (this.levels == 0) {
				this.hasValidBase = false;
			}
		}

		if (!this.world.isClient && this.levels == 4 && i < this.levels) {
			for (PlayerEntity playerEntity : this.world
				.getEntitiesInBox(PlayerEntity.class, new Box((double)j, (double)k, (double)l, (double)j, (double)(k - 4), (double)l).expand(10.0, 5.0, 10.0))) {
				playerEntity.incrementStat(AchievementsAndCriterions.FULL_BEACON);
			}
		}
	}

	public List<BeaconBlockEntity.BeamSegment> getBeamSegments() {
		return this.beamSegments;
	}

	public float getBeamSpeed() {
		if (!this.hasValidBase) {
			return 0.0F;
		} else {
			int i = (int)(this.world.getLastUpdateTime() - this.lastBeamRenderTime);
			this.lastBeamRenderTime = this.world.getLastUpdateTime();
			if (i > 1) {
				this.beamSpeed -= (float)i / 40.0F;
				if (this.beamSpeed < 0.0F) {
					this.beamSpeed = 0.0F;
				}
			}

			this.beamSpeed += 0.025F;
			if (this.beamSpeed > 1.0F) {
				this.beamSpeed = 1.0F;
			}

			return this.beamSpeed;
		}
	}

	@Override
	public Packet getPacket() {
		NbtCompound nbtCompound = new NbtCompound();
		this.toNbt(nbtCompound);
		return new BlockEntityUpdateS2CPacket(this.pos, 3, nbtCompound);
	}

	@Override
	public double getSquaredRenderDistance() {
		return 65536.0;
	}

	private int getFixedEffectId(int id) {
		if (id >= 0 && id < StatusEffect.STATUS_EFFECTS.length && StatusEffect.STATUS_EFFECTS[id] != null) {
			StatusEffect statusEffect = StatusEffect.STATUS_EFFECTS[id];
			return statusEffect != StatusEffect.SPEED
					&& statusEffect != StatusEffect.HASTE
					&& statusEffect != StatusEffect.RESISTANCE
					&& statusEffect != StatusEffect.JUMP_BOOST
					&& statusEffect != StatusEffect.STRENGTH
					&& statusEffect != StatusEffect.REGENERATION
				? 0
				: id;
		} else {
			return 0;
		}
	}

	@Override
	public void fromNbt(NbtCompound nbt) {
		super.fromNbt(nbt);
		this.primary = this.getFixedEffectId(nbt.getInt("Primary"));
		this.secondary = this.getFixedEffectId(nbt.getInt("Secondary"));
		this.levels = nbt.getInt("Levels");
	}

	@Override
	public void toNbt(NbtCompound nbt) {
		super.toNbt(nbt);
		nbt.putInt("Primary", this.primary);
		nbt.putInt("Secondary", this.secondary);
		nbt.putInt("Levels", this.levels);
	}

	@Override
	public int getInvSize() {
		return 1;
	}

	@Override
	public ItemStack getInvStack(int slot) {
		return slot == 0 ? this.priceStack : null;
	}

	@Override
	public ItemStack takeInvStack(int slot, int amount) {
		if (slot != 0 || this.priceStack == null) {
			return null;
		} else if (amount >= this.priceStack.count) {
			ItemStack itemStack = this.priceStack;
			this.priceStack = null;
			return itemStack;
		} else {
			this.priceStack.count -= amount;
			return new ItemStack(this.priceStack.getItem(), amount, this.priceStack.getData());
		}
	}

	@Override
	public ItemStack removeInvStack(int slot) {
		if (slot == 0 && this.priceStack != null) {
			ItemStack itemStack = this.priceStack;
			this.priceStack = null;
			return itemStack;
		} else {
			return null;
		}
	}

	@Override
	public void setInvStack(int slot, ItemStack stack) {
		if (slot == 0) {
			this.priceStack = stack;
		}
	}

	@Override
	public String getTranslationKey() {
		return this.hasCustomName() ? this.customName : "container.beacon";
	}

	@Override
	public boolean hasCustomName() {
		return this.customName != null && this.customName.length() > 0;
	}

	public void setCustomName(String customName) {
		this.customName = customName;
	}

	@Override
	public int getInvMaxStackAmount() {
		return 1;
	}

	@Override
	public boolean canPlayerUseInv(PlayerEntity player) {
		return this.world.getBlockEntity(this.pos) != this
			? false
			: !(player.squaredDistanceTo((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) > 64.0);
	}

	@Override
	public void onInvOpen(PlayerEntity player) {
	}

	@Override
	public void onInvClose(PlayerEntity player) {
	}

	@Override
	public boolean isValidInvStack(int slot, ItemStack stack) {
		return stack.getItem() == Items.EMERALD || stack.getItem() == Items.DIAMOND || stack.getItem() == Items.GOLD_INGOT || stack.getItem() == Items.IRON_INGOT;
	}

	@Override
	public String getId() {
		return "minecraft:beacon";
	}

	@Override
	public ScreenHandler createScreenHandler(PlayerInventory inventory, PlayerEntity player) {
		return new BeaconScreenHandler(inventory, this);
	}

	@Override
	public int getProperty(int key) {
		switch (key) {
			case 0:
				return this.levels;
			case 1:
				return this.primary;
			case 2:
				return this.secondary;
			default:
				return 0;
		}
	}

	@Override
	public void setProperty(int id, int value) {
		switch (id) {
			case 0:
				this.levels = value;
				break;
			case 1:
				this.primary = this.getFixedEffectId(value);
				break;
			case 2:
				this.secondary = this.getFixedEffectId(value);
		}
	}

	@Override
	public int getProperties() {
		return 3;
	}

	@Override
	public void clear() {
		this.priceStack = null;
	}

	@Override
	public boolean onBlockAction(int code, int data) {
		if (code == 1) {
			this.tickBeacon();
			return true;
		} else {
			return super.onBlockAction(code, data);
		}
	}

	public static class BeamSegment {
		private final float[] color;
		private int height;

		public BeamSegment(float[] fs) {
			this.color = fs;
			this.height = 1;
		}

		protected void increaseHeight() {
			this.height++;
		}

		public float[] getColor() {
			return this.color;
		}

		public int getHeight() {
			return this.height;
		}
	}
}
