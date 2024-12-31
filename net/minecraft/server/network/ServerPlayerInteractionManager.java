package net.minecraft.server.network;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.CommandBlock;
import net.minecraft.block.StructureBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.LockableScreenHandlerFactory;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
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
			Block block = blockState.getBlock();
			if (blockState.getMaterial() == Material.AIR) {
				this.failedToMine = false;
			} else {
				float f = blockState.method_11716(this.player, this.player.world, this.miningPos) * (float)(i + 1);
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
			Block block2 = blockState2.getBlock();
			if (blockState2.getMaterial() == Material.AIR) {
				this.world.setBlockBreakingInfo(this.player.getEntityId(), this.field_11764, -1);
				this.field_2858 = -1;
				this.mining = false;
			} else {
				int k = this.tickCounter - this.field_2848;
				float g = blockState2.method_11716(this.player, this.player.world, this.miningPos) * (float)(k + 1);
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
			BlockState blockState = this.world.getBlockState(pos);
			Block block = blockState.getBlock();
			if (this.gameMode.isAdventure()) {
				if (this.gameMode == GameMode.SPECTATOR) {
					return;
				}

				if (!this.player.canModifyWorld()) {
					ItemStack itemStack = this.player.getMainHandStack();
					if (itemStack == null) {
						return;
					}

					if (!itemStack.canDestroy(block)) {
						return;
					}
				}
			}

			this.world.extinguishFire(null, pos, direction);
			this.field_2848 = this.tickCounter;
			float f = 1.0F;
			if (blockState.getMaterial() != Material.AIR) {
				block.onBlockBreakStart(this.world, pos, this.player);
				f = blockState.method_11716(this.player, this.player.world, pos);
			}

			if (blockState.getMaterial() != Material.AIR && f >= 1.0F) {
				this.method_10766(pos);
			} else {
				this.mining = true;
				this.field_11764 = pos;
				int i = (int)(f * 10.0F);
				this.world.setBlockBreakingInfo(this.player.getEntityId(), pos, i);
				this.field_2858 = i;
			}
		}
	}

	public void method_10764(BlockPos blockPos) {
		if (blockPos.equals(this.field_11764)) {
			int i = this.tickCounter - this.field_2848;
			BlockState blockState = this.world.getBlockState(blockPos);
			if (blockState.getMaterial() != Material.AIR) {
				float f = blockState.method_11716(this.player, this.player.world, blockPos) * (float)(i + 1);
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
		boolean bl = this.world.setAir(pos);
		if (bl) {
			blockState.getBlock().onBreakByPlayer(this.world, pos, blockState);
		}

		return bl;
	}

	public boolean method_10766(BlockPos pos) {
		if (this.gameMode.isCreative() && this.player.getMainHandStack() != null && this.player.getMainHandStack().getItem() instanceof SwordItem) {
			return false;
		} else {
			BlockState blockState = this.world.getBlockState(pos);
			BlockEntity blockEntity = this.world.getBlockEntity(pos);
			Block block = blockState.getBlock();
			if ((block instanceof CommandBlock || block instanceof StructureBlock) && !this.player.method_13567()) {
				this.world.method_11481(pos, blockState, blockState, 3);
				return false;
			} else {
				if (this.gameMode.isAdventure()) {
					if (this.gameMode == GameMode.SPECTATOR) {
						return false;
					}

					if (!this.player.canModifyWorld()) {
						ItemStack itemStack = this.player.getMainHandStack();
						if (itemStack == null) {
							return false;
						}

						if (!itemStack.canDestroy(block)) {
							return false;
						}
					}
				}

				this.world.syncWorldEvent(this.player, 2001, pos, Block.getByBlockState(blockState));
				boolean bl = this.tryBreakBlock(pos);
				if (this.isCreative()) {
					this.player.networkHandler.sendPacket(new BlockUpdateS2CPacket(this.world, pos));
				} else {
					ItemStack itemStack2 = this.player.getMainHandStack();
					ItemStack itemStack3 = itemStack2 == null ? null : itemStack2.copy();
					boolean bl2 = this.player.method_13265(blockState);
					if (itemStack2 != null) {
						itemStack2.method_11306(this.world, blockState, pos, this.player);
						if (itemStack2.count == 0) {
							this.player.equipStack(Hand.MAIN_HAND, null);
						}
					}

					if (bl && bl2) {
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
			int i = item.count;
			int j = item.getData();
			TypedActionResult<ItemStack> typedActionResult = item.method_11390(world, player, hand);
			ItemStack itemStack = typedActionResult.getObject();
			if (itemStack == item && itemStack.count == i && itemStack.getMaxUseTime() <= 0 && itemStack.getData() == j) {
				return typedActionResult.getActionResult();
			} else {
				player.equipStack(hand, itemStack);
				if (this.isCreative()) {
					itemStack.count = i;
					if (itemStack.isDamageable()) {
						itemStack.setDamage(j);
					}
				}

				if (itemStack.count == 0) {
					player.equipStack(hand, null);
				}

				if (!player.method_13061()) {
					((ServerPlayerEntity)player).refreshScreenHandler(player.playerScreenHandler);
				}

				return typedActionResult.getActionResult();
			}
		}
	}

	public ActionResult method_12792(
		PlayerEntity playerEntity, World world, @Nullable ItemStack itemStack, Hand hand, BlockPos blockPos, Direction direction, float f, float g, float h
	) {
		if (this.gameMode == GameMode.SPECTATOR) {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof LockableScreenHandlerFactory) {
				Block block = world.getBlockState(blockPos).getBlock();
				LockableScreenHandlerFactory lockableScreenHandlerFactory = (LockableScreenHandlerFactory)blockEntity;
				if (lockableScreenHandlerFactory instanceof ChestBlockEntity && block instanceof ChestBlock) {
					lockableScreenHandlerFactory = ((ChestBlock)block).method_11583(world, blockPos);
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
			if (!playerEntity.isSneaking() || playerEntity.getMainHandStack() == null && playerEntity.getOffHandStack() == null) {
				BlockState blockState = world.getBlockState(blockPos);
				if (blockState.getBlock().method_421(world, blockPos, blockState, playerEntity, hand, itemStack, direction, f, g, h)) {
					return ActionResult.SUCCESS;
				}
			}

			if (itemStack == null) {
				return ActionResult.PASS;
			} else if (playerEntity.getItemCooldownManager().method_11382(itemStack.getItem())) {
				return ActionResult.PASS;
			} else {
				if (itemStack.getItem() instanceof BlockItem && !playerEntity.method_13567()) {
					Block block2 = ((BlockItem)itemStack.getItem()).getBlock();
					if (block2 instanceof CommandBlock || block2 instanceof StructureBlock) {
						return ActionResult.FAIL;
					}
				}

				if (this.isCreative()) {
					int i = itemStack.getData();
					int j = itemStack.count;
					ActionResult actionResult = itemStack.use(playerEntity, world, blockPos, hand, direction, f, g, h);
					itemStack.setDamage(i);
					itemStack.count = j;
					return actionResult;
				} else {
					return itemStack.use(playerEntity, world, blockPos, hand, direction, f, g, h);
				}
			}
		}
	}

	public void setWorld(ServerWorld world) {
		this.world = world;
	}
}
