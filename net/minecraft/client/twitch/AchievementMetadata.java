package net.minecraft.client.twitch;

import net.minecraft.advancement.Achievement;

public class AchievementMetadata extends StreamMetadata {
	public AchievementMetadata(Achievement achievement) {
		super("achievement");
		this.put("achievement_id", achievement.name);
		this.put("achievement_name", achievement.getText().asUnformattedString());
		this.put("achievement_description", achievement.getDescription());
		this.setDescription("Achievement '" + achievement.getText().asUnformattedString() + "' obtained!");
	}
}
