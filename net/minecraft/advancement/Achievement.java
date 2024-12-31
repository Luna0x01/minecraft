package net.minecraft.advancement;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatFormatter;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.Formatting;
import net.minecraft.util.JsonElementProvider;

public class Achievement extends Stat {
	public final int column;
	public final int row;
	public final Achievement parent;
	private final String translationKey;
	private StatFormatter statFormatter;
	public final ItemStack logo;
	private boolean challenge;

	public Achievement(String string, String string2, int i, int j, Item item, Achievement achievement) {
		this(string, string2, i, j, new ItemStack(item), achievement);
	}

	public Achievement(String string, String string2, int i, int j, Block block, Achievement achievement) {
		this(string, string2, i, j, new ItemStack(block), achievement);
	}

	public Achievement(String string, String string2, int i, int j, ItemStack itemStack, Achievement achievement) {
		super(string, new TranslatableText("achievement." + string2));
		this.logo = itemStack;
		this.translationKey = "achievement." + string2 + ".desc";
		this.column = i;
		this.row = j;
		if (i < AchievementsAndCriterions.minColumn) {
			AchievementsAndCriterions.minColumn = i;
		}

		if (j < AchievementsAndCriterions.minRow) {
			AchievementsAndCriterions.minRow = j;
		}

		if (i > AchievementsAndCriterions.maxColumn) {
			AchievementsAndCriterions.maxColumn = i;
		}

		if (j > AchievementsAndCriterions.maxRow) {
			AchievementsAndCriterions.maxRow = j;
		}

		this.parent = achievement;
	}

	public Achievement localOnly() {
		this.localOnly = true;
		return this;
	}

	public Achievement challenge() {
		this.challenge = true;
		return this;
	}

	public Achievement addStat() {
		super.addStat();
		AchievementsAndCriterions.ACHIEVEMENTS.add(this);
		return this;
	}

	@Override
	public boolean isAchievement() {
		return true;
	}

	@Override
	public Text getText() {
		Text text = super.getText();
		text.getStyle().setFormatting(this.isChallenge() ? Formatting.DARK_PURPLE : Formatting.GREEN);
		return text;
	}

	public Achievement setJsonElementProvider(Class<? extends JsonElementProvider> class_) {
		return (Achievement)super.setJsonElementProvider(class_);
	}

	public String getDescription() {
		return this.statFormatter != null ? this.statFormatter.format(CommonI18n.translate(this.translationKey)) : CommonI18n.translate(this.translationKey);
	}

	public Achievement setStatFormatter(StatFormatter statFormatter) {
		this.statFormatter = statFormatter;
		return this;
	}

	public boolean isChallenge() {
		return this.challenge;
	}
}
