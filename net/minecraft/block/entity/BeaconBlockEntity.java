package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StainedGlassBlock;
import net.minecraft.block.StainedGlassPaneBlock;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.BeaconScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

public class BeaconBlockEntity extends LockableContainerBlockEntity implements Tickable, SidedInventory {
	public static final StatusEffect[][] EFFECTS = new StatusEffect[][]{
		{StatusEffects.SPEED, StatusEffects.HASTE}, {StatusEffects.RESISTANCE, StatusEffects.JUMP_BOOST}, {StatusEffects.STRENGTH}, {StatusEffects.REGENERATION}
	};
	private static final Set<StatusEffect> field_12838 = Sets.newHashSet();
	private final List<BeaconBlockEntity.BeamSegment> beamSegments = Lists.newArrayList();
	private long lastBeamRenderTime;
	private float beamSpeed;
	private boolean hasValidBase;
	private int levels = -1;
	@Nullable
	private StatusEffect field_12839;
	@Nullable
	private StatusEffect field_12840;
	private ItemStack priceStack = ItemStack.EMPTY;
	private String customName;

	@Override
	public void tick() {
		if (this.world.getLastUpdateTime() % 80L == 0L) {
			this.tickBeacon();
		}
	}

	public void tickBeacon() {
		if (this.world != null) {
			this.updateBeam();
			this.addPlayerEffects();
		}
	}

	private void addPlayerEffects() {
		if (this.hasValidBase && this.levels > 0 && !this.world.isClient && this.field_12839 != null) {
			double d = (double)(this.levels * 10 + 10);
			int i = 0;
			if (this.levels >= 4 && this.field_12839 == this.field_12840) {
				i = 1;
			}

			int j = (9 + this.levels * 2) * 20;
			int k = this.pos.getX();
			int l = this.pos.getY();
			int m = this.pos.getZ();
			Box box = new Box((double)k, (double)l, (double)m, (double)(k + 1), (double)(l + 1), (double)(m + 1))
				.expand(d)
				.stretch(0.0, (double)this.world.getMaxBuildHeight(), 0.0);
			List<PlayerEntity> list = this.world.getEntitiesInBox(PlayerEntity.class, box);

			for (PlayerEntity playerEntity : list) {
				playerEntity.addStatusEffect(new StatusEffectInstance(this.field_12839, j, i, true, true));
			}

			if (this.levels >= 4 && this.field_12839 != this.field_12840 && this.field_12840 != null) {
				for (PlayerEntity playerEntity2 : list) {
					playerEntity2.addStatusEffect(new StatusEffectInstance(this.field_12840, j, 0, true, true));
				}
			}
		}
	}

	private void updateBeam() {
		int i = this.pos.getX();
		int j = this.pos.getY();
		int k = this.pos.getZ();
		int l = this.levels;
		this.levels = 0;
		this.beamSegments.clear();
		this.hasValidBase = true;
		BeaconBlockEntity.BeamSegment beamSegment = new BeaconBlockEntity.BeamSegment(DyeColor.WHITE.getColorComponents());
		this.beamSegments.add(beamSegment);
		boolean bl = true;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int m = j + 1; m < 256; m++) {
			BlockState blockState = this.world.getBlockState(mutable.setPosition(i, m, k));
			float[] fs;
			if (blockState.getBlock() == Blocks.STAINED_GLASS) {
				fs = ((DyeColor)blockState.get(StainedGlassBlock.COLOR)).getColorComponents();
			} else {
				if (blockState.getBlock() != Blocks.STAINED_GLASS_PANE) {
					if (blockState.getOpacity() >= 15 && blockState.getBlock() != Blocks.BEDROCK) {
						this.hasValidBase = false;
						this.beamSegments.clear();
						break;
					}

					beamSegment.increaseHeight();
					continue;
				}

				fs = ((DyeColor)blockState.get(StainedGlassPaneBlock.COLOR)).getColorComponents();
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
				int o = j - n;
				if (o < 0) {
					break;
				}

				boolean bl2 = true;

				for (int p = i - n; p <= i + n && bl2; p++) {
					for (int q = k - n; q <= k + n; q++) {
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

		if (!this.world.isClient && l < this.levels) {
			for (ServerPlayerEntity serverPlayerEntity : this.world
				.getEntitiesInBox(ServerPlayerEntity.class, new Box((double)i, (double)j, (double)k, (double)i, (double)(j - 4), (double)k).expand(10.0, 5.0, 10.0))) {
				AchievementsAndCriterions.field_16339.method_15081(serverPlayerEntity, this);
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

	public int method_14362() {
		return this.levels;
	}

	@Nullable
	@Override
	public BlockEntityUpdateS2CPacket getUpdatePacket() {
		return new BlockEntityUpdateS2CPacket(this.pos, 3, this.getUpdatePacketContent());
	}

	@Override
	public NbtCompound getUpdatePacketContent() {
		return this.toNbt(new NbtCompound());
	}

	@Override
	public double getSquaredRenderDistance() {
		return 65536.0;
	}

	@Nullable
	private static StatusEffect method_11645(int i) {
		StatusEffect statusEffect = StatusEffect.byIndex(i);
		return field_12838.contains(statusEffect) ? statusEffect : null;
	}

	@Override
	public void fromNbt(NbtCompound nbt) {
		super.fromNbt(nbt);
		this.field_12839 = method_11645(nbt.getInt("Primary"));
		this.field_12840 = method_11645(nbt.getInt("Secondary"));
		this.levels = nbt.getInt("Levels");
	}

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		super.toNbt(nbt);
		nbt.putInt("Primary", StatusEffect.getIndex(this.field_12839));
		nbt.putInt("Secondary", StatusEffect.getIndex(this.field_12840));
		nbt.putInt("Levels", this.levels);
		return nbt;
	}

	@Override
	public int getInvSize() {
		return 1;
	}

	@Override
	public boolean isEmpty() {
		return this.priceStack.isEmpty();
	}

	@Override
	public ItemStack getInvStack(int slot) {
		return slot == 0 ? this.priceStack : ItemStack.EMPTY;
	}

	@Override
	public ItemStack takeInvStack(int slot, int amount) {
		if (slot != 0 || this.priceStack.isEmpty()) {
			return ItemStack.EMPTY;
		} else if (amount >= this.priceStack.getCount()) {
			ItemStack itemStack = this.priceStack;
			this.priceStack = ItemStack.EMPTY;
			return itemStack;
		} else {
			return this.priceStack.split(amount);
		}
	}

	@Override
	public ItemStack removeInvStack(int slot) {
		if (slot == 0) {
			ItemStack itemStack = this.priceStack;
			this.priceStack = ItemStack.EMPTY;
			return itemStack;
		} else {
			return ItemStack.EMPTY;
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
		return this.customName != null && !this.customName.isEmpty();
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
				return StatusEffect.getIndex(this.field_12839);
			case 2:
				return StatusEffect.getIndex(this.field_12840);
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
				this.field_12839 = method_11645(value);
				break;
			case 2:
				this.field_12840 = method_11645(value);
		}
	}

	@Override
	public int getProperties() {
		return 3;
	}

	@Override
	public void clear() {
		this.priceStack = ItemStack.EMPTY;
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

	@Override
	public int[] getAvailableSlots(Direction side) {
		return new int[0];
	}

	@Override
	public boolean canInsertInvStack(int slot, ItemStack stack, Direction dir) {
		return false;
	}

	@Override
	public boolean canExtractInvStack(int slot, ItemStack stack, Direction dir) {
		return false;
	}

	static {
		for (StatusEffect[] statusEffects : EFFECTS) {
			Collections.addAll(field_12838, statusEffects);
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
