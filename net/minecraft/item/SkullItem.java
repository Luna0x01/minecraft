package net.minecraft.item;

import com.mojang.authlib.GameProfile;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class SkullItem extends Item {
	private static final String[] SKULL_TYPES = new String[]{"skeleton", "wither", "zombie", "char", "creeper"};

	public SkullItem() {
		this.setItemGroup(ItemGroup.DECORATIONS);
		this.setMaxDamage(0);
		this.setUnbreakable(true);
	}

	@Override
	public boolean use(ItemStack itemStack, PlayerEntity player, World world, BlockPos pos, Direction direction, float facingX, float facingY, float facingZ) {
		if (direction == Direction.DOWN) {
			return false;
		} else {
			BlockState blockState = world.getBlockState(pos);
			Block block = blockState.getBlock();
			boolean bl = block.isReplaceable(world, pos);
			if (!bl) {
				if (!world.getBlockState(pos).getBlock().getMaterial().isSolid()) {
					return false;
				}

				pos = pos.offset(direction);
			}

			if (!player.canModify(pos, direction, itemStack)) {
				return false;
			} else if (!Blocks.SKULL.canBePlacedAtPos(world, pos)) {
				return false;
			} else {
				if (!world.isClient) {
					world.setBlockState(pos, Blocks.SKULL.getDefaultState().with(SkullBlock.FACING, direction), 3);
					int i = 0;
					if (direction == Direction.UP) {
						i = MathHelper.floor((double)(player.yaw * 16.0F / 360.0F) + 0.5) & 15;
					}

					BlockEntity blockEntity = world.getBlockEntity(pos);
					if (blockEntity instanceof SkullBlockEntity) {
						SkullBlockEntity skullBlockEntity = (SkullBlockEntity)blockEntity;
						if (itemStack.getData() == 3) {
							GameProfile gameProfile = null;
							if (itemStack.hasNbt()) {
								NbtCompound nbtCompound = itemStack.getNbt();
								if (nbtCompound.contains("SkullOwner", 10)) {
									gameProfile = NbtHelper.toGameProfile(nbtCompound.getCompound("SkullOwner"));
								} else if (nbtCompound.contains("SkullOwner", 8) && nbtCompound.getString("SkullOwner").length() > 0) {
									gameProfile = new GameProfile(null, nbtCompound.getString("SkullOwner"));
								}
							}

							skullBlockEntity.setOwnerAndType(gameProfile);
						} else {
							skullBlockEntity.setSkullType(itemStack.getData());
						}

						skullBlockEntity.setRotation(i);
						Blocks.SKULL.trySpawnEntity(world, pos, skullBlockEntity);
					}

					itemStack.count--;
				}

				return true;
			}
		}
	}

	@Override
	public void appendItemStacks(Item item, ItemGroup group, List<ItemStack> list) {
		for (int i = 0; i < SKULL_TYPES.length; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	public int getMeta(int i) {
		return i;
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		int i = stack.getData();
		if (i < 0 || i >= SKULL_TYPES.length) {
			i = 0;
		}

		return super.getTranslationKey() + "." + SKULL_TYPES[i];
	}

	@Override
	public String getDisplayName(ItemStack stack) {
		if (stack.getData() == 3 && stack.hasNbt()) {
			if (stack.getNbt().contains("SkullOwner", 8)) {
				return CommonI18n.translate("item.skull.player.name", stack.getNbt().getString("SkullOwner"));
			}

			if (stack.getNbt().contains("SkullOwner", 10)) {
				NbtCompound nbtCompound = stack.getNbt().getCompound("SkullOwner");
				if (nbtCompound.contains("Name", 8)) {
					return CommonI18n.translate("item.skull.player.name", nbtCompound.getString("Name"));
				}
			}
		}

		return super.getDisplayName(stack);
	}

	@Override
	public boolean postProcessNbt(NbtCompound nbt) {
		super.postProcessNbt(nbt);
		if (nbt.contains("SkullOwner", 8) && nbt.getString("SkullOwner").length() > 0) {
			GameProfile gameProfile = new GameProfile(null, nbt.getString("SkullOwner"));
			gameProfile = SkullBlockEntity.loadProperties(gameProfile);
			nbt.put("SkullOwner", NbtHelper.fromGameProfile(new NbtCompound(), gameProfile));
			return true;
		} else {
			return false;
		}
	}
}
