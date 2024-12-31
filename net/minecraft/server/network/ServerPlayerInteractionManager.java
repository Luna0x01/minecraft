package net.minecraft.server.network;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.LockableScreenHandlerFactory;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.level.LevelInfo;

public class ServerPlayerInteractionManager {
	public World world;
	public ServerPlayerEntity player;
	private LevelInfo.GameMode gameMode = LevelInfo.GameMode.NOT_SET;
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

	public void setGameMode(LevelInfo.GameMode gamemode) {
		this.gameMode = gamemode;
		gamemode.setAbilities(this.player.abilities);
		this.player.sendAbilitiesUpdate();
		this.player.server.getPlayerManager().sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_GAME_MODE, this.player));
	}

	public LevelInfo.GameMode getGameMode() {
		return this.gameMode;
	}

	public boolean isSurvival() {
		return this.gameMode.isSurvivalLike();
	}

	public boolean isCreative() {
		return this.gameMode.isCreative();
	}

	public void setGameModeIfNotPresent(LevelInfo.GameMode gameMode) {
		if (this.gameMode == LevelInfo.GameMode.NOT_SET) {
			this.gameMode = gameMode;
		}

		this.setGameMode(this.gameMode);
	}

	public void tick() {
		this.tickCounter++;
		if (this.failedToMine) {
			int i = this.tickCounter - this.field_2857;
			Block block = this.world.getBlockState(this.miningPos).getBlock();
			if (block.getMaterial() == Material.AIR) {
				this.failedToMine = false;
			} else {
				float f = block.calcBlockBreakingData(this.player, this.player.world, this.miningPos) * (float)(i + 1);
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
			Block block2 = this.world.getBlockState(this.field_11764).getBlock();
			if (block2.getMaterial() == Material.AIR) {
				this.world.setBlockBreakingInfo(this.player.getEntityId(), this.field_11764, -1);
				this.field_2858 = -1;
				this.mining = false;
			} else {
				int k = this.tickCounter - this.field_2848;
				float g = block2.calcBlockBreakingData(this.player, this.player.world, this.miningPos) * (float)(k + 1);
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
			Block block = this.world.getBlockState(pos).getBlock();
			if (this.gameMode.shouldLimitWorldModification()) {
				if (this.gameMode == LevelInfo.GameMode.SPECTATOR) {
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
			if (block.getMaterial() != Material.AIR) {
				block.onBlockBreakStart(this.world, pos, this.player);
				f = block.calcBlockBreakingData(this.player, this.player.world, pos);
			}

			if (block.getMaterial() != Material.AIR && f >= 1.0F) {
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
			Block block = this.world.getBlockState(blockPos).getBlock();
			if (block.getMaterial() != Material.AIR) {
				float f = block.calcBlockBreakingData(this.player, this.player.world, blockPos) * (float)(i + 1);
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
		if (this.gameMode.isCreative() && this.player.getStackInHand() != null && this.player.getStackInHand().getItem() instanceof SwordItem) {
			return false;
		} else {
			BlockState blockState = this.world.getBlockState(pos);
			BlockEntity blockEntity = this.world.getBlockEntity(pos);
			if (this.gameMode.shouldLimitWorldModification()) {
				if (this.gameMode == LevelInfo.GameMode.SPECTATOR) {
					return false;
				}

				if (!this.player.canModifyWorld()) {
					ItemStack itemStack = this.player.getMainHandStack();
					if (itemStack == null) {
						return false;
					}

					if (!itemStack.canDestroy(blockState.getBlock())) {
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
				boolean bl2 = this.player.isUsingEffectiveTool(blockState.getBlock());
				if (itemStack2 != null) {
					itemStack2.onBlockBroken(this.world, blockState.getBlock(), pos, this.player);
					if (itemStack2.count == 0) {
						this.player.removeSelectedSlotItem();
					}
				}

				if (bl && bl2) {
					blockState.getBlock().harvest(this.world, this.player, pos, blockState, blockEntity);
				}
			}

			return bl;
		}
	}

	public boolean interactItem(PlayerEntity player, World world, ItemStack stack) {
		if (this.gameMode == LevelInfo.GameMode.SPECTATOR) {
			return false;
		} else {
			int i = stack.count;
			int j = stack.getData();
			ItemStack itemStack = stack.onStartUse(world, player);
			if (itemStack != stack || itemStack != null && (itemStack.count != i || itemStack.getMaxUseTime() > 0 || itemStack.getData() != j)) {
				player.inventory.main[player.inventory.selectedSlot] = itemStack;
				if (this.isCreative()) {
					itemStack.count = i;
					if (itemStack.isDamageable()) {
						itemStack.setDamage(j);
					}
				}

				if (itemStack.count == 0) {
					player.inventory.main[player.inventory.selectedSlot] = null;
				}

				if (!player.isUsingItem()) {
					((ServerPlayerEntity)player).refreshScreenHandler(player.playerScreenHandler);
				}

				return true;
			} else {
				return false;
			}
		}
	}

	public boolean interactBlock(PlayerEntity player, World world, ItemStack stack, BlockPos pos, Direction direction, float x, float y, float z) {
		if (this.gameMode == LevelInfo.GameMode.SPECTATOR) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof LockableScreenHandlerFactory) {
				Block block = world.getBlockState(pos).getBlock();
				LockableScreenHandlerFactory lockableScreenHandlerFactory = (LockableScreenHandlerFactory)blockEntity;
				if (lockableScreenHandlerFactory instanceof ChestBlockEntity && block instanceof ChestBlock) {
					lockableScreenHandlerFactory = ((ChestBlock)block).createScreenHandlerFactory(world, pos);
				}

				if (lockableScreenHandlerFactory != null) {
					player.openInventory(lockableScreenHandlerFactory);
					return true;
				}
			} else if (blockEntity instanceof Inventory) {
				player.openInventory((Inventory)blockEntity);
				return true;
			}

			return false;
		} else {
			if (!player.isSneaking() || player.getStackInHand() == null) {
				BlockState blockState = world.getBlockState(pos);
				if (blockState.getBlock().onUse(world, pos, blockState, player, direction, x, y, z)) {
					return true;
				}
			}

			if (stack == null) {
				return false;
			} else if (this.isCreative()) {
				int i = stack.getData();
				int j = stack.count;
				boolean bl = stack.use(player, world, pos, direction, x, y, z);
				stack.setDamage(i);
				stack.count = j;
				return bl;
			} else {
				return stack.use(player, world, pos, direction, x, y, z);
			}
		}
	}

	public void setWorld(ServerWorld world) {
		this.world = world;
	}
}
