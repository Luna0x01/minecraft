package net.minecraft.world.event;

import net.minecraft.util.registry.Registry;

public class GameEvent {
	public static final GameEvent BLOCK_ATTACH = register("block_attach");
	public static final GameEvent BLOCK_CHANGE = register("block_change");
	public static final GameEvent BLOCK_CLOSE = register("block_close");
	public static final GameEvent BLOCK_DESTROY = register("block_destroy");
	public static final GameEvent BLOCK_DETACH = register("block_detach");
	public static final GameEvent BLOCK_OPEN = register("block_open");
	public static final GameEvent BLOCK_PLACE = register("block_place");
	public static final GameEvent BLOCK_PRESS = register("block_press");
	public static final GameEvent BLOCK_SWITCH = register("block_switch");
	public static final GameEvent BLOCK_UNPRESS = register("block_unpress");
	public static final GameEvent BLOCK_UNSWITCH = register("block_unswitch");
	public static final GameEvent CONTAINER_CLOSE = register("container_close");
	public static final GameEvent CONTAINER_OPEN = register("container_open");
	public static final GameEvent DISPENSE_FAIL = register("dispense_fail");
	public static final GameEvent DRINKING_FINISH = register("drinking_finish");
	public static final GameEvent EAT = register("eat");
	public static final GameEvent ELYTRA_FREE_FALL = register("elytra_free_fall");
	public static final GameEvent ENTITY_DAMAGED = register("entity_damaged");
	public static final GameEvent ENTITY_KILLED = register("entity_killed");
	public static final GameEvent ENTITY_PLACE = register("entity_place");
	public static final GameEvent EQUIP = register("equip");
	public static final GameEvent EXPLODE = register("explode");
	public static final GameEvent FISHING_ROD_CAST = register("fishing_rod_cast");
	public static final GameEvent FISHING_ROD_REEL_IN = register("fishing_rod_reel_in");
	public static final GameEvent FLAP = register("flap");
	public static final GameEvent FLUID_PICKUP = register("fluid_pickup");
	public static final GameEvent FLUID_PLACE = register("fluid_place");
	public static final GameEvent HIT_GROUND = register("hit_ground");
	public static final GameEvent MOB_INTERACT = register("mob_interact");
	public static final GameEvent LIGHTNING_STRIKE = register("lightning_strike");
	public static final GameEvent MINECART_MOVING = register("minecart_moving");
	public static final GameEvent PISTON_CONTRACT = register("piston_contract");
	public static final GameEvent PISTON_EXTEND = register("piston_extend");
	public static final GameEvent PRIME_FUSE = register("prime_fuse");
	public static final GameEvent PROJECTILE_LAND = register("projectile_land");
	public static final GameEvent PROJECTILE_SHOOT = register("projectile_shoot");
	public static final GameEvent RAVAGER_ROAR = register("ravager_roar");
	public static final GameEvent RING_BELL = register("ring_bell");
	public static final GameEvent SHEAR = register("shear");
	public static final GameEvent SHULKER_CLOSE = register("shulker_close");
	public static final GameEvent SHULKER_OPEN = register("shulker_open");
	public static final GameEvent SPLASH = register("splash");
	public static final GameEvent STEP = register("step");
	public static final GameEvent SWIM = register("swim");
	public static final GameEvent WOLF_SHAKING = register("wolf_shaking");
	public static final int DEFAULT_RANGE = 16;
	private final String id;
	private final int range;

	public GameEvent(String id, int range) {
		this.id = id;
		this.range = range;
	}

	public String getId() {
		return this.id;
	}

	public int getRange() {
		return this.range;
	}

	private static GameEvent register(String id) {
		return register(id, 16);
	}

	private static GameEvent register(String id, int range) {
		return Registry.register(Registry.GAME_EVENT, id, new GameEvent(id, range));
	}

	public String toString() {
		return "Game Event{ " + this.id + " , " + this.range + "}";
	}
}
