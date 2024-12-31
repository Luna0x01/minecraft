package net.minecraft;

public class class_4228 {
	private int field_20767;
	private int field_20768;
	private int field_20769;
	private int field_20770;

	public class_4228(int i, int j, int k, int l) {
		this.field_20767 = i;
		this.field_20768 = j;
		this.field_20769 = k;
		this.field_20770 = l;
	}

	public int method_19177() {
		return this.field_20767;
	}

	public int method_19178() {
		return this.field_20768;
	}

	public int method_19180() {
		return this.field_20769;
	}

	public int method_19181() {
		return this.field_20770;
	}

	public boolean method_19179(int i, int j) {
		return i >= this.field_20767 && i <= this.field_20767 + this.field_20769 && j >= this.field_20768 && j <= this.field_20768 + this.field_20770;
	}
}
