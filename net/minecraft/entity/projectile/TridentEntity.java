package net.minecraft.entity.projectile;

import javax.annotation.Nullable;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningBoltEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class TridentEntity extends AbstractArrowEntity {
	private static final TrackedData<Byte> field_17106 = DataTracker.registerData(TridentEntity.class, TrackedDataHandlerRegistry.BYTE);
	private ItemStack field_17103 = new ItemStack(Items.TRIDENT);
	private boolean field_17104;
	public int field_17105;

	public TridentEntity(World world) {
		super(EntityType.TRIDENT, world);
	}

	public TridentEntity(World world, LivingEntity livingEntity, ItemStack itemStack) {
		super(EntityType.TRIDENT, livingEntity, world);
		this.field_17103 = itemStack.copy();
		this.dataTracker.set(field_17106, (byte)EnchantmentHelper.method_16266(itemStack));
	}

	public TridentEntity(World world, double d, double e, double f) {
		super(EntityType.TRIDENT, d, e, f, world);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_17106, (byte)0);
	}

	@Override
	public void tick() {
		if (this.inGroundTime > 4) {
			this.field_17104 = true;
		}

		Entity entity = this.method_15950();
		if ((this.field_17104 || this.method_15953()) && entity != null) {
			int i = this.dataTracker.get(field_17106);
			if (i > 0 && !this.method_15958()) {
				if (!this.world.isClient && this.pickupType == AbstractArrowEntity.PickupPermission.ALLOWED) {
					this.dropItem(this.asItemStack(), 0.1F);
				}

				this.remove();
			} else if (i > 0) {
				this.method_15951(true);
				Vec3d vec3d = new Vec3d(entity.x - this.x, entity.y + (double)entity.getEyeHeight() - this.y, entity.z - this.z);
				this.y = this.y + vec3d.y * 0.015 * (double)i;
				if (this.world.isClient) {
					this.prevTickY = this.y;
				}

				vec3d = vec3d.normalize();
				double d = 0.05 * (double)i;
				this.velocityX = this.velocityX + (vec3d.x * d - this.velocityX * 0.05);
				this.velocityY = this.velocityY + (vec3d.y * d - this.velocityY * 0.05);
				this.velocityZ = this.velocityZ + (vec3d.z * d - this.velocityZ * 0.05);
				if (this.field_17105 == 0) {
					this.playSound(Sounds.ITEM_TRIDENT_RETURN, 10.0F, 1.0F);
				}

				this.field_17105++;
			}
		}

		super.tick();
	}

	private boolean method_15958() {
		Entity entity = this.method_15950();
		return entity == null || !entity.isAlive() ? false : !(entity instanceof ServerPlayerEntity) || !((ServerPlayerEntity)entity).isSpectator();
	}

	@Override
	protected ItemStack asItemStack() {
		return this.field_17103.copy();
	}

	@Nullable
	@Override
	protected Entity getEntityCollision(Vec3d currentPosition, Vec3d nextPosition) {
		return this.field_17104 ? null : super.getEntityCollision(currentPosition, nextPosition);
	}

	@Override
	protected void method_15947(BlockHitResult blockHitResult) {
		Entity entity = blockHitResult.entity;
		float f = 8.0F;
		if (entity instanceof LivingEntity) {
			LivingEntity livingEntity = (LivingEntity)entity;
			f += EnchantmentHelper.method_16260(this.field_17103, livingEntity.method_2647());
		}

		Entity entity2 = this.method_15950();
		DamageSource damageSource = DamageSource.method_15546(this, (Entity)(entity2 == null ? this : entity2));
		this.field_17104 = true;
		Sound sound = Sounds.ITEM_TRIDENT_HIT;
		if (entity.damage(damageSource, f) && entity instanceof LivingEntity) {
			LivingEntity livingEntity2 = (LivingEntity)entity;
			if (entity2 instanceof LivingEntity) {
				EnchantmentHelper.onUserDamaged(livingEntity2, entity2);
				EnchantmentHelper.onTargetDamaged((LivingEntity)entity2, livingEntity2);
			}

			this.onHit(livingEntity2);
		}

		this.velocityX *= -0.01F;
		this.velocityY *= -0.1F;
		this.velocityZ *= -0.01F;
		float g = 1.0F;
		if (this.world.isThundering() && EnchantmentHelper.method_16268(this.field_17103)) {
			BlockPos blockPos = entity.method_4086();
			if (this.world.method_8555(blockPos)) {
				LightningBoltEntity lightningBoltEntity = new LightningBoltEntity(
					this.world, (double)blockPos.getX() + 0.5, (double)blockPos.getY(), (double)blockPos.getZ() + 0.5, false
				);
				lightningBoltEntity.method_15846(entity2 instanceof ServerPlayerEntity ? (ServerPlayerEntity)entity2 : null);
				this.world.addEntity(lightningBoltEntity);
				sound = Sounds.ITEM_TRIDENT_THUNDER;
				g = 5.0F;
			}
		}

		this.playSound(sound, g, 1.0F);
	}

	@Override
	protected Sound method_15949() {
		return Sounds.ITEM_TRIDENT_HIT_GROUND;
	}

	@Override
	public void onPlayerCollision(PlayerEntity player) {
		Entity entity = this.method_15950();
		if (entity == null || entity.getUuid() == player.getUuid()) {
			super.onPlayerCollision(player);
		}
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if (nbt.contains("Trident", 10)) {
			this.field_17103 = ItemStack.from(nbt.getCompound("Trident"));
		}

		this.field_17104 = nbt.getBoolean("DealtDamage");
		this.dataTracker.set(field_17106, (byte)EnchantmentHelper.method_16266(this.field_17103));
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.put("Trident", this.field_17103.toNbt(new NbtCompound()));
		nbt.putBoolean("DealtDamage", this.field_17104);
	}

	@Override
	protected void method_15948() {
		int i = this.dataTracker.get(field_17106);
		if (this.pickupType != AbstractArrowEntity.PickupPermission.ALLOWED || i <= 0) {
			super.method_15948();
		}
	}

	@Override
	protected float method_15952() {
		return 0.99F;
	}

	@Override
	public boolean shouldRender(double x, double y, double z) {
		return true;
	}
}
