package net.minecraft;

import com.mojang.authlib.GameProfile;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;

public class class_3714 extends SkullBlock {
	protected class_3714(Block.Builder builder) {
		super(SkullBlock.class_3723.PLAYER, builder);
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
		super.onPlaced(world, pos, state, placer, itemStack);
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof SkullBlockEntity) {
			SkullBlockEntity skullBlockEntity = (SkullBlockEntity)blockEntity;
			GameProfile gameProfile = null;
			if (itemStack.hasNbt()) {
				NbtCompound nbtCompound = itemStack.getNbt();
				if (nbtCompound.contains("SkullOwner", 10)) {
					gameProfile = NbtHelper.toGameProfile(nbtCompound.getCompound("SkullOwner"));
				} else if (nbtCompound.contains("SkullOwner", 8) && !StringUtils.isBlank(nbtCompound.getString("SkullOwner"))) {
					gameProfile = new GameProfile(null, nbtCompound.getString("SkullOwner"));
				}
			}

			skullBlockEntity.method_16841(gameProfile);
		}
	}
}
