package net.minecraft.block.entity;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Tickable;
import net.minecraft.util.UserCache;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class SkullBlockEntity extends BlockEntity implements Tickable {
	private GameProfile owner;
	private int field_12854;
	private boolean field_12855;
	private boolean field_18646 = true;
	private static UserCache field_12856;
	private static MinecraftSessionService field_12857;

	public SkullBlockEntity() {
		super(BlockEntityType.SKULL);
	}

	public static void method_11666(UserCache userCache) {
		field_12856 = userCache;
	}

	public static void method_11665(MinecraftSessionService minecraftSessionService) {
		field_12857 = minecraftSessionService;
	}

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		super.toNbt(nbt);
		if (this.owner != null) {
			NbtCompound nbtCompound = new NbtCompound();
			NbtHelper.fromGameProfile(nbtCompound, this.owner);
			nbt.put("Owner", nbtCompound);
		}

		return nbt;
	}

	@Override
	public void fromNbt(NbtCompound nbt) {
		super.fromNbt(nbt);
		if (nbt.contains("Owner", 10)) {
			this.method_16841(NbtHelper.toGameProfile(nbt.getCompound("Owner")));
		} else if (nbt.contains("ExtraType", 8)) {
			String string = nbt.getString("ExtraType");
			if (!ChatUtil.isEmpty(string)) {
				this.method_16841(new GameProfile(null, string));
			}
		}
	}

	@Override
	public void tick() {
		Block block = this.method_16783().getBlock();
		if (block == Blocks.DRAGON_HEAD || block == Blocks.DRAGON_WALL_HEAD) {
			if (this.world.isReceivingRedstonePower(this.pos)) {
				this.field_12855 = true;
				this.field_12854++;
			} else {
				this.field_12855 = false;
			}
		}
	}

	public float method_11664(float f) {
		return this.field_12855 ? (float)this.field_12854 + f : (float)this.field_12854;
	}

	@Nullable
	public GameProfile getOwner() {
		return this.owner;
	}

	@Nullable
	@Override
	public BlockEntityUpdateS2CPacket getUpdatePacket() {
		return new BlockEntityUpdateS2CPacket(this.pos, 4, this.getUpdatePacketContent());
	}

	@Override
	public NbtCompound getUpdatePacketContent() {
		return this.toNbt(new NbtCompound());
	}

	public void method_16841(@Nullable GameProfile gameProfile) {
		this.owner = gameProfile;
		this.loadOwnerProperties();
	}

	private void loadOwnerProperties() {
		this.owner = loadProperties(this.owner);
		this.markDirty();
	}

	public static GameProfile loadProperties(GameProfile profile) {
		if (profile != null && !ChatUtil.isEmpty(profile.getName())) {
			if (profile.isComplete() && profile.getProperties().containsKey("textures")) {
				return profile;
			} else if (field_12856 != null && field_12857 != null) {
				GameProfile gameProfile = field_12856.findByName(profile.getName());
				if (gameProfile == null) {
					return profile;
				} else {
					Property property = (Property)Iterables.getFirst(gameProfile.getProperties().get("textures"), null);
					if (property == null) {
						gameProfile = field_12857.fillProfileProperties(gameProfile, true);
					}

					return gameProfile;
				}
			} else {
				return profile;
			}
		} else {
			return profile;
		}
	}

	public static void method_16840(BlockView blockView, BlockPos blockPos) {
		BlockEntity blockEntity = blockView.getBlockEntity(blockPos);
		if (blockEntity instanceof SkullBlockEntity) {
			SkullBlockEntity skullBlockEntity = (SkullBlockEntity)blockEntity;
			skullBlockEntity.field_18646 = false;
		}
	}

	public boolean method_16842() {
		return this.field_18646;
	}
}
