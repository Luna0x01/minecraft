package net.minecraft;

import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ChatMessageType;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class class_3547 extends Item {
	public class_3547(Item.Settings settings) {
		super(settings);
	}

	@Override
	public boolean hasEnchantmentGlint(ItemStack stack) {
		return true;
	}

	@Override
	public boolean beforeBlockBreak(BlockState state, World world, BlockPos pos, PlayerEntity player) {
		if (!world.isClient) {
			this.method_16035(player, state, world, pos, false, player.getStackInHand(Hand.MAIN_HAND));
		}

		return false;
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		PlayerEntity playerEntity = itemUsageContext.getPlayer();
		World world = itemUsageContext.getWorld();
		if (!world.isClient && playerEntity != null) {
			BlockPos blockPos = itemUsageContext.getBlockPos();
			this.method_16035(playerEntity, world.getBlockState(blockPos), world, blockPos, true, itemUsageContext.getItemStack());
		}

		return ActionResult.SUCCESS;
	}

	private void method_16035(PlayerEntity playerEntity, BlockState blockState, IWorld iWorld, BlockPos blockPos, boolean bl, ItemStack itemStack) {
		if (playerEntity.method_15936()) {
			Block block = blockState.getBlock();
			StateManager<Block, BlockState> stateManager = block.getStateManager();
			Collection<Property<?>> collection = stateManager.getProperties();
			String string = Registry.BLOCK.getId(block).toString();
			if (collection.isEmpty()) {
				method_16036(playerEntity, new TranslatableText(this.getTranslationKey() + ".empty", string));
			} else {
				NbtCompound nbtCompound = itemStack.getOrCreateNbtCompound("DebugProperty");
				String string2 = nbtCompound.getString(string);
				Property<?> property = stateManager.getProperty(string2);
				if (bl) {
					if (property == null) {
						property = (Property<?>)collection.iterator().next();
					}

					BlockState blockState2 = method_16038(blockState, property, playerEntity.isSneaking());
					iWorld.setBlockState(blockPos, blockState2, 18);
					method_16036(playerEntity, new TranslatableText(this.getTranslationKey() + ".update", property.getName(), method_16037(blockState2, property)));
				} else {
					property = method_16039(collection, property, playerEntity.isSneaking());
					String string3 = property.getName();
					nbtCompound.putString(string, string3);
					method_16036(playerEntity, new TranslatableText(this.getTranslationKey() + ".select", string3, method_16037(blockState, property)));
				}
			}
		}
	}

	private static <T extends Comparable<T>> BlockState method_16038(BlockState blockState, Property<T> property, boolean bl) {
		return blockState.withProperty(property, method_16039(property.getValues(), blockState.getProperty(property), bl));
	}

	private static <T> T method_16039(Iterable<T> iterable, @Nullable T object, boolean bl) {
		return bl ? Util.method_20228(iterable, object) : Util.method_20220(iterable, object);
	}

	private static void method_16036(PlayerEntity playerEntity, Text text) {
		((ServerPlayerEntity)playerEntity).method_21277(text, ChatMessageType.GAME_INFO);
	}

	private static <T extends Comparable<T>> String method_16037(BlockState blockState, Property<T> property) {
		return property.name(blockState.getProperty(property));
	}
}
