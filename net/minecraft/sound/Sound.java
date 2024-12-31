package net.minecraft.sound;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.SimpleRegistry;

public class Sound {
	public static final SimpleRegistry<Identifier, Sound> REGISTRY = new SimpleRegistry<>();
	private final Identifier id;
	private static int field_13907 = 0;

	public Sound(Identifier identifier) {
		this.id = identifier;
	}

	public Identifier getId() {
		return this.id;
	}

	public static void register() {
		register("ambient.cave");
		register("block.anvil.break");
		register("block.anvil.destroy");
		register("block.anvil.fall");
		register("block.anvil.hit");
		register("block.anvil.land");
		register("block.anvil.place");
		register("block.anvil.step");
		register("block.anvil.use");
		register("block.brewing_stand.brew");
		register("block.chest.close");
		register("block.chest.locked");
		register("block.chest.open");
		register("block.chorus_flower.death");
		register("block.chorus_flower.grow");
		register("block.cloth.break");
		register("block.cloth.fall");
		register("block.cloth.hit");
		register("block.cloth.place");
		register("block.cloth.step");
		register("block.comparator.click");
		register("block.dispenser.dispense");
		register("block.dispenser.fail");
		register("block.dispenser.launch");
		register("block.end_gateway.spawn");
		register("block.enderchest.close");
		register("block.enderchest.open");
		register("block.fence_gate.close");
		register("block.fence_gate.open");
		register("block.fire.ambient");
		register("block.fire.extinguish");
		register("block.furnace.fire_crackle");
		register("block.glass.break");
		register("block.glass.fall");
		register("block.glass.hit");
		register("block.glass.place");
		register("block.glass.step");
		register("block.grass.break");
		register("block.grass.fall");
		register("block.grass.hit");
		register("block.grass.place");
		register("block.grass.step");
		register("block.gravel.break");
		register("block.gravel.fall");
		register("block.gravel.hit");
		register("block.gravel.place");
		register("block.gravel.step");
		register("block.iron_door.close");
		register("block.iron_door.open");
		register("block.iron_trapdoor.close");
		register("block.iron_trapdoor.open");
		register("block.ladder.break");
		register("block.ladder.fall");
		register("block.ladder.hit");
		register("block.ladder.place");
		register("block.ladder.step");
		register("block.lava.ambient");
		register("block.lava.extinguish");
		register("block.lava.pop");
		register("block.lever.click");
		register("block.metal.break");
		register("block.metal.fall");
		register("block.metal.hit");
		register("block.metal.place");
		register("block.metal.step");
		register("block.metal_pressureplate.click_off");
		register("block.metal_pressureplate.click_on");
		register("block.note.basedrum");
		register("block.note.bass");
		register("block.note.harp");
		register("block.note.hat");
		register("block.note.pling");
		register("block.note.snare");
		register("block.piston.contract");
		register("block.piston.extend");
		register("block.portal.ambient");
		register("block.portal.travel");
		register("block.portal.trigger");
		register("block.redstone_torch.burnout");
		register("block.sand.break");
		register("block.sand.fall");
		register("block.sand.hit");
		register("block.sand.place");
		register("block.sand.step");
		register("block.slime.break");
		register("block.slime.fall");
		register("block.slime.hit");
		register("block.slime.place");
		register("block.slime.step");
		register("block.snow.break");
		register("block.snow.fall");
		register("block.snow.hit");
		register("block.snow.place");
		register("block.snow.step");
		register("block.stone.break");
		register("block.stone.fall");
		register("block.stone.hit");
		register("block.stone.place");
		register("block.stone.step");
		register("block.stone_button.click_off");
		register("block.stone_button.click_on");
		register("block.stone_pressureplate.click_off");
		register("block.stone_pressureplate.click_on");
		register("block.tripwire.attach");
		register("block.tripwire.click_off");
		register("block.tripwire.click_on");
		register("block.tripwire.detach");
		register("block.water.ambient");
		register("block.waterlily.place");
		register("block.wood.break");
		register("block.wood.fall");
		register("block.wood.hit");
		register("block.wood.place");
		register("block.wood.step");
		register("block.wood_button.click_off");
		register("block.wood_button.click_on");
		register("block.wood_pressureplate.click_off");
		register("block.wood_pressureplate.click_on");
		register("block.wooden_door.close");
		register("block.wooden_door.open");
		register("block.wooden_trapdoor.close");
		register("block.wooden_trapdoor.open");
		register("enchant.thorns.hit");
		register("entity.armorstand.break");
		register("entity.armorstand.fall");
		register("entity.armorstand.hit");
		register("entity.armorstand.place");
		register("entity.arrow.hit");
		register("entity.arrow.hit_player");
		register("entity.arrow.shoot");
		register("entity.bat.ambient");
		register("entity.bat.death");
		register("entity.bat.hurt");
		register("entity.bat.loop");
		register("entity.bat.takeoff");
		register("entity.blaze.ambient");
		register("entity.blaze.burn");
		register("entity.blaze.death");
		register("entity.blaze.hurt");
		register("entity.blaze.shoot");
		register("entity.bobber.splash");
		register("entity.bobber.throw");
		register("entity.cat.ambient");
		register("entity.cat.death");
		register("entity.cat.hiss");
		register("entity.cat.hurt");
		register("entity.cat.purr");
		register("entity.cat.purreow");
		register("entity.chicken.ambient");
		register("entity.chicken.death");
		register("entity.chicken.egg");
		register("entity.chicken.hurt");
		register("entity.chicken.step");
		register("entity.cow.ambient");
		register("entity.cow.death");
		register("entity.cow.hurt");
		register("entity.cow.milk");
		register("entity.cow.step");
		register("entity.creeper.death");
		register("entity.creeper.hurt");
		register("entity.creeper.primed");
		register("entity.donkey.ambient");
		register("entity.donkey.angry");
		register("entity.donkey.chest");
		register("entity.donkey.death");
		register("entity.donkey.hurt");
		register("entity.egg.throw");
		register("entity.elder_guardian.ambient");
		register("entity.elder_guardian.ambient_land");
		register("entity.elder_guardian.curse");
		register("entity.elder_guardian.death");
		register("entity.elder_guardian.death_land");
		register("entity.elder_guardian.hurt");
		register("entity.elder_guardian.hurt_land");
		register("entity.enderdragon.ambient");
		register("entity.enderdragon.death");
		register("entity.enderdragon.flap");
		register("entity.enderdragon.growl");
		register("entity.enderdragon.hurt");
		register("entity.enderdragon.shoot");
		register("entity.enderdragon_fireball.explode");
		register("entity.endereye.launch");
		register("entity.endermen.ambient");
		register("entity.endermen.death");
		register("entity.endermen.hurt");
		register("entity.endermen.scream");
		register("entity.endermen.stare");
		register("entity.endermen.teleport");
		register("entity.endermite.ambient");
		register("entity.endermite.death");
		register("entity.endermite.hurt");
		register("entity.endermite.step");
		register("entity.enderpearl.throw");
		register("entity.experience_bottle.throw");
		register("entity.experience_orb.pickup");
		register("entity.experience_orb.touch");
		register("entity.firework.blast");
		register("entity.firework.blast_far");
		register("entity.firework.large_blast");
		register("entity.firework.large_blast_far");
		register("entity.firework.launch");
		register("entity.firework.shoot");
		register("entity.firework.twinkle");
		register("entity.firework.twinkle_far");
		register("entity.generic.big_fall");
		register("entity.generic.burn");
		register("entity.generic.death");
		register("entity.generic.drink");
		register("entity.generic.eat");
		register("entity.generic.explode");
		register("entity.generic.extinguish_fire");
		register("entity.generic.hurt");
		register("entity.generic.small_fall");
		register("entity.generic.splash");
		register("entity.generic.swim");
		register("entity.ghast.ambient");
		register("entity.ghast.death");
		register("entity.ghast.hurt");
		register("entity.ghast.scream");
		register("entity.ghast.shoot");
		register("entity.ghast.warn");
		register("entity.guardian.ambient");
		register("entity.guardian.ambient_land");
		register("entity.guardian.attack");
		register("entity.guardian.death");
		register("entity.guardian.death_land");
		register("entity.guardian.flop");
		register("entity.guardian.hurt");
		register("entity.guardian.hurt_land");
		register("entity.horse.ambient");
		register("entity.horse.angry");
		register("entity.horse.armor");
		register("entity.horse.breathe");
		register("entity.horse.death");
		register("entity.horse.eat");
		register("entity.horse.gallop");
		register("entity.horse.hurt");
		register("entity.horse.jump");
		register("entity.horse.land");
		register("entity.horse.saddle");
		register("entity.horse.step");
		register("entity.horse.step_wood");
		register("entity.hostile.big_fall");
		register("entity.hostile.death");
		register("entity.hostile.hurt");
		register("entity.hostile.small_fall");
		register("entity.hostile.splash");
		register("entity.hostile.swim");
		register("entity.irongolem.attack");
		register("entity.irongolem.death");
		register("entity.irongolem.hurt");
		register("entity.irongolem.step");
		register("entity.item.break");
		register("entity.item.pickup");
		register("entity.itemframe.add_item");
		register("entity.itemframe.break");
		register("entity.itemframe.place");
		register("entity.itemframe.remove_item");
		register("entity.itemframe.rotate_item");
		register("entity.leashknot.break");
		register("entity.leashknot.place");
		register("entity.lightning.impact");
		register("entity.lightning.thunder");
		register("entity.lingeringpotion.throw");
		register("entity.magmacube.death");
		register("entity.magmacube.hurt");
		register("entity.magmacube.jump");
		register("entity.magmacube.squish");
		register("entity.minecart.inside");
		register("entity.minecart.riding");
		register("entity.mooshroom.shear");
		register("entity.mule.ambient");
		register("entity.mule.death");
		register("entity.mule.hurt");
		register("entity.painting.break");
		register("entity.painting.place");
		register("entity.pig.ambient");
		register("entity.pig.death");
		register("entity.pig.hurt");
		register("entity.pig.saddle");
		register("entity.pig.step");
		register("entity.player.attack.crit");
		register("entity.player.attack.knockback");
		register("entity.player.attack.nodamage");
		register("entity.player.attack.strong");
		register("entity.player.attack.sweep");
		register("entity.player.attack.weak");
		register("entity.player.big_fall");
		register("entity.player.breath");
		register("entity.player.burp");
		register("entity.player.death");
		register("entity.player.hurt");
		register("entity.player.levelup");
		register("entity.player.small_fall");
		register("entity.player.splash");
		register("entity.player.swim");
		register("entity.rabbit.ambient");
		register("entity.rabbit.attack");
		register("entity.rabbit.death");
		register("entity.rabbit.hurt");
		register("entity.rabbit.jump");
		register("entity.sheep.ambient");
		register("entity.sheep.death");
		register("entity.sheep.hurt");
		register("entity.sheep.shear");
		register("entity.sheep.step");
		register("entity.shulker.ambient");
		register("entity.shulker.close");
		register("entity.shulker.death");
		register("entity.shulker.hurt");
		register("entity.shulker.hurt_closed");
		register("entity.shulker.open");
		register("entity.shulker.shoot");
		register("entity.shulker.teleport");
		register("entity.shulker_bullet.hit");
		register("entity.shulker_bullet.hurt");
		register("entity.silverfish.ambient");
		register("entity.silverfish.death");
		register("entity.silverfish.hurt");
		register("entity.silverfish.step");
		register("entity.skeleton.ambient");
		register("entity.skeleton.death");
		register("entity.skeleton.hurt");
		register("entity.skeleton.shoot");
		register("entity.skeleton.step");
		register("entity.skeleton_horse.ambient");
		register("entity.skeleton_horse.death");
		register("entity.skeleton_horse.hurt");
		register("entity.slime.attack");
		register("entity.slime.death");
		register("entity.slime.hurt");
		register("entity.slime.jump");
		register("entity.slime.squish");
		register("entity.small_magmacube.death");
		register("entity.small_magmacube.hurt");
		register("entity.small_magmacube.squish");
		register("entity.small_slime.death");
		register("entity.small_slime.hurt");
		register("entity.small_slime.jump");
		register("entity.small_slime.squish");
		register("entity.snowball.throw");
		register("entity.snowman.ambient");
		register("entity.snowman.death");
		register("entity.snowman.hurt");
		register("entity.snowman.shoot");
		register("entity.spider.ambient");
		register("entity.spider.death");
		register("entity.spider.hurt");
		register("entity.spider.step");
		register("entity.splash_potion.break");
		register("entity.splash_potion.throw");
		register("entity.squid.ambient");
		register("entity.squid.death");
		register("entity.squid.hurt");
		register("entity.tnt.primed");
		register("entity.villager.ambient");
		register("entity.villager.death");
		register("entity.villager.hurt");
		register("entity.villager.no");
		register("entity.villager.trading");
		register("entity.villager.yes");
		register("entity.witch.ambient");
		register("entity.witch.death");
		register("entity.witch.drink");
		register("entity.witch.hurt");
		register("entity.witch.throw");
		register("entity.wither.ambient");
		register("entity.wither.break_block");
		register("entity.wither.death");
		register("entity.wither.hurt");
		register("entity.wither.shoot");
		register("entity.wither.spawn");
		register("entity.wolf.ambient");
		register("entity.wolf.death");
		register("entity.wolf.growl");
		register("entity.wolf.howl");
		register("entity.wolf.hurt");
		register("entity.wolf.pant");
		register("entity.wolf.shake");
		register("entity.wolf.step");
		register("entity.wolf.whine");
		register("entity.zombie.ambient");
		register("entity.zombie.attack_door_wood");
		register("entity.zombie.attack_iron_door");
		register("entity.zombie.break_door_wood");
		register("entity.zombie.death");
		register("entity.zombie.hurt");
		register("entity.zombie.infect");
		register("entity.zombie.step");
		register("entity.zombie_horse.ambient");
		register("entity.zombie_horse.death");
		register("entity.zombie_horse.hurt");
		register("entity.zombie_pig.ambient");
		register("entity.zombie_pig.angry");
		register("entity.zombie_pig.death");
		register("entity.zombie_pig.hurt");
		register("entity.zombie_villager.ambient");
		register("entity.zombie_villager.converted");
		register("entity.zombie_villager.cure");
		register("entity.zombie_villager.death");
		register("entity.zombie_villager.hurt");
		register("entity.zombie_villager.step");
		register("item.armor.equip_chain");
		register("item.armor.equip_diamond");
		register("item.armor.equip_generic");
		register("item.armor.equip_gold");
		register("item.armor.equip_iron");
		register("item.armor.equip_leather");
		register("item.bottle.fill");
		register("item.bottle.fill_dragonbreath");
		register("item.bucket.empty");
		register("item.bucket.empty_lava");
		register("item.bucket.fill");
		register("item.bucket.fill_lava");
		register("item.chorus_fruit.teleport");
		register("item.elytra.flying");
		register("item.firecharge.use");
		register("item.flintandsteel.use");
		register("item.hoe.till");
		register("item.shield.block");
		register("item.shield.break");
		register("item.shovel.flatten");
		register("music.creative");
		register("music.credits");
		register("music.dragon");
		register("music.end");
		register("music.game");
		register("music.menu");
		register("music.nether");
		register("record.11");
		register("record.13");
		register("record.blocks");
		register("record.cat");
		register("record.chirp");
		register("record.far");
		register("record.mall");
		register("record.mellohi");
		register("record.stal");
		register("record.strad");
		register("record.wait");
		register("record.ward");
		register("ui.button.click");
		register("weather.rain");
		register("weather.rain.above");
	}

	private static void register(String id) {
		Identifier identifier = new Identifier(id);
		REGISTRY.add(field_13907++, identifier, new Sound(identifier));
	}
}
