package net.minecraft.item.map;

public class MapIcon {
	private byte typeId;
	private byte x;
	private byte y;
	private byte rot;

	public MapIcon(byte b, byte c, byte d, byte e) {
		this.typeId = b;
		this.x = c;
		this.y = d;
		this.rot = e;
	}

	public MapIcon(MapIcon mapIcon) {
		this.typeId = mapIcon.typeId;
		this.x = mapIcon.x;
		this.y = mapIcon.y;
		this.rot = mapIcon.rot;
	}

	public byte getTypeId() {
		return this.typeId;
	}

	public byte getX() {
		return this.x;
	}

	public byte getY() {
		return this.y;
	}

	public byte getRotation() {
		return this.rot;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof MapIcon)) {
			return false;
		} else {
			MapIcon mapIcon = (MapIcon)object;
			if (this.typeId != mapIcon.typeId) {
				return false;
			} else if (this.rot != mapIcon.rot) {
				return false;
			} else {
				return this.x != mapIcon.x ? false : this.y == mapIcon.y;
			}
		}
	}

	public int hashCode() {
		int i = this.typeId;
		i = 31 * i + this.x;
		i = 31 * i + this.y;
		return 31 * i + this.rot;
	}
}
