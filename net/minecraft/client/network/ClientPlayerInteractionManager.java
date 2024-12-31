package net.minecraft.client.network;

import io.netty.buffer.Unpooled;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CommandBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundCategory;
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
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.SwingHandC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.stat.StatHandler;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemAction;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.TypedActionResult;
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
			interactionManager.method_12233(pos);
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

	public boolean method_12233(BlockPos blockPos) {
		if (this.gameMode.shouldLimitWorldModification()) {
			if (this.gameMode == LevelInfo.GameMode.SPECTATOR) {
				return false;
			}

			if (!this.client.player.canModifyWorld()) {
				ItemStack itemStack = this.client.player.getMainHandStack();
				if (itemStack == null) {
					return false;
				}

				if (!itemStack.canDestroy(this.client.world.getBlockState(blockPos).getBlock())) {
					return false;
				}
			}
		}

		if (this.gameMode.isCreative() && this.client.player.getMainHandStack() != null && this.client.player.getMainHandStack().getItem() instanceof SwordItem) {
			return false;
		} else {
			World world = this.client.world;
			BlockState blockState = world.getBlockState(blockPos);
			Block block = blockState.getBlock();
			if (block instanceof CommandBlock && !this.client.player.canUseCommand(2, "")) {
				return false;
			} else if (blockState.getMaterial() == Material.AIR) {
				return false;
			} else {
				world.syncGlobalEvent(2001, blockPos, Block.getByBlockState(blockState));
				block.onBreakByPlayer(world, blockPos, blockState, this.client.player);
				boolean bl = world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 11);
				if (bl) {
					block.onBreakByPlayer(world, blockPos, blockState);
				}

				this.currentBreakingPos = new BlockPos(this.currentBreakingPos.getX(), -1, this.currentBreakingPos.getZ());
				if (!this.gameMode.isCreative()) {
					ItemStack itemStack2 = this.client.player.getMainHandStack();
					if (itemStack2 != null) {
						itemStack2.method_11306(world, blockState, blockPos, this.client.player);
						if (itemStack2.count == 0) {
							this.client.player.equipStack(Hand.MAIN_HAND, null);
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
				ItemStack itemStack = this.client.player.getMainHandStack();
				if (itemStack == null) {
					return false;
				}

				if (!itemStack.canDestroy(this.client.world.getBlockState(pos).getBlock())) {
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
				BlockState blockState = this.client.world.getBlockState(pos);
				boolean bl = blockState.getMaterial() != Material.AIR;
				if (bl && this.currentBreakingProgress == 0.0F) {
					blockState.getBlock().onBlockBreakStart(this.client.world, pos, this.client.player);
				}

				if (bl && blockState.method_11716(this.client.player, this.client.player.world, pos) >= 1.0F) {
					this.method_12233(pos);
				} else {
					this.breakingBlock = true;
					this.currentBreakingPos = pos;
					this.selectedStack = this.client.player.getMainHandStack();
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
			this.client.player.method_13269();
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
			BlockState blockState = this.client.world.getBlockState(pos);
			Block block = blockState.getBlock();
			if (blockState.getMaterial() == Material.AIR) {
				this.breakingBlock = false;
				return false;
			} else {
				this.currentBreakingProgress = this.currentBreakingProgress + blockState.method_11716(this.client.player, this.client.player.world, pos);
				if (this.blockBreakingSoundCooldown % 4.0F == 0.0F) {
					BlockSoundGroup blockSoundGroup = block.getSoundGroup();
					this.client
						.getSoundManager()
						.play(
							new PositionedSoundInstance(
								blockSoundGroup.method_11630(), SoundCategory.NEUTRAL, (blockSoundGroup.getVolume() + 1.0F) / 8.0F, blockSoundGroup.getPitch() * 0.5F, pos
							)
						);
				}

				this.blockBreakingSoundCooldown++;
				if (this.currentBreakingProgress >= 1.0F) {
					this.breakingBlock = false;
					this.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, direction));
					this.method_12233(pos);
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
		ItemStack itemStack = this.client.player.getMainHandStack();
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

	public ActionResult method_12232(
		ClientPlayerEntity clientPlayerEntity, ClientWorld clientWorld, @Nullable ItemStack itemStack, BlockPos blockPos, Direction direction, Vec3d vec3d, Hand hand
	) {
		this.syncSelectedSlot();
		float f = (float)(vec3d.x - (double)blockPos.getX());
		float g = (float)(vec3d.y - (double)blockPos.getY());
		float h = (float)(vec3d.z - (double)blockPos.getZ());
		boolean bl = false;
		if (!this.client.world.getWorldBorder().contains(blockPos)) {
			return ActionResult.FAIL;
		} else {
			if (this.gameMode != LevelInfo.GameMode.SPECTATOR) {
				BlockState blockState = clientWorld.getBlockState(blockPos);
				if ((!clientPlayerEntity.isSneaking() || clientPlayerEntity.getMainHandStack() == null && clientPlayerEntity.getOffHandStack() == null)
					&& blockState.getBlock().method_421(clientWorld, blockPos, blockState, clientPlayerEntity, hand, itemStack, direction, f, g, h)) {
					bl = true;
				}

				if (!bl && itemStack != null && itemStack.getItem() instanceof BlockItem) {
					BlockItem blockItem = (BlockItem)itemStack.getItem();
					if (!blockItem.canPlaceItemBlock(clientWorld, blockPos, direction, clientPlayerEntity, itemStack)) {
						return ActionResult.FAIL;
					}
				}
			}

			this.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(blockPos, direction, hand, f, g, h));
			if (bl || this.gameMode == LevelInfo.GameMode.SPECTATOR) {
				return ActionResult.SUCCESS;
			} else if (itemStack == null) {
				return ActionResult.PASS;
			} else if (clientPlayerEntity.getItemCooldownManager().method_11382(itemStack.getItem())) {
				return ActionResult.PASS;
			} else if (itemStack.getItem() instanceof BlockItem
				&& ((BlockItem)itemStack.getItem()).getBlock() instanceof CommandBlock
				&& !clientPlayerEntity.canUseCommand(2, "")) {
				return ActionResult.FAIL;
			} else if (this.gameMode.isCreative()) {
				int i = itemStack.getData();
				int j = itemStack.count;
				ActionResult actionResult = itemStack.use(clientPlayerEntity, clientWorld, blockPos, hand, direction, f, g, h);
				itemStack.setDamage(i);
				itemStack.count = j;
				return actionResult;
			} else {
				return itemStack.use(clientPlayerEntity, clientWorld, blockPos, hand, direction, f, g, h);
			}
		}
	}

	public ActionResult method_12234(PlayerEntity playerEntity, World world, ItemStack itemStack, Hand hand) {
		if (this.gameMode == LevelInfo.GameMode.SPECTATOR) {
			return ActionResult.PASS;
		} else {
			this.syncSelectedSlot();
			this.networkHandler.sendPacket(new SwingHandC2SPacket(hand));
			if (playerEntity.getItemCooldownManager().method_11382(itemStack.getItem())) {
				return ActionResult.PASS;
			} else {
				int i = itemStack.count;
				TypedActionResult<ItemStack> typedActionResult = itemStack.method_11390(world, playerEntity, hand);
				ItemStack itemStack2 = typedActionResult.getObject();
				if (itemStack2 != itemStack || itemStack2.count != i) {
					playerEntity.equipStack(hand, itemStack2);
					if (itemStack2.count == 0) {
						playerEntity.equipStack(hand, null);
					}
				}

				return typedActionResult.getActionResult();
			}
		}
	}

	public ClientPlayerEntity createPlayer(World world, StatHandler statHandler) {
		return new ClientPlayerEntity(this.client, world, this.networkHandler, statHandler);
	}

	public void attackEntity(PlayerEntity player, Entity target) {
		this.syncSelectedSlot();
		this.networkHandler.sendPacket(new PlayerInteractEntityC2SPacket(target));
		if (this.gameMode != LevelInfo.GameMode.SPECTATOR) {
			player.attack(target);
			player.method_13269();
		}
	}

	public ActionResult method_12235(PlayerEntity playerEntity, Entity entity, @Nullable ItemStack itemStack, Hand hand) {
		this.syncSelectedSlot();
		this.networkHandler.sendPacket(new PlayerInteractEntityC2SPacket(entity, hand));
		return this.gameMode == LevelInfo.GameMode.SPECTATOR ? ActionResult.PASS : playerEntity.method_13263(entity, itemStack, hand);
	}

	public ActionResult method_12236(PlayerEntity playerEntity, Entity entity, BlockHitResult blockHitResult, @Nullable ItemStack itemStack, Hand hand) {
		this.syncSelectedSlot();
		Vec3d vec3d = new Vec3d(blockHitResult.pos.x - entity.x, blockHitResult.pos.y - entity.y, blockHitResult.pos.z - entity.z);
		this.networkHandler.sendPacket(new PlayerInteractEntityC2SPacket(entity, hand, vec3d));
		return this.gameMode == LevelInfo.GameMode.SPECTATOR ? ActionResult.PASS : entity.method_12976(playerEntity, vec3d, itemStack, hand);
	}

	public ItemStack method_1224(int i, int j, int k, ItemAction itemAction, PlayerEntity playerEntity) {
		short s = playerEntity.openScreenHandler.getNextActionId(playerEntity.inventory);
		ItemStack itemStack = playerEntity.openScreenHandler.method_3252(j, k, itemAction, playerEntity);
		this.networkHandler.sendPacket(new ClickWindowC2SPacket(i, j, k, itemAction, itemStack, s));
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
		player.method_13067();
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
		return this.client.player.hasMount() && this.client.player.getVehicle() instanceof HorseBaseEntity;
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

	public void method_12231(int i) {
		this.networkHandler.sendPacket(new CustomPayloadC2SPacket("MC|PickItem", new PacketByteBuf(Unpooled.buffer()).writeVarInt(i)));
	}
}
