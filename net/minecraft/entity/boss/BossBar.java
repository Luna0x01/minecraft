package net.minecraft.entity.boss;

public final class BossBar {
	public static float percent;
	public static int framesToLive;
	public static String name;
	public static boolean darkenSky;

	public static void update(BossBarProvider provider, boolean darkenSky) {
		percent = provider.getHealth() / provider.getMaxHealth();
		framesToLive = 100;
		name = provider.getName().asFormattedString();
		BossBar.darkenSky = darkenSky;
	}
}
