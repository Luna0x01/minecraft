package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityPosWrapper;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.TradeOffer;

public class HoldTradeOffersTask extends Task<VillagerEntity> {
	@Nullable
	private ItemStack customerHeldStack;
	private final List<ItemStack> offers = Lists.newArrayList();
	private int offerShownTicks;
	private int offerIndex;
	private int ticksLeft;

	public HoldTradeOffersTask(int i, int j) {
		super(ImmutableMap.of(MemoryModuleType.field_18447, MemoryModuleState.field_18456), i, j);
	}

	public boolean shouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		Brain<?> brain = villagerEntity.getBrain();
		if (!brain.getOptionalMemory(MemoryModuleType.field_18447).isPresent()) {
			return false;
		} else {
			LivingEntity livingEntity = (LivingEntity)brain.getOptionalMemory(MemoryModuleType.field_18447).get();
			return livingEntity.getType() == EntityType.field_6097
				&& villagerEntity.isAlive()
				&& livingEntity.isAlive()
				&& !villagerEntity.isBaby()
				&& villagerEntity.squaredDistanceTo(livingEntity) <= 17.0;
		}
	}

	public boolean shouldKeepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		return this.shouldRun(serverWorld, villagerEntity)
			&& this.ticksLeft > 0
			&& villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.field_18447).isPresent();
	}

	public void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		super.run(serverWorld, villagerEntity, l);
		this.findPotentialCuatomer(villagerEntity);
		this.offerShownTicks = 0;
		this.offerIndex = 0;
		this.ticksLeft = 40;
	}

	public void keepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		LivingEntity livingEntity = this.findPotentialCuatomer(villagerEntity);
		this.setupOffers(livingEntity, villagerEntity);
		if (!this.offers.isEmpty()) {
			this.refreshShownOffer(villagerEntity);
		} else {
			villagerEntity.equipStack(EquipmentSlot.field_6173, ItemStack.EMPTY);
			this.ticksLeft = Math.min(this.ticksLeft, 40);
		}

		this.ticksLeft--;
	}

	public void finishRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		super.finishRunning(serverWorld, villagerEntity, l);
		villagerEntity.getBrain().forget(MemoryModuleType.field_18447);
		villagerEntity.equipStack(EquipmentSlot.field_6173, ItemStack.EMPTY);
		this.customerHeldStack = null;
	}

	private void setupOffers(LivingEntity livingEntity, VillagerEntity villagerEntity) {
		boolean bl = false;
		ItemStack itemStack = livingEntity.getMainHandStack();
		if (this.customerHeldStack == null || !ItemStack.areItemsEqualIgnoreDamage(this.customerHeldStack, itemStack)) {
			this.customerHeldStack = itemStack;
			bl = true;
			this.offers.clear();
		}

		if (bl && !this.customerHeldStack.isEmpty()) {
			this.loadPossibleOffers(villagerEntity);
			if (!this.offers.isEmpty()) {
				this.ticksLeft = 900;
				this.holdOffer(villagerEntity);
			}
		}
	}

	private void holdOffer(VillagerEntity villagerEntity) {
		villagerEntity.equipStack(EquipmentSlot.field_6173, (ItemStack)this.offers.get(0));
	}

	private void loadPossibleOffers(VillagerEntity villagerEntity) {
		for (TradeOffer tradeOffer : villagerEntity.getOffers()) {
			if (!tradeOffer.isDisabled() && this.isPossible(tradeOffer)) {
				this.offers.add(tradeOffer.getMutableSellItem());
			}
		}
	}

	private boolean isPossible(TradeOffer tradeOffer) {
		return ItemStack.areItemsEqualIgnoreDamage(this.customerHeldStack, tradeOffer.getAdjustedFirstBuyItem())
			|| ItemStack.areItemsEqualIgnoreDamage(this.customerHeldStack, tradeOffer.getSecondBuyItem());
	}

	private LivingEntity findPotentialCuatomer(VillagerEntity villagerEntity) {
		Brain<?> brain = villagerEntity.getBrain();
		LivingEntity livingEntity = (LivingEntity)brain.getOptionalMemory(MemoryModuleType.field_18447).get();
		brain.putMemory(MemoryModuleType.field_18446, new EntityPosWrapper(livingEntity));
		return livingEntity;
	}

	private void refreshShownOffer(VillagerEntity villagerEntity) {
		if (this.offers.size() >= 2 && ++this.offerShownTicks >= 40) {
			this.offerIndex++;
			this.offerShownTicks = 0;
			if (this.offerIndex > this.offers.size() - 1) {
				this.offerIndex = 0;
			}

			villagerEntity.equipStack(EquipmentSlot.field_6173, (ItemStack)this.offers.get(this.offerIndex));
		}
	}
}
