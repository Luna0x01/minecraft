package net.minecraft.client.network;

import net.minecraft.class_3320;
import net.minecraft.class_4387;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CommandBlock;
import net.minecraft.block.StructureBlock;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.AbstractHorseEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.packet.c2s.play.ButtonClickC2SPacket;
import net.minecraft.network.packet.c2s.play.ClickWindowC2SPacket;
import net.minecraft.network.packet.c2s.play.CraftRecipeRequestC2SPacket;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.SwingHandC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.recipe.RecipeType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.stat.StatHandler;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemAction;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

public class ClientPlayerInteractionManager {
	private final MinecraftClient client;
	private final ClientPlayNetworkHandler networkHandler;
	private BlockPos currentBreakingPos = new BlockPos(-1, -1, -1);
	private ItemStack selectedStack = ItemStack.EMPTY;
	private float currentBreakingProgress;
	private float blockBreakingSoundCooldown;
	private int blockBreakingCooldown;
	private boolean breakingBlock;
	private GameMode gameMode = GameMode.SURVIVAL;
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
		this.gameMode.gameModeAbilities(player.abilities);
	}

	public void setGameMode(GameMode gameMode) {
		this.gameMode = gameMode;
		this.gameMode.gameModeAbilities(this.client.player.abilities);
	}

	public void flipPlayer(PlayerEntity player) {
		player.yaw = -180.0F;
	}

	public boolean hasStatusBars() {
		return this.gameMode.canBeDamaged();
	}

	public boolean method_12233(BlockPos blockPos) {
		if (this.gameMode.isAdventure()) {
			if (this.gameMode == GameMode.SPECTATOR) {
				return false;
			}

			if (!this.client.player.canModifyWorld()) {
				ItemStack itemStack = this.client.player.getMainHandStack();
				if (itemStack.isEmpty()) {
					return false;
				}

				CachedBlockPosition cachedBlockPosition = new CachedBlockPosition(this.client.world, blockPos, false);
				if (!itemStack.method_16103(this.client.world.method_16314(), cachedBlockPosition)) {
					return false;
				}
			}
		}

		World world = this.client.world;
		BlockState blockState = world.getBlockState(blockPos);
		if (!this.client.player.getMainHandStack().getItem().beforeBlockBreak(blockState, world, blockPos, this.client.player)) {
			return false;
		} else {
			Block block = blockState.getBlock();
			if ((block instanceof CommandBlock || block instanceof StructureBlock) && !this.client.player.method_15936()) {
				return false;
			} else if (blockState.isAir()) {
				return false;
			} else {
				block.onBreakByPlayer(world, blockPos, blockState, this.client.player);
				FluidState fluidState = world.getFluidState(blockPos);
				boolean bl = world.setBlockState(blockPos, fluidState.method_17813(), 11);
				if (bl) {
					block.method_8674(world, blockPos, blockState);
				}

				this.currentBreakingPos = new BlockPos(this.currentBreakingPos.getX(), -1, this.currentBreakingPos.getZ());
				if (!this.gameMode.isCreative()) {
					ItemStack itemStack2 = this.client.player.getMainHandStack();
					if (!itemStack2.isEmpty()) {
						itemStack2.method_11306(world, blockState, blockPos, this.client.player);
						if (itemStack2.isEmpty()) {
							this.client.player.equipStack(Hand.MAIN_HAND, ItemStack.EMPTY);
						}
					}
				}

				return bl;
			}
		}
	}

	public boolean attackBlock(BlockPos pos, Direction direction) {
		if (this.gameMode.isAdventure()) {
			if (this.gameMode == GameMode.SPECTATOR) {
				return false;
			}

			if (!this.client.player.canModifyWorld()) {
				ItemStack itemStack = this.client.player.getMainHandStack();
				if (itemStack.isEmpty()) {
					return false;
				}

				CachedBlockPosition cachedBlockPosition = new CachedBlockPosition(this.client.world, pos, false);
				if (!itemStack.method_16103(this.client.world.method_16314(), cachedBlockPosition)) {
					return false;
				}
			}
		}

		if (!this.client.world.method_8524().contains(pos)) {
			return false;
		} else {
			if (this.gameMode.isCreative()) {
				this.client.method_14463().method_14722(this.client.world, pos, this.client.world.getBlockState(pos), 1.0F);
				this.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, direction));
				breakBlockOrFire(this.client, this, pos, direction);
				this.blockBreakingCooldown = 5;
			} else if (!this.breakingBlock || !this.isCurrentlyBreaking(pos)) {
				if (this.breakingBlock) {
					this.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, this.currentBreakingPos, direction));
				}

				BlockState blockState = this.client.world.getBlockState(pos);
				this.client.method_14463().method_14722(this.client.world, pos, blockState, 0.0F);
				this.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, direction));
				boolean bl = !blockState.isAir();
				if (bl && this.currentBreakingProgress == 0.0F) {
					blockState.method_16870(this.client.world, pos, this.client.player);
				}

				if (bl && blockState.method_16860(this.client.player, this.client.player.world, pos) >= 1.0F) {
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
			this.client.method_14463().method_14722(this.client.world, this.currentBreakingPos, this.client.world.getBlockState(this.currentBreakingPos), -1.0F);
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
		} else if (this.gameMode.isCreative() && this.client.world.method_8524().contains(pos)) {
			this.blockBreakingCooldown = 5;
			this.client.method_14463().method_14722(this.client.world, pos, this.client.world.getBlockState(pos), 1.0F);
			this.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, direction));
			breakBlockOrFire(this.client, this, pos, direction);
			return true;
		} else if (this.isCurrentlyBreaking(pos)) {
			BlockState blockState = this.client.world.getBlockState(pos);
			Block block = blockState.getBlock();
			if (blockState.isAir()) {
				this.breakingBlock = false;
				return false;
			} else {
				this.currentBreakingProgress = this.currentBreakingProgress + blockState.method_16860(this.client.player, this.client.player.world, pos);
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
				this.client.method_14463().method_14722(this.client.world, pos, blockState, MathHelper.clamp(this.currentBreakingProgress, 0.0F, 1.0F));
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
		boolean bl = this.selectedStack.isEmpty() && itemStack.isEmpty();
		if (!this.selectedStack.isEmpty() && !itemStack.isEmpty()) {
			bl = itemStack.getItem() == this.selectedStack.getItem()
				&& ItemStack.equalsIgnoreDamage(itemStack, this.selectedStack)
				&& (itemStack.isDamageable() || itemStack.getDamage() == this.selectedStack.getDamage());
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

	public ActionResult method_13842(
		ClientPlayerEntity clientPlayerEntity, ClientWorld clientWorld, BlockPos blockPos, Direction direction, Vec3d vec3d, Hand hand
	) {
		this.syncSelectedSlot();
		if (!this.client.world.method_8524().contains(blockPos)) {
			return ActionResult.FAIL;
		} else {
			ItemStack itemStack = clientPlayerEntity.getStackInHand(hand);
			float f = (float)(vec3d.x - (double)blockPos.getX());
			float g = (float)(vec3d.y - (double)blockPos.getY());
			float h = (float)(vec3d.z - (double)blockPos.getZ());
			if (this.gameMode == GameMode.SPECTATOR) {
				this.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(blockPos, direction, hand, f, g, h));
				return ActionResult.SUCCESS;
			} else {
				boolean bl = !clientPlayerEntity.getMainHandStack().isEmpty() || !clientPlayerEntity.getOffHandStack().isEmpty();
				boolean bl2 = clientPlayerEntity.isSneaking() && bl;
				if (!bl2 && clientWorld.getBlockState(blockPos).onUse(clientWorld, blockPos, clientPlayerEntity, hand, direction, f, g, h)) {
					this.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(blockPos, direction, hand, f, g, h));
					return ActionResult.SUCCESS;
				} else {
					this.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(blockPos, direction, hand, f, g, h));
					if (!itemStack.isEmpty() && !clientPlayerEntity.getItemCooldownManager().method_11382(itemStack.getItem())) {
						ItemUsageContext itemUsageContext = new ItemUsageContext(clientPlayerEntity, clientPlayerEntity.getStackInHand(hand), blockPos, direction, f, g, h);
						ActionResult actionResult;
						if (this.gameMode.isCreative()) {
							int i = itemStack.getCount();
							actionResult = itemStack.method_16097(itemUsageContext);
							itemStack.setCount(i);
						} else {
							actionResult = itemStack.method_16097(itemUsageContext);
						}

						return actionResult;
					} else {
						return ActionResult.PASS;
					}
				}
			}
		}
	}

	public ActionResult method_12234(PlayerEntity player, World world, Hand hand) {
		if (this.gameMode == GameMode.SPECTATOR) {
			return ActionResult.PASS;
		} else {
			this.syncSelectedSlot();
			this.networkHandler.sendPacket(new SwingHandC2SPacket(hand));
			ItemStack itemStack = player.getStackInHand(hand);
			if (player.getItemCooldownManager().method_11382(itemStack.getItem())) {
				return ActionResult.PASS;
			} else {
				int i = itemStack.getCount();
				TypedActionResult<ItemStack> typedActionResult = itemStack.method_11390(world, player, hand);
				ItemStack itemStack2 = typedActionResult.getObject();
				if (itemStack2 != itemStack || itemStack2.getCount() != i) {
					player.equipStack(hand, itemStack2);
				}

				return typedActionResult.getActionResult();
			}
		}
	}

	public ClientPlayerEntity method_9658(World world, StatHandler statHandler, class_3320 arg) {
		return new ClientPlayerEntity(this.client, world, this.networkHandler, statHandler, arg);
	}

	public void attackEntity(PlayerEntity player, Entity target) {
		this.syncSelectedSlot();
		this.networkHandler.sendPacket(new PlayerInteractEntityC2SPacket(target));
		if (this.gameMode != GameMode.SPECTATOR) {
			player.attack(target);
			player.method_13269();
		}
	}

	public ActionResult method_12235(PlayerEntity player, Entity entity, Hand hand) {
		this.syncSelectedSlot();
		this.networkHandler.sendPacket(new PlayerInteractEntityC2SPacket(entity, hand));
		return this.gameMode == GameMode.SPECTATOR ? ActionResult.PASS : player.method_13616(entity, hand);
	}

	public ActionResult method_12236(PlayerEntity player, Entity entity, BlockHitResult blockHitResult, Hand hand) {
		this.syncSelectedSlot();
		Vec3d vec3d = new Vec3d(blockHitResult.pos.x - entity.x, blockHitResult.pos.y - entity.y, blockHitResult.pos.z - entity.z);
		this.networkHandler.sendPacket(new PlayerInteractEntityC2SPacket(entity, hand, vec3d));
		return this.gameMode == GameMode.SPECTATOR ? ActionResult.PASS : entity.interactAt(player, vec3d, hand);
	}

	public ItemStack method_1224(int i, int j, int k, ItemAction itemAction, PlayerEntity playerEntity) {
		short s = playerEntity.openScreenHandler.getNextActionId(playerEntity.inventory);
		ItemStack itemStack = playerEntity.openScreenHandler.method_3252(j, k, itemAction, playerEntity);
		this.networkHandler.sendPacket(new ClickWindowC2SPacket(i, j, k, itemAction, itemStack, s));
		return itemStack;
	}

	public void method_14674(int i, RecipeType recipeType, boolean bl) {
		this.networkHandler.sendPacket(new CraftRecipeRequestC2SPacket(i, recipeType, bl));
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
		if (this.gameMode.isCreative() && !stack.isEmpty()) {
			this.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(-1, stack));
		}
	}

	public void stopUsingItem(PlayerEntity player) {
		this.syncSelectedSlot();
		this.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, Direction.DOWN));
		player.method_13067();
	}

	public boolean hasExperienceBar() {
		return this.gameMode.canBeDamaged();
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
		return this.client.player.hasMount() && this.client.player.getVehicle() instanceof AbstractHorseEntity;
	}

	public boolean isFlyingLocked() {
		return this.gameMode == GameMode.SPECTATOR;
	}

	public GameMode method_9667() {
		return this.gameMode;
	}

	public boolean isBreakingBlock() {
		return this.breakingBlock;
	}

	public void method_12231(int i) {
		this.networkHandler.sendPacket(new class_4387(i));
	}
}
