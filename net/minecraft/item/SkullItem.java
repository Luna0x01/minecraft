package net.minecraft.item;

import com.mojang.authlib.GameProfile;
import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.ActionResult;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;

public class SkullItem extends Item {
	private static final String[] SKULL_TYPES = new String[]{"skeleton", "wither", "zombie", "char", "creeper", "dragon"};

	public SkullItem() {
		this.setItemGroup(ItemGroup.DECORATIONS);
		this.setMaxDamage(0);
		this.setUnbreakable(true);
	}

	@Override
	public ActionResult use(PlayerEntity player, World world, BlockPos pos, Hand hand, Direction direction, float x, float y, float z) {
		if (direction == Direction.DOWN) {
			return ActionResult.FAIL;
		} else {
			BlockState blockState = world.getBlockState(pos);
			Block block = blockState.getBlock();
			boolean bl = block.method_8638(world, pos);
			if (!bl) {
				if (!world.getBlockState(pos).getMaterial().isSolid()) {
					return ActionResult.FAIL;
				}

				pos = pos.offset(direction);
			}

			ItemStack itemStack = player.getStackInHand(hand);
			if (!player.canModify(pos, direction, itemStack) || !Blocks.SKULL.canBePlacedAtPos(world, pos)) {
				return ActionResult.FAIL;
			} else if (world.isClient) {
				return ActionResult.SUCCESS;
			} else {
				world.setBlockState(pos, Blocks.SKULL.getDefaultState().with(SkullBlock.FACING, direction), 11);
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
							} else if (nbtCompound.contains("SkullOwner", 8) && !StringUtils.isBlank(nbtCompound.getString("SkullOwner"))) {
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

				if (player instanceof ServerPlayerEntity) {
					AchievementsAndCriterions.field_16352.method_14369((ServerPlayerEntity)player, pos, itemStack);
				}

				itemStack.decrement(1);
				return ActionResult.SUCCESS;
			}
		}
	}

	@Override
	public void appendToItemGroup(ItemGroup group, DefaultedList<ItemStack> stacks) {
		if (this.canAddTo(group)) {
			for (int i = 0; i < SKULL_TYPES.length; i++) {
				stacks.add(new ItemStack(this, 1, i));
			}
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
		if (nbt.contains("SkullOwner", 8) && !StringUtils.isBlank(nbt.getString("SkullOwner"))) {
			GameProfile gameProfile = new GameProfile(null, nbt.getString("SkullOwner"));
			gameProfile = SkullBlockEntity.loadProperties(gameProfile);
			nbt.put("SkullOwner", NbtHelper.fromGameProfile(new NbtCompound(), gameProfile));
			return true;
		} else {
			return false;
		}
	}
}
