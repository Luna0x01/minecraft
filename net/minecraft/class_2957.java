package net.minecraft;

import java.util.UUID;
import net.minecraft.text.Text;

public abstract class class_2957 {
	private final UUID uuid;
	protected Text title;
	protected float health;
	protected class_2957.Color color;
	protected class_2957.Division division;
	protected boolean field_14418;
	protected boolean field_14419;
	protected boolean field_14420;

	public class_2957(UUID uUID, Text text, class_2957.Color color, class_2957.Division division) {
		this.uuid = uUID;
		this.title = text;
		this.color = color;
		this.division = division;
		this.health = 1.0F;
	}

	public UUID getUuid() {
		return this.uuid;
	}

	public Text getTitle() {
		return this.title;
	}

	public void setTitle(Text title) {
		this.title = title;
	}

	public float getHealth() {
		return this.health;
	}

	public void setHealth(float health) {
		this.health = health;
	}

	public class_2957.Color getColor() {
		return this.color;
	}

	public void setColor(class_2957.Color color) {
		this.color = color;
	}

	public class_2957.Division getDivision() {
		return this.division;
	}

	public void setDivision(class_2957.Division division) {
		this.division = division;
	}

	public boolean method_12929() {
		return this.field_14418;
	}

	public class_2957 method_12921(boolean bl) {
		this.field_14418 = bl;
		return this;
	}

	public boolean method_12930() {
		return this.field_14419;
	}

	public class_2957 method_12922(boolean bl) {
		this.field_14419 = bl;
		return this;
	}

	public class_2957 method_12923(boolean bl) {
		this.field_14420 = bl;
		return this;
	}

	public boolean method_12931() {
		return this.field_14420;
	}

	public static enum Color {
		PINK,
		BLUE,
		RED,
		GREEN,
		YELLOW,
		PURPLE,
		WHITE;
	}

	public static enum Division {
		PROGRESS,
		NOTCHED_6,
		NOTCHED_10,
		NOTCHED_12,
		NOTCHED_20;
	}
}
