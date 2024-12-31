package net.minecraft;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ListWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.BiomeSourceType;

public class class_4156 extends Screen {
	private static final List<Identifier> field_20243 = (List<Identifier>)Registry.CHUNK_GENERATOR_TYPE
		.getKeySet()
		.stream()
		.filter(identifier -> Registry.CHUNK_GENERATOR_TYPE.getByIdentifier(identifier).method_17041())
		.collect(Collectors.toList());
	private final CreateWorldScreen field_20244;
	private final List<Identifier> field_20245 = Lists.newArrayList();
	private final Identifier[] field_20246 = new Identifier[Registry.BIOME.getKeySet().size()];
	private String field_20247;
	private class_4156.class_2316 field_20248;
	private int field_20249;
	private ButtonWidget field_20250;

	public class_4156(CreateWorldScreen createWorldScreen, NbtCompound nbtCompound) {
		this.field_20244 = createWorldScreen;
		int i = 0;

		for (Identifier identifier : Registry.BIOME.getKeySet()) {
			this.field_20246[i] = identifier;
			i++;
		}

		Arrays.sort(this.field_20246, (identifierx, identifier2) -> {
			String string = Registry.BIOME.getByIdentifier(identifierx).method_16445().getString();
			String string2 = Registry.BIOME.getByIdentifier(identifier2).method_16445().getString();
			return string.compareTo(string2);
		});
		this.method_18562(nbtCompound);
	}

	private void method_18562(NbtCompound nbtCompound) {
		if (nbtCompound.contains("chunk_generator", 10) && nbtCompound.getCompound("chunk_generator").contains("type", 8)) {
			Identifier identifier = new Identifier(nbtCompound.getCompound("chunk_generator").getString("type"));

			for (int i = 0; i < field_20243.size(); i++) {
				if (((Identifier)field_20243.get(i)).equals(identifier)) {
					this.field_20249 = i;
					break;
				}
			}
		}

		if (nbtCompound.contains("biome_source", 10) && nbtCompound.getCompound("biome_source").contains("biomes", 9)) {
			NbtList nbtList = nbtCompound.getCompound("biome_source").getList("biomes", 8);

			for (int j = 0; j < nbtList.size(); j++) {
				this.field_20245.add(new Identifier(nbtList.getString(j)));
			}
		}
	}

	private NbtCompound method_18572() {
		NbtCompound nbtCompound = new NbtCompound();
		NbtCompound nbtCompound2 = new NbtCompound();
		nbtCompound2.putString("type", Registry.BIOME_SOURCE_TYPE.getId(BiomeSourceType.FIXED).toString());
		NbtCompound nbtCompound3 = new NbtCompound();
		NbtList nbtList = new NbtList();

		for (Identifier identifier : this.field_20245) {
			nbtList.add((NbtElement)(new NbtString(identifier.toString())));
		}

		nbtCompound3.put("biomes", nbtList);
		nbtCompound2.put("options", nbtCompound3);
		NbtCompound nbtCompound4 = new NbtCompound();
		NbtCompound nbtCompound5 = new NbtCompound();
		nbtCompound4.putString("type", ((Identifier)field_20243.get(this.field_20249)).toString());
		nbtCompound5.putString("default_block", "minecraft:stone");
		nbtCompound5.putString("default_fluid", "minecraft:water");
		nbtCompound4.put("options", nbtCompound5);
		nbtCompound.put("biome_source", nbtCompound2);
		nbtCompound.put("chunk_generator", nbtCompound4);
		return nbtCompound;
	}

	@Nullable
	@Override
	public class_4122 getFocused() {
		return this.field_20248;
	}

	@Override
	protected void init() {
		this.client.field_19946.method_18191(true);
		this.field_20247 = I18n.translate("createWorld.customize.buffet.title");
		this.field_20248 = new class_4156.class_2316();
		this.field_20307.add(this.field_20248);
		this.addButton(
			new ButtonWidget(
				2,
				(this.width - 200) / 2,
				40,
				200,
				20,
				I18n.translate("createWorld.customize.buffet.generatortype")
					+ " "
					+ I18n.translate(Util.createTranslationKey("generator", (Identifier)field_20243.get(this.field_20249)))
			) {
				@Override
				public void method_18374(double d, double e) {
					class_4156.this.field_20249++;
					if (class_4156.this.field_20249 >= class_4156.field_20243.size()) {
						class_4156.this.field_20249 = 0;
					}

					this.message = I18n.translate("createWorld.customize.buffet.generatortype")
						+ " "
						+ I18n.translate(Util.createTranslationKey("generator", (Identifier)class_4156.field_20243.get(class_4156.this.field_20249)));
				}
			}
		);
		this.field_20250 = this.addButton(new ButtonWidget(0, this.width / 2 - 155, this.height - 28, 150, 20, I18n.translate("gui.done")) {
			@Override
			public void method_18374(double d, double e) {
				class_4156.this.field_20244.field_20472 = class_4156.this.method_18572();
				class_4156.this.client.setScreen(class_4156.this.field_20244);
			}
		});
		this.addButton(new ButtonWidget(1, this.width / 2 + 5, this.height - 28, 150, 20, I18n.translate("gui.cancel")) {
			@Override
			public void method_18374(double d, double e) {
				class_4156.this.client.setScreen(class_4156.this.field_20244);
			}
		});
		this.method_18570();
	}

	public void method_18570() {
		this.field_20250.active = !this.field_20245.isEmpty();
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderDirtBackground(0);
		this.field_20248.render(mouseX, mouseY, tickDelta);
		this.drawCenteredString(this.textRenderer, this.field_20247, this.width / 2, 8, 16777215);
		this.drawCenteredString(this.textRenderer, I18n.translate("createWorld.customize.buffet.generator"), this.width / 2, 30, 10526880);
		this.drawCenteredString(this.textRenderer, I18n.translate("createWorld.customize.buffet.biome"), this.width / 2, 68, 10526880);
		super.render(mouseX, mouseY, tickDelta);
	}

	class class_2316 extends ListWidget {
		private class_2316() {
			super(class_4156.this.client, class_4156.this.width, class_4156.this.height, 80, class_4156.this.height - 37, 16);
		}

		@Override
		protected int getEntryCount() {
			return class_4156.this.field_20246.length;
		}

		@Override
		protected boolean method_18414(int i, int j, double d, double e) {
			class_4156.this.field_20245.clear();
			class_4156.this.field_20245.add(class_4156.this.field_20246[i]);
			class_4156.this.method_18570();
			return true;
		}

		@Override
		protected boolean isEntrySelected(int index) {
			return class_4156.this.field_20245.contains(class_4156.this.field_20246[index]);
		}

		@Override
		protected void renderBackground() {
		}

		@Override
		protected void method_1055(int i, int j, int k, int l, int m, int n, float f) {
			this.drawWithShadow(
				class_4156.this.textRenderer, Registry.BIOME.getByIdentifier(class_4156.this.field_20246[i]).method_16445().getString(), j + 5, k + 2, 16777215
			);
		}
	}
}
