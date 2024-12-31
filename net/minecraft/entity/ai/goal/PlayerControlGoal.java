package net.minecraft.entity.ai.goal;

import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.PathAwareEntity;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class PlayerControlGoal extends Goal {
	private final MobEntity mob;
	private final float speed;
	private float field_5351;
	private boolean field_5352;
	private int field_5353;
	private int field_5354;

	public PlayerControlGoal(MobEntity mobEntity, float f) {
		this.mob = mobEntity;
		this.speed = f;
		this.setCategoryBits(7);
	}

	@Override
	public void start() {
		this.field_5351 = 0.0F;
	}

	@Override
	public void stop() {
		this.field_5352 = false;
		this.field_5351 = 0.0F;
	}

	@Override
	public boolean canStart() {
		return this.mob.isAlive() && this.mob.rider != null && this.mob.rider instanceof PlayerEntity && (this.field_5352 || this.mob.canBeControlledByRider());
	}

	@Override
	public void tick() {
		PlayerEntity playerEntity = (PlayerEntity)this.mob.rider;
		PathAwareEntity pathAwareEntity = (PathAwareEntity)this.mob;
		float f = MathHelper.wrapDegrees(playerEntity.yaw - this.mob.yaw) * 0.5F;
		if (f > 5.0F) {
			f = 5.0F;
		}

		if (f < -5.0F) {
			f = -5.0F;
		}

		this.mob.yaw = MathHelper.wrapDegrees(this.mob.yaw + f);
		if (this.field_5351 < this.speed) {
			this.field_5351 = this.field_5351 + (this.speed - this.field_5351) * 0.01F;
		}

		if (this.field_5351 > this.speed) {
			this.field_5351 = this.speed;
		}

		int i = MathHelper.floor(this.mob.x);
		int j = MathHelper.floor(this.mob.y);
		int k = MathHelper.floor(this.mob.z);
		float g = this.field_5351;
		if (this.field_5352) {
			if (this.field_5353++ > this.field_5354) {
				this.field_5352 = false;
			}

			g += g * 1.15F * MathHelper.sin((float)this.field_5353 / (float)this.field_5354 * (float) Math.PI);
		}

		float h = 0.91F;
		if (this.mob.onGround) {
			h = this.mob.world.getBlockState(new BlockPos(MathHelper.floor((float)i), MathHelper.floor((float)j) - 1, MathHelper.floor((float)k))).getBlock().slipperiness
				* 0.91F;
		}

		float l = 0.16277136F / (h * h * h);
		float m = MathHelper.sin(pathAwareEntity.yaw * (float) Math.PI / 180.0F);
		float n = MathHelper.cos(pathAwareEntity.yaw * (float) Math.PI / 180.0F);
		float o = pathAwareEntity.getMovementSpeed() * l;
		float p = Math.max(g, 1.0F);
		p = o / p;
		float q = g * p;
		float r = -(q * m);
		float s = q * n;
		if (MathHelper.abs(r) > MathHelper.abs(s)) {
			if (r < 0.0F) {
				r -= this.mob.width / 2.0F;
			}

			if (r > 0.0F) {
				r += this.mob.width / 2.0F;
			}

			s = 0.0F;
		} else {
			r = 0.0F;
			if (s < 0.0F) {
				s -= this.mob.width / 2.0F;
			}

			if (s > 0.0F) {
				s += this.mob.width / 2.0F;
			}
		}

		int t = MathHelper.floor(this.mob.x + (double)r);
		int u = MathHelper.floor(this.mob.z + (double)s);
		int v = MathHelper.floor(this.mob.width + 1.0F);
		int w = MathHelper.floor(this.mob.height + playerEntity.height + 1.0F);
		int x = MathHelper.floor(this.mob.width + 1.0F);
		if (i != t || k != u) {
			Block block = this.mob.world.getBlockState(new BlockPos(i, j, k)).getBlock();
			boolean bl = !this.method_8381(block)
				&& (block.getMaterial() != Material.AIR || !this.method_8381(this.mob.world.getBlockState(new BlockPos(i, j - 1, k)).getBlock()));
			if (bl
				&& 0 == LandPathNodeMaker.getNodeType(this.mob.world, this.mob, t, j, u, v, w, x, false, false, true)
				&& 1 == LandPathNodeMaker.getNodeType(this.mob.world, this.mob, i, j + 1, k, v, w, x, false, false, true)
				&& 1 == LandPathNodeMaker.getNodeType(this.mob.world, this.mob, t, j + 1, u, v, w, x, false, false, true)) {
				pathAwareEntity.getJumpControl().setActive();
			}
		}

		if (!playerEntity.abilities.creativeMode && this.field_5351 >= this.speed * 0.5F && this.mob.getRandom().nextFloat() < 0.006F && !this.field_5352) {
			ItemStack itemStack = playerEntity.getStackInHand();
			if (itemStack != null && itemStack.getItem() == Items.CARROT_ON_A_STICK) {
				itemStack.damage(1, playerEntity);
				if (itemStack.count == 0) {
					ItemStack itemStack2 = new ItemStack(Items.FISHING_ROD);
					itemStack2.setNbt(itemStack.getNbt());
					playerEntity.inventory.main[playerEntity.inventory.selectedSlot] = itemStack2;
				}
			}
		}

		this.mob.travel(0.0F, g);
	}

	private boolean method_8381(Block block) {
		return block instanceof StairsBlock || block instanceof SlabBlock;
	}

	public boolean method_4493() {
		return this.field_5352;
	}

	public void method_4494() {
		this.field_5352 = true;
		this.field_5353 = 0;
		this.field_5354 = this.mob.getRandom().nextInt(841) + 140;
	}

	public boolean method_4495() {
		return !this.method_4493() && this.field_5351 > this.speed * 0.3F;
	}
}
