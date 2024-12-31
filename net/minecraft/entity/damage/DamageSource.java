package net.minecraft.entity.damage;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.CommonI18n;
import net.minecraft.world.explosion.Explosion;

public class DamageSource {
	public static DamageSource FIRE = new DamageSource("inFire").setFire();
	public static DamageSource LIGHTNING_BOLT = new DamageSource("lightningBolt");
	public static DamageSource ON_FIRE = new DamageSource("onFire").setBypassesArmor().setFire();
	public static DamageSource LAVA = new DamageSource("lava").setFire();
	public static DamageSource IN_WALL = new DamageSource("inWall").setBypassesArmor();
	public static DamageSource DROWN = new DamageSource("drown").setBypassesArmor();
	public static DamageSource STARVE = new DamageSource("starve").setBypassesArmor().setUnblockable();
	public static DamageSource CACTUS = new DamageSource("cactus");
	public static DamageSource FALL = new DamageSource("fall").setBypassesArmor();
	public static DamageSource OUT_OF_WORLD = new DamageSource("outOfWorld").setBypassesArmor().setOutOfWorld();
	public static DamageSource GENERIC = new DamageSource("generic").setBypassesArmor();
	public static DamageSource MAGIC = new DamageSource("magic").setBypassesArmor().setUsesMagic();
	public static DamageSource WITHER = new DamageSource("wither").setBypassesArmor();
	public static DamageSource ANVIL = new DamageSource("anvil");
	public static DamageSource FALLING_BLOCK = new DamageSource("fallingBlock");
	private boolean bypassesArmor;
	private boolean outOfWorld;
	private boolean unblockable;
	private float exhaustion = 0.3F;
	private boolean fire;
	private boolean projectile;
	private boolean scaleWithDifficulty;
	private boolean magic;
	private boolean explosive;
	public String name;

	public static DamageSource mob(LivingEntity attacker) {
		return new EntityDamageSource("mob", attacker);
	}

	public static DamageSource player(PlayerEntity attacker) {
		return new EntityDamageSource("player", attacker);
	}

	public static DamageSource arrow(AbstractArrowEntity arrow, Entity attacker) {
		return new ProjectileDamageSource("arrow", arrow, attacker).setProjectile();
	}

	public static DamageSource fire(ExplosiveProjectileEntity projectile, Entity attacker) {
		return attacker == null
			? new ProjectileDamageSource("onFire", projectile, projectile).setFire().setProjectile()
			: new ProjectileDamageSource("fireball", projectile, attacker).setFire().setProjectile();
	}

	public static DamageSource thrownProjectile(Entity projectile, Entity attacker) {
		return new ProjectileDamageSource("thrown", projectile, attacker).setProjectile();
	}

	public static DamageSource magic(Entity magic, Entity attacker) {
		return new ProjectileDamageSource("indirectMagic", magic, attacker).setBypassesArmor().setUsesMagic();
	}

	public static DamageSource thorns(Entity attacker) {
		return new EntityDamageSource("thorns", attacker).setThorns().setUsesMagic();
	}

	public static DamageSource explosion(Explosion explosion) {
		return explosion != null && explosion.getCausingEntity() != null
			? new EntityDamageSource("explosion.player", explosion.getCausingEntity()).setScaledWithDifficulty().setExplosive()
			: new DamageSource("explosion").setScaledWithDifficulty().setExplosive();
	}

	public boolean isProjectile() {
		return this.projectile;
	}

	public DamageSource setProjectile() {
		this.projectile = true;
		return this;
	}

	public boolean isExplosive() {
		return this.explosive;
	}

	public DamageSource setExplosive() {
		this.explosive = true;
		return this;
	}

	public boolean bypassesArmor() {
		return this.bypassesArmor;
	}

	public float getExhaustion() {
		return this.exhaustion;
	}

	public boolean isOutOfWorld() {
		return this.outOfWorld;
	}

	public boolean isUnblockable() {
		return this.unblockable;
	}

	protected DamageSource(String string) {
		this.name = string;
	}

	public Entity getSource() {
		return this.getAttacker();
	}

	public Entity getAttacker() {
		return null;
	}

	protected DamageSource setBypassesArmor() {
		this.bypassesArmor = true;
		this.exhaustion = 0.0F;
		return this;
	}

	protected DamageSource setOutOfWorld() {
		this.outOfWorld = true;
		return this;
	}

	protected DamageSource setUnblockable() {
		this.unblockable = true;
		this.exhaustion = 0.0F;
		return this;
	}

	protected DamageSource setFire() {
		this.fire = true;
		return this;
	}

	public Text getDeathMessage(LivingEntity entity) {
		LivingEntity livingEntity = entity.getOpponent();
		String string = "death.attack." + this.name;
		String string2 = string + ".player";
		return livingEntity != null && CommonI18n.hasTranslation(string2)
			? new TranslatableText(string2, entity.getName(), livingEntity.getName())
			: new TranslatableText(string, entity.getName());
	}

	public boolean isFire() {
		return this.fire;
	}

	public String getName() {
		return this.name;
	}

	public DamageSource setScaledWithDifficulty() {
		this.scaleWithDifficulty = true;
		return this;
	}

	public boolean isScaledWithDifficulty() {
		return this.scaleWithDifficulty;
	}

	public boolean getMagic() {
		return this.magic;
	}

	public DamageSource setUsesMagic() {
		this.magic = true;
		return this;
	}

	public boolean isSourceCreativePlayer() {
		Entity entity = this.getAttacker();
		return entity instanceof PlayerEntity && ((PlayerEntity)entity).abilities.creativeMode;
	}
}
