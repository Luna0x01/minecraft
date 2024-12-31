package net.minecraft.client.network;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.c2s.play.ButtonClickC2SPacket;
import net.minecraft.network.packet.c2s.play.ClickWindowC2SPacket;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.stat.StatHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.level.LevelInfo;

public class ClientPlayerInteractionManager {
	private final MinecraftClient client;
	private final ClientPlayNetworkHandler networkHandler;
	private BlockPos currentBreakingPos = new BlockPos(-1, -1, -1);
	private ItemStack selectedStack;
	private float currentBreakingProgress;
	private float blockBreakingSoundCooldown;
	private int blockBreakingCooldown;
	private boolean breakingBlock;
	private LevelInfo.GameMode gameMode = LevelInfo.GameMode.SURVIVAL;
	private int lastSelectedSlot;

	public ClientPlayerInteractionManager(MinecraftClient minecraftClient, ClientPlayNetworkHandler clientPlayNetworkHandler) {
		this.client = minecraftClient;
		this.networkHandler = clientPlayNetworkHandler;
	}

	public static void breakBlockOrFire(MinecraftClient client, ClientPlayerInteractionManager interactionManager, BlockPos pos, Direction direction) {
		if (!client.world.extinguishFire(client.player, pos, direction)) {
			interactionManager.breakBlock(pos, direction);
		}
	}

	public void copyAbilities(PlayerEntity player) {
		this.gameMode.setAbilities(player.abilities);
	}

	public boolean isSpectator() {
		return this.gameMode == LevelInfo.GameMode.SPECTATOR;
	}

	public void setGameMode(LevelInfo.GameMode gameMode) {
		this.gameMode = gameMode;
		this.gameMode.setAbilities(this.client.player.abilities);
	}

	public void flipPlayer(PlayerEntity player) {
		player.yaw = -180.0F;
	}

	public boolean hasStatusBars() {
		return this.gameMode.isSurvivalLike();
	}

	public boolean breakBlock(BlockPos pos, Direction direction) {
		if (this.gameMode.shouldLimitWorldModification()) {
			if (this.gameMode == LevelInfo.GameMode.SPECTATOR) {
				return false;
			}

			if (!this.client.player.canModifyWorld()) {
				Block block = this.client.world.getBlockState(pos).getBlock();
				ItemStack itemStack = this.client.player.getMainHandStack();
				if (itemStack == null) {
					return false;
				}

				if (!itemStack.canDestroy(block)) {
					return false;
				}
			}
		}

		if (this.gameMode.isCreative() && this.client.player.getStackInHand() != null && this.client.player.getStackInHand().getItem() instanceof SwordItem) {
			return false;
		} else {
			World world = this.client.world;
			BlockState blockState = world.getBlockState(pos);
			Block block2 = blockState.getBlock();
			if (block2.getMaterial() == Material.AIR) {
				return false;
			} else {
				world.syncGlobalEvent(2001, pos, Block.getByBlockState(blockState));
				boolean bl = world.setAir(pos);
				if (bl) {
					block2.onBreakByPlayer(world, pos, blockState);
				}

				this.currentBreakingPos = new BlockPos(this.currentBreakingPos.getX(), -1, this.currentBreakingPos.getZ());
				if (!this.gameMode.isCreative()) {
					ItemStack itemStack2 = this.client.player.getMainHandStack();
					if (itemStack2 != null) {
						itemStack2.onBlockBroken(world, block2, pos, this.client.player);
						if (itemStack2.count == 0) {
							this.client.player.removeSelectedSlotItem();
						}
					}
				}

				return bl;
			}
		}
	}

	public boolean attackBlock(BlockPos pos, Direction direction) {
		if (this.gameMode.shouldLimitWorldModification()) {
			if (this.gameMode == LevelInfo.GameMode.SPECTATOR) {
				return false;
			}

			if (!this.client.player.canModifyWorld()) {
				Block block = this.client.world.getBlockState(pos).getBlock();
				ItemStack itemStack = this.client.player.getMainHandStack();
				if (itemStack == null) {
					return false;
				}

				if (!itemStack.canDestroy(block)) {
					return false;
				}
			}
		}

		if (!this.client.world.getWorldBorder().contains(pos)) {
			return false;
		} else {
			if (this.gameMode.isCreative()) {
				this.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, direction));
				breakBlockOrFire(this.client, this, pos, direction);
				this.blockBreakingCooldown = 5;
			} else if (!this.breakingBlock || !this.isCurrentlyBreaking(pos)) {
				if (this.breakingBlock) {
					this.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, this.currentBreakingPos, direction));
				}

				this.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, direction));
				Block block2 = this.client.world.getBlockState(pos).getBlock();
				boolean bl = block2.getMaterial() != Material.AIR;
				if (bl && this.currentBreakingProgress == 0.0F) {
					block2.onBlockBreakStart(this.client.world, pos, this.client.player);
				}

				if (bl && block2.calcBlockBreakingData(this.client.player, this.client.player.world, pos) >= 1.0F) {
					this.breakBlock(pos, direction);
				} else {
					this.breakingBlock = true;
					this.currentBreakingPos = pos;
					this.selectedStack = this.client.player.getStackInHand();
					this.currentBreakingProgress = 0.0F;
					this.blockBreakingSoundCooldown = 0.0F;
					this.client.world.setBlockBreakingInfo(this.client.player.getEntityId(), this.currentBreakingPos, (int)(this.currentBreakingProgress * 10.0F) - 1);
				}
			}

			return true;
		}
	}

	public void cancelBlockBreaking() {
		if (this.breakingBlock) {
			this.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, this.currentBreakingPos, Direction.DOWN));
			this.breakingBlock = false;
			this.currentBreakingProgress = 0.0F;
			this.client.world.setBlockBreakingInfo(this.client.player.getEntityId(), this.currentBreakingPos, -1);
		}
	}

	public boolean updateBlockBreakingProgress(BlockPos pos, Direction direction) {
		this.syncSelectedSlot();
		if (this.blockBreakingCooldown > 0) {
			this.blockBreakingCooldown--;
			return true;
		} else if (this.gameMode.isCreative() && this.client.world.getWorldBorder().contains(pos)) {
			this.blockBreakingCooldown = 5;
			this.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, direction));
			breakBlockOrFire(this.client, this, pos, direction);
			return true;
		} else if (this.isCurrentlyBreaking(pos)) {
			Block block = this.client.world.getBlockState(pos).getBlock();
			if (block.getMaterial() == Material.AIR) {
				this.breakingBlock = false;
				return false;
			} else {
				this.currentBreakingProgress = this.currentBreakingProgress + block.calcBlockBreakingData(this.client.player, this.client.player.world, pos);
				if (this.blockBreakingSoundCooldown % 4.0F == 0.0F) {
					this.client
						.getSoundManager()
						.play(
							new PositionedSoundInstance(
								new Identifier(block.sound.getStepSound()),
								(block.sound.getVolume() + 1.0F) / 8.0F,
								block.sound.getPitch() * 0.5F,
								(float)pos.getX() + 0.5F,
								(float)pos.getY() + 0.5F,
								(float)pos.getZ() + 0.5F
							)
						);
				}

				this.blockBreakingSoundCooldown++;
				if (this.currentBreakingProgress >= 1.0F) {
					this.breakingBlock = false;
					this.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, direction));
					this.breakBlock(pos, direction);
					this.currentBreakingProgress = 0.0F;
					this.blockBreakingSoundCooldown = 0.0F;
					this.blockBreakingCooldown = 5;
				}

				this.client.world.setBlockBreakingInfo(this.client.player.getEntityId(), this.currentBreakingPos, (int)(this.currentBreakingProgress * 10.0F) - 1);
				return true;
			}
		} else {
			return this.attackBlock(pos, direction);
		}
	}

	public float getReachDistance() {
		return this.gameMode.isCreative() ? 5.0F : 4.5F;
	}

	public void tick() {
		this.syncSelectedSlot();
		if (this.networkHandler.getClientConnection().isOpen()) {
			this.networkHandler.getClientConnection().tick();
		} else {
			this.networkHandler.getClientConnection().handleDisconnection();
		}
	}

	private boolean isCurrentlyBreaking(BlockPos pos) {
		ItemStack itemStack = this.client.player.getStackInHand();
		boolean bl = this.selectedStack == null && itemStack == null;
		if (this.selectedStack != null && itemStack != null) {
			bl = itemStack.getItem() == this.selectedStack.getItem()
				&& ItemStack.equalsIgnoreDamage(itemStack, this.selectedStack)
				&& (itemStack.isDamageable() || itemStack.getData() == this.selectedStack.getData());
		}

		return pos.equals(this.currentBreakingPos) && bl;
	}

	private void syncSelectedSlot() {
		int i = this.client.player.inventory.selectedSlot;
		if (i != this.lastSelectedSlot) {
			this.lastSelectedSlot = i;
			this.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(this.lastSelectedSlot));
		}
	}

	public boolean onRightClick(ClientPlayerEntity player, ClientWorld world, ItemStack stack, BlockPos pos, Direction direction, Vec3d vec) {
		this.syncSelectedSlot();
		float f = (float)(vec.x - (double)pos.getX());
		float g = (float)(vec.y - (double)pos.getY());
		float h = (float)(vec.z - (double)pos.getZ());
		boolean bl = false;
		if (!this.client.world.getWorldBorder().contains(pos)) {
			return false;
		} else {
			if (this.gameMode != LevelInfo.GameMode.SPECTATOR) {
				BlockState blockState = world.getBlockState(pos);
				if ((!player.isSneaking() || player.getStackInHand() == null) && blockState.getBlock().onUse(world, pos, blockState, player, direction, f, g, h)) {
					bl = true;
				}

				if (!bl && stack != null && stack.getItem() instanceof BlockItem) {
					BlockItem blockItem = (BlockItem)stack.getItem();
					if (!blockItem.canPlaceItemBlock(world, pos, direction, player, stack)) {
						return false;
					}
				}
			}

			this.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(pos, direction.getId(), player.inventory.getMainHandStack(), f, g, h));
			if (bl || this.gameMode == LevelInfo.GameMode.SPECTATOR) {
				return true;
			} else if (stack == null) {
				return false;
			} else if (this.gameMode.isCreative()) {
				int i = stack.getData();
				int j = stack.count;
				boolean bl2 = stack.use(player, world, pos, direction, f, g, h);
				stack.setDamage(i);
				stack.count = j;
				return bl2;
			} else {
				return stack.use(player, world, pos, direction, f, g, h);
			}
		}
	}

	public boolean interactItem(PlayerEntity player, World world, ItemStack stack) {
		if (this.gameMode == LevelInfo.GameMode.SPECTATOR) {
			return false;
		} else {
			this.syncSelectedSlot();
			this.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(player.inventory.getMainHandStack()));
			int i = stack.count;
			ItemStack itemStack = stack.onStartUse(world, player);
			if (itemStack != stack || itemStack != null && itemStack.count != i) {
				player.inventory.main[player.inventory.selectedSlot] = itemStack;
				if (itemStack.count == 0) {
					player.inventory.main[player.inventory.selectedSlot] = null;
				}

				return true;
			} else {
				return false;
			}
		}
	}

	public ClientPlayerEntity createPlayer(World world, StatHandler statHandler) {
		return new ClientPlayerEntity(this.client, world, this.networkHandler, statHandler);
	}

	public void attackEntity(PlayerEntity player, Entity target) {
		this.syncSelectedSlot();
		this.networkHandler.sendPacket(new PlayerInteractEntityC2SPacket(target, PlayerInteractEntityC2SPacket.Type.ATTACK));
		if (this.gameMode != LevelInfo.GameMode.SPECTATOR) {
			player.attack(target);
		}
	}

	public boolean interactEntity(PlayerEntity player, Entity entity) {
		this.syncSelectedSlot();
		this.networkHandler.sendPacket(new PlayerInteractEntityC2SPacket(entity, PlayerInteractEntityC2SPacket.Type.INTERACT));
		return this.gameMode != LevelInfo.GameMode.SPECTATOR && player.method_3215(entity);
	}

	public boolean interactEntityAtLocation(PlayerEntity player, Entity entity, BlockHitResult hitResult) {
		this.syncSelectedSlot();
		Vec3d vec3d = new Vec3d(hitResult.pos.x - entity.x, hitResult.pos.y - entity.y, hitResult.pos.z - entity.z);
		this.networkHandler.sendPacket(new PlayerInteractEntityC2SPacket(entity, vec3d));
		return this.gameMode != LevelInfo.GameMode.SPECTATOR && entity.interactAt(player, vec3d);
	}

	public ItemStack clickSlot(int syncId, int slotId, int mouseButton, int actionType, PlayerEntity player) {
		short s = player.openScreenHandler.getNextActionId(player.inventory);
		ItemStack itemStack = player.openScreenHandler.onSlotClick(slotId, mouseButton, actionType, player);
		this.networkHandler.sendPacket(new ClickWindowC2SPacket(syncId, slotId, mouseButton, actionType, itemStack, s));
		return itemStack;
	}

	public void clickButton(int syncId, int buttonId) {
		this.networkHandler.sendPacket(new ButtonClickC2SPacket(syncId, buttonId));
	}

	public void clickCreativeStack(ItemStack stack, int slotId) {
		if (this.gameMode.isCreative()) {
			this.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(slotId, stack));
		}
	}

	public void dropCreativeStack(ItemStack stack) {
		if (this.gameMode.isCreative() && stack != null) {
			this.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(-1, stack));
		}
	}

	public void stopUsingItem(PlayerEntity player) {
		this.syncSelectedSlot();
		this.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, Direction.DOWN));
		player.stopUsingItem();
	}

	public boolean hasExperienceBar() {
		return this.gameMode.isSurvivalLike();
	}

	public boolean hasLimitedAttackSpeed() {
		return !this.gameMode.isCreative();
	}

	public boolean hasCreativeInventory() {
		return this.gameMode.isCreative();
	}

	public boolean hasExtendedReach() {
		return this.gameMode.isCreative();
	}

	public boolean hasRidingInventory() {
		return this.client.player.hasVehicle() && this.client.player.vehicle instanceof HorseBaseEntity;
	}

	public boolean isFlyingLocked() {
		return this.gameMode == LevelInfo.GameMode.SPECTATOR;
	}

	public LevelInfo.GameMode getCurrentGameMode() {
		return this.gameMode;
	}

	public boolean isBreakingBlock() {
		return this.breakingBlock;
	}
}
