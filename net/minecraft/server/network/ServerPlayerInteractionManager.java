package net.minecraft.server.network;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.CommandBlock;
import net.minecraft.block.StructureBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.LockableScreenHandlerFactory;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

public class ServerPlayerInteractionManager {
	public World world;
	public ServerPlayerEntity player;
	private GameMode gameMode = GameMode.NOT_SET;
	private boolean mining;
	private int field_2848;
	private BlockPos field_11764 = BlockPos.ORIGIN;
	private int tickCounter;
	private boolean failedToMine;
	private BlockPos miningPos = BlockPos.ORIGIN;
	private int field_2857;
	private int field_2858 = -1;

	public ServerPlayerInteractionManager(World world) {
		this.world = world;
	}

	public void setGameMode(GameMode gameMode) {
		this.gameMode = gameMode;
		gameMode.gameModeAbilities(this.player.abilities);
		this.player.sendAbilitiesUpdate();
		this.player.server.getPlayerManager().sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_GAME_MODE, this.player));
		this.world.updateSleepingStatus();
	}

	public GameMode getGameMode() {
		return this.gameMode;
	}

	public boolean isSurvival() {
		return this.gameMode.canBeDamaged();
	}

	public boolean isCreative() {
		return this.gameMode.isCreative();
	}

	public void method_2174(GameMode gameMode) {
		if (this.gameMode == GameMode.NOT_SET) {
			this.gameMode = gameMode;
		}

		this.setGameMode(this.gameMode);
	}

	public void tick() {
		this.tickCounter++;
		if (this.failedToMine) {
			int i = this.tickCounter - this.field_2857;
			BlockState blockState = this.world.getBlockState(this.miningPos);
			if (blockState.isAir()) {
				this.failedToMine = false;
			} else {
				float f = blockState.method_16860(this.player, this.player.world, this.miningPos) * (float)(i + 1);
				int j = (int)(f * 10.0F);
				if (j != this.field_2858) {
					this.world.setBlockBreakingInfo(this.player.getEntityId(), this.miningPos, j);
					this.field_2858 = j;
				}

				if (f >= 1.0F) {
					this.failedToMine = false;
					this.method_10766(this.miningPos);
				}
			}
		} else if (this.mining) {
			BlockState blockState2 = this.world.getBlockState(this.field_11764);
			if (blockState2.isAir()) {
				this.world.setBlockBreakingInfo(this.player.getEntityId(), this.field_11764, -1);
				this.field_2858 = -1;
				this.mining = false;
			} else {
				int k = this.tickCounter - this.field_2848;
				float g = blockState2.method_16860(this.player, this.player.world, this.miningPos) * (float)(k + 1);
				int l = (int)(g * 10.0F);
				if (l != this.field_2858) {
					this.world.setBlockBreakingInfo(this.player.getEntityId(), this.field_11764, l);
					this.field_2858 = l;
				}
			}
		}
	}

	public void processBlockBreakingAction(BlockPos pos, Direction direction) {
		if (this.isCreative()) {
			if (!this.world.extinguishFire(null, pos, direction)) {
				this.method_10766(pos);
			}
		} else {
			if (this.gameMode.isAdventure()) {
				if (this.gameMode == GameMode.SPECTATOR) {
					return;
				}

				if (!this.player.canModifyWorld()) {
					ItemStack itemStack = this.player.getMainHandStack();
					if (itemStack.isEmpty()) {
						return;
					}

					CachedBlockPosition cachedBlockPosition = new CachedBlockPosition(this.world, pos, false);
					if (!itemStack.method_16103(this.world.method_16314(), cachedBlockPosition)) {
						return;
					}
				}
			}

			this.world.extinguishFire(null, pos, direction);
			this.field_2848 = this.tickCounter;
			float f = 1.0F;
			BlockState blockState = this.world.getBlockState(pos);
			if (!blockState.isAir()) {
				blockState.method_16870(this.world, pos, this.player);
				f = blockState.method_16860(this.player, this.player.world, pos);
			}

			if (!blockState.isAir() && f >= 1.0F) {
				this.method_10766(pos);
			} else {
				this.mining = true;
				this.field_11764 = pos;
				int i = (int)(f * 10.0F);
				this.world.setBlockBreakingInfo(this.player.getEntityId(), pos, i);
				this.player.networkHandler.sendPacket(new BlockUpdateS2CPacket(this.world, pos));
				this.field_2858 = i;
			}
		}
	}

	public void method_10764(BlockPos blockPos) {
		if (blockPos.equals(this.field_11764)) {
			int i = this.tickCounter - this.field_2848;
			BlockState blockState = this.world.getBlockState(blockPos);
			if (!blockState.isAir()) {
				float f = blockState.method_16860(this.player, this.player.world, blockPos) * (float)(i + 1);
				if (f >= 0.7F) {
					this.mining = false;
					this.world.setBlockBreakingInfo(this.player.getEntityId(), blockPos, -1);
					this.method_10766(blockPos);
				} else if (!this.failedToMine) {
					this.mining = false;
					this.failedToMine = true;
					this.miningPos = blockPos;
					this.field_2857 = this.field_2848;
				}
			}
		}
	}

	public void method_10769() {
		this.mining = false;
		this.world.setBlockBreakingInfo(this.player.getEntityId(), this.field_11764, -1);
	}

	private boolean tryBreakBlock(BlockPos pos) {
		BlockState blockState = this.world.getBlockState(pos);
		blockState.getBlock().onBreakByPlayer(this.world, pos, blockState, this.player);
		boolean bl = this.world.method_8553(pos);
		if (bl) {
			blockState.getBlock().method_8674(this.world, pos, blockState);
		}

		return bl;
	}

	public boolean method_10766(BlockPos pos) {
		BlockState blockState = this.world.getBlockState(pos);
		if (!this.player.getMainHandStack().getItem().beforeBlockBreak(blockState, this.world, pos, this.player)) {
			return false;
		} else {
			BlockEntity blockEntity = this.world.getBlockEntity(pos);
			Block block = blockState.getBlock();
			if ((block instanceof CommandBlock || block instanceof StructureBlock) && !this.player.method_15936()) {
				this.world.method_11481(pos, blockState, blockState, 3);
				return false;
			} else {
				if (this.gameMode.isAdventure()) {
					if (this.gameMode == GameMode.SPECTATOR) {
						return false;
					}

					if (!this.player.canModifyWorld()) {
						ItemStack itemStack = this.player.getMainHandStack();
						if (itemStack.isEmpty()) {
							return false;
						}

						CachedBlockPosition cachedBlockPosition = new CachedBlockPosition(this.world, pos, false);
						if (!itemStack.method_16103(this.world.method_16314(), cachedBlockPosition)) {
							return false;
						}
					}
				}

				boolean bl = this.tryBreakBlock(pos);
				if (!this.isCreative()) {
					ItemStack itemStack2 = this.player.getMainHandStack();
					boolean bl2 = this.player.method_13265(blockState);
					itemStack2.method_11306(this.world, blockState, pos, this.player);
					if (bl && bl2) {
						ItemStack itemStack3 = itemStack2.isEmpty() ? ItemStack.EMPTY : itemStack2.copy();
						blockState.getBlock().method_8651(this.world, this.player, pos, blockState, blockEntity, itemStack3);
					}
				}

				return bl;
			}
		}
	}

	public ActionResult method_12791(PlayerEntity player, World world, ItemStack item, Hand hand) {
		if (this.gameMode == GameMode.SPECTATOR) {
			return ActionResult.PASS;
		} else if (player.getItemCooldownManager().method_11382(item.getItem())) {
			return ActionResult.PASS;
		} else {
			int i = item.getCount();
			int j = item.getDamage();
			TypedActionResult<ItemStack> typedActionResult = item.method_11390(world, player, hand);
			ItemStack itemStack = typedActionResult.getObject();
			if (itemStack == item && itemStack.getCount() == i && itemStack.getMaxUseTime() <= 0 && itemStack.getDamage() == j) {
				return typedActionResult.getActionResult();
			} else if (typedActionResult.getActionResult() == ActionResult.FAIL && itemStack.getMaxUseTime() > 0 && !player.method_13061()) {
				return typedActionResult.getActionResult();
			} else {
				player.equipStack(hand, itemStack);
				if (this.isCreative()) {
					itemStack.setCount(i);
					if (itemStack.isDamageable()) {
						itemStack.setDamage(j);
					}
				}

				if (itemStack.isEmpty()) {
					player.equipStack(hand, ItemStack.EMPTY);
				}

				if (!player.method_13061()) {
					((ServerPlayerEntity)player).refreshScreenHandler(player.playerScreenHandler);
				}

				return typedActionResult.getActionResult();
			}
		}
	}

	public ActionResult method_12792(
		PlayerEntity playerEntity, World world, ItemStack itemStack, Hand hand, BlockPos blockPos, Direction direction, float f, float g, float h
	) {
		BlockState blockState = world.getBlockState(blockPos);
		if (this.gameMode == GameMode.SPECTATOR) {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof LockableScreenHandlerFactory) {
				Block block = blockState.getBlock();
				LockableScreenHandlerFactory lockableScreenHandlerFactory = (LockableScreenHandlerFactory)blockEntity;
				if (lockableScreenHandlerFactory instanceof ChestBlockEntity && block instanceof ChestBlock) {
					lockableScreenHandlerFactory = ((ChestBlock)block).getInventory(blockState, world, blockPos, false);
				}

				if (lockableScreenHandlerFactory != null) {
					playerEntity.openInventory(lockableScreenHandlerFactory);
					return ActionResult.SUCCESS;
				}
			} else if (blockEntity instanceof Inventory) {
				playerEntity.openInventory((Inventory)blockEntity);
				return ActionResult.SUCCESS;
			}

			return ActionResult.PASS;
		} else {
			boolean bl = !playerEntity.getMainHandStack().isEmpty() || !playerEntity.getOffHandStack().isEmpty();
			boolean bl2 = playerEntity.isSneaking() && bl;
			if (!bl2 && blockState.onUse(world, blockPos, playerEntity, hand, direction, f, g, h)) {
				return ActionResult.SUCCESS;
			} else if (!itemStack.isEmpty() && !playerEntity.getItemCooldownManager().method_11382(itemStack.getItem())) {
				ItemUsageContext itemUsageContext = new ItemUsageContext(playerEntity, playerEntity.getStackInHand(hand), blockPos, direction, f, g, h);
				if (this.isCreative()) {
					int i = itemStack.getCount();
					ActionResult actionResult = itemStack.method_16097(itemUsageContext);
					itemStack.setCount(i);
					return actionResult;
				} else {
					return itemStack.method_16097(itemUsageContext);
				}
			} else {
				return ActionResult.PASS;
			}
		}
	}

	public void setWorld(ServerWorld world) {
		this.world = world;
	}
}
