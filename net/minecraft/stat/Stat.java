package net.minecraft.stat;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.StartupParameter;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.JsonElementProvider;

public class Stat {
	public final String name;
	private final Text nameId;
	public boolean localOnly;
	private final StatTypeProvider statTypeProvider;
	private final ScoreboardCriterion criterion;
	private Class<? extends JsonElementProvider> jsonElementProvider;
	private static NumberFormat DEFAULT_NUMBER_FORMAT = NumberFormat.getIntegerInstance(Locale.US);
	public static StatTypeProvider INTEGER_PROVIDER = new StatTypeProvider() {
		@Override
		public String formatValue(int value) {
			return Stat.DEFAULT_NUMBER_FORMAT.format((long)value);
		}
	};
	private static DecimalFormat DEFAULT_DECIMAL_FORMAT = new DecimalFormat("########0.00");
	public static StatTypeProvider TIME_PROVIDER = new StatTypeProvider() {
		@Override
		public String formatValue(int value) {
			double d = (double)value / 20.0;
			double e = d / 60.0;
			double f = e / 60.0;
			double g = f / 24.0;
			double h = g / 365.0;
			if (h > 0.5) {
				return Stat.DEFAULT_DECIMAL_FORMAT.format(h) + " y";
			} else if (g > 0.5) {
				return Stat.DEFAULT_DECIMAL_FORMAT.format(g) + " d";
			} else if (f > 0.5) {
				return Stat.DEFAULT_DECIMAL_FORMAT.format(f) + " h";
			} else {
				return e > 0.5 ? Stat.DEFAULT_DECIMAL_FORMAT.format(e) + " m" : d + " s";
			}
		}
	};
	public static StatTypeProvider DISTANCE_PROVIDER = new StatTypeProvider() {
		@Override
		public String formatValue(int value) {
			double d = (double)value / 100.0;
			double e = d / 1000.0;
			if (e > 0.5) {
				return Stat.DEFAULT_DECIMAL_FORMAT.format(e) + " km";
			} else {
				return d > 0.5 ? Stat.DEFAULT_DECIMAL_FORMAT.format(d) + " m" : value + " cm";
			}
		}
	};
	public static StatTypeProvider DAMAGE_PROVIDER = new StatTypeProvider() {
		@Override
		public String formatValue(int value) {
			return Stat.DEFAULT_DECIMAL_FORMAT.format((double)value * 0.1);
		}
	};

	public Stat(String string, Text text, StatTypeProvider statTypeProvider) {
		this.name = string;
		this.nameId = text;
		this.statTypeProvider = statTypeProvider;
		this.criterion = new StartupParameter(this);
		ScoreboardCriterion.OBJECTIVES.put(this.criterion.getName(), this.criterion);
	}

	public Stat(String string, Text text) {
		this(string, text, INTEGER_PROVIDER);
	}

	public Stat localOnly() {
		this.localOnly = true;
		return this;
	}

	public Stat addStat() {
		if (Stats.ID_TO_STAT.containsKey(this.name)) {
			throw new RuntimeException("Duplicate stat id: \"" + ((Stat)Stats.ID_TO_STAT.get(this.name)).nameId + "\" and \"" + this.nameId + "\" at id " + this.name);
		} else {
			Stats.ALL.add(this);
			Stats.ID_TO_STAT.put(this.name, this);
			return this;
		}
	}

	public boolean isAchievement() {
		return false;
	}

	public String formatValue(int value) {
		return this.statTypeProvider.formatValue(value);
	}

	public Text getText() {
		Text text = this.nameId.copy();
		text.getStyle().setFormatting(Formatting.GRAY);
		text.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ACHIEVEMENT, new LiteralText(this.name)));
		return text;
	}

	public Text method_8281() {
		Text text = this.getText();
		Text text2 = new LiteralText("[").append(text).append("]");
		text2.setStyle(text.getStyle());
		return text2;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			Stat stat = (Stat)object;
			return this.name.equals(stat.name);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return this.name.hashCode();
	}

	public String toString() {
		return "Stat{id="
			+ this.name
			+ ", nameId="
			+ this.nameId
			+ ", awardLocallyOnly="
			+ this.localOnly
			+ ", formatter="
			+ this.statTypeProvider
			+ ", objectiveCriteria="
			+ this.criterion
			+ '}';
	}

	public ScoreboardCriterion getCriterion() {
		return this.criterion;
	}

	public Class<? extends JsonElementProvider> getJsonElementProvider() {
		return this.jsonElementProvider;
	}

	public Stat setJsonElementProvider(Class<? extends JsonElementProvider> clazz) {
		this.jsonElementProvider = clazz;
		return this;
	}
}
