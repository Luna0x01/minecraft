package net.minecraft.advancement;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.advancement.criterion.CriterionInstance;
import net.minecraft.advancement.criterion.CriterionProgress;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.packet.c2s.play.AdvancementUpdatePacket;
import net.minecraft.network.packet.s2c.play.SelectAdvancementTabS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdvancementFile {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = new GsonBuilder()
		.registerTypeAdapter(AdvancementProgress.class, new AdvancementProgress.class_3335())
		.registerTypeAdapter(Identifier.class, new Identifier.class_3346())
		.setPrettyPrinting()
		.create();
	private static final TypeToken<Map<Identifier, AdvancementProgress>> field_16368 = new TypeToken<Map<Identifier, AdvancementProgress>>() {
	};
	private final MinecraftServer server;
	private final File file;
	private final Map<SimpleAdvancement, AdvancementProgress> field_16371 = Maps.newLinkedHashMap();
	private final Set<SimpleAdvancement> field_16372 = Sets.newLinkedHashSet();
	private final Set<SimpleAdvancement> field_16373 = Sets.newLinkedHashSet();
	private final Set<SimpleAdvancement> field_16374 = Sets.newLinkedHashSet();
	private ServerPlayerEntity player;
	@Nullable
	private SimpleAdvancement field_16376;
	private boolean field_16377 = true;

	public AdvancementFile(MinecraftServer minecraftServer, File file, ServerPlayerEntity serverPlayerEntity) {
		this.server = minecraftServer;
		this.file = file;
		this.player = serverPlayerEntity;
		this.method_14934();
	}

	public void setPlayer(ServerPlayerEntity player) {
		this.player = player;
	}

	public void method_14917() {
		for (Criterion<?> criterion : AchievementsAndCriterions.getCriterions()) {
			criterion.removeAdvancementFile(this);
		}
	}

	public void method_14922() {
		this.method_14917();
		this.field_16371.clear();
		this.field_16372.clear();
		this.field_16373.clear();
		this.field_16374.clear();
		this.field_16377 = true;
		this.field_16376 = null;
		this.method_14934();
	}

	private void method_14928() {
		for (SimpleAdvancement simpleAdvancement : this.server.method_14910().method_20451()) {
			this.method_14927(simpleAdvancement);
		}
	}

	private void method_14930() {
		List<SimpleAdvancement> list = Lists.newArrayList();

		for (Entry<SimpleAdvancement, AdvancementProgress> entry : this.field_16371.entrySet()) {
			if (((AdvancementProgress)entry.getValue()).method_14833()) {
				list.add(entry.getKey());
				this.field_16374.add(entry.getKey());
			}
		}

		for (SimpleAdvancement simpleAdvancement : list) {
			this.method_14931(simpleAdvancement);
		}
	}

	private void method_14932() {
		for (SimpleAdvancement simpleAdvancement : this.server.method_14910().method_20451()) {
			if (simpleAdvancement.getCriteria().isEmpty()) {
				this.method_14919(simpleAdvancement, "");
				simpleAdvancement.getRewards().method_14859(this.player);
			}
		}
	}

	private void method_14934() {
		if (this.file.isFile()) {
			try {
				JsonReader jsonReader = new JsonReader(new StringReader(Files.toString(this.file, StandardCharsets.UTF_8)));
				Throwable var2 = null;

				try {
					jsonReader.setLenient(false);
					Dynamic<JsonElement> dynamic = new Dynamic(JsonOps.INSTANCE, Streams.parse(jsonReader));
					if (!dynamic.get("DataVersion").flatMap(Dynamic::getNumberValue).isPresent()) {
						dynamic = dynamic.set("DataVersion", dynamic.createInt(1343));
					}

					dynamic = this.server.method_20343().update(DataFixTypes.ADVANCEMENTS, dynamic, dynamic.getInt("DataVersion"), 1631);
					dynamic = dynamic.remove("DataVersion");
					Map<Identifier, AdvancementProgress> map = (Map<Identifier, AdvancementProgress>)GSON.getAdapter(field_16368)
						.fromJsonTree((JsonElement)dynamic.getValue());
					if (map == null) {
						throw new JsonParseException("Found null for advancements");
					}

					Stream<Entry<Identifier, AdvancementProgress>> stream = map.entrySet().stream().sorted(Comparator.comparing(Entry::getValue));

					for (Entry<Identifier, AdvancementProgress> entry : (List)stream.collect(Collectors.toList())) {
						SimpleAdvancement simpleAdvancement = this.server.method_14910().method_14938((Identifier)entry.getKey());
						if (simpleAdvancement == null) {
							LOGGER.warn("Ignored advancement '{}' in progress file {} - it doesn't exist anymore?", entry.getKey(), this.file);
						} else {
							this.method_14920(simpleAdvancement, (AdvancementProgress)entry.getValue());
						}
					}
				} catch (Throwable var18) {
					var2 = var18;
					throw var18;
				} finally {
					if (jsonReader != null) {
						if (var2 != null) {
							try {
								jsonReader.close();
							} catch (Throwable var17) {
								var2.addSuppressed(var17);
							}
						} else {
							jsonReader.close();
						}
					}
				}
			} catch (JsonParseException var20) {
				LOGGER.error("Couldn't parse player advancements in {}", this.file, var20);
			} catch (IOException var21) {
				LOGGER.error("Couldn't access player advancements in {}", this.file, var21);
			}
		}

		this.method_14932();
		this.method_14930();
		this.method_14928();
	}

	public void method_14926() {
		Map<Identifier, AdvancementProgress> map = Maps.newHashMap();

		for (Entry<SimpleAdvancement, AdvancementProgress> entry : this.field_16371.entrySet()) {
			AdvancementProgress advancementProgress = (AdvancementProgress)entry.getValue();
			if (advancementProgress.method_14838()) {
				map.put(((SimpleAdvancement)entry.getKey()).getIdentifier(), advancementProgress);
			}
		}

		if (this.file.getParentFile() != null) {
			this.file.getParentFile().mkdirs();
		}

		try {
			Files.write(GSON.toJson(map), this.file, StandardCharsets.UTF_8);
		} catch (IOException var5) {
			LOGGER.error("Couldn't save player advancements to {}", this.file, var5);
		}
	}

	public boolean method_14919(SimpleAdvancement adv, String string) {
		boolean bl = false;
		AdvancementProgress advancementProgress = this.method_14923(adv);
		boolean bl2 = advancementProgress.method_14833();
		if (advancementProgress.method_14835(string)) {
			this.method_14929(adv);
			this.field_16374.add(adv);
			bl = true;
			if (!bl2 && advancementProgress.method_14833()) {
				adv.getRewards().method_14859(this.player);
				if (adv.getDisplay() != null && adv.getDisplay().method_15015() && this.player.world.getGameRules().getBoolean("announceAdvancements")) {
					this.server
						.getPlayerManager()
						.sendToAll(new TranslatableText("chat.type.advancement." + adv.getDisplay().getAdvancementType().getType(), this.player.getName(), adv.method_14803()));
				}
			}
		}

		if (advancementProgress.method_14833()) {
			this.method_14931(adv);
		}

		return bl;
	}

	public boolean method_14924(SimpleAdvancement simpleAdvancement, String string) {
		boolean bl = false;
		AdvancementProgress advancementProgress = this.method_14923(simpleAdvancement);
		if (advancementProgress.method_14840(string)) {
			this.method_14927(simpleAdvancement);
			this.field_16374.add(simpleAdvancement);
			bl = true;
		}

		if (!advancementProgress.method_14838()) {
			this.method_14931(simpleAdvancement);
		}

		return bl;
	}

	private void method_14927(SimpleAdvancement simpleAdvancement) {
		AdvancementProgress advancementProgress = this.method_14923(simpleAdvancement);
		if (!advancementProgress.method_14833()) {
			for (Entry<String, Criteria> entry : simpleAdvancement.getCriteria().entrySet()) {
				CriterionProgress criterionProgress = advancementProgress.getCriteria((String)entry.getKey());
				if (criterionProgress != null && !criterionProgress.hasBeenObtained()) {
					CriterionInstance criterionInstance = ((Criteria)entry.getValue()).method_14879();
					if (criterionInstance != null) {
						Criterion<CriterionInstance> criterion = AchievementsAndCriterions.getInstance(criterionInstance.getCriterion());
						if (criterion != null) {
							criterion.method_14973(this, new Criterion.class_3353<>(criterionInstance, simpleAdvancement, (String)entry.getKey()));
						}
					}
				}
			}
		}
	}

	private void method_14929(SimpleAdvancement simpleAdvancement) {
		AdvancementProgress advancementProgress = this.method_14923(simpleAdvancement);

		for (Entry<String, Criteria> entry : simpleAdvancement.getCriteria().entrySet()) {
			CriterionProgress criterionProgress = advancementProgress.getCriteria((String)entry.getKey());
			if (criterionProgress != null && (criterionProgress.hasBeenObtained() || advancementProgress.method_14833())) {
				CriterionInstance criterionInstance = ((Criteria)entry.getValue()).method_14879();
				if (criterionInstance != null) {
					Criterion<CriterionInstance> criterion = AchievementsAndCriterions.getInstance(criterionInstance.getCriterion());
					if (criterion != null) {
						criterion.method_14974(this, new Criterion.class_3353<>(criterionInstance, simpleAdvancement, (String)entry.getKey()));
					}
				}
			}
		}
	}

	public void method_14925(ServerPlayerEntity serverPlayerEntity) {
		if (this.field_16377 || !this.field_16373.isEmpty() || !this.field_16374.isEmpty()) {
			Map<Identifier, AdvancementProgress> map = Maps.newHashMap();
			Set<SimpleAdvancement> set = Sets.newLinkedHashSet();
			Set<Identifier> set2 = Sets.newLinkedHashSet();

			for (SimpleAdvancement simpleAdvancement : this.field_16374) {
				if (this.field_16372.contains(simpleAdvancement)) {
					map.put(simpleAdvancement.getIdentifier(), this.field_16371.get(simpleAdvancement));
				}
			}

			for (SimpleAdvancement simpleAdvancement2 : this.field_16373) {
				if (this.field_16372.contains(simpleAdvancement2)) {
					set.add(simpleAdvancement2);
				} else {
					set2.add(simpleAdvancement2.getIdentifier());
				}
			}

			if (this.field_16377 || !map.isEmpty() || !set.isEmpty() || !set2.isEmpty()) {
				serverPlayerEntity.networkHandler.sendPacket(new AdvancementUpdatePacket(this.field_16377, set, set2, map));
				this.field_16373.clear();
				this.field_16374.clear();
			}
		}

		this.field_16377 = false;
	}

	public void method_14918(@Nullable SimpleAdvancement simpleAdvancement) {
		SimpleAdvancement simpleAdvancement2 = this.field_16376;
		if (simpleAdvancement != null && simpleAdvancement.getParent() == null && simpleAdvancement.getDisplay() != null) {
			this.field_16376 = simpleAdvancement;
		} else {
			this.field_16376 = null;
		}

		if (simpleAdvancement2 != this.field_16376) {
			this.player.networkHandler.sendPacket(new SelectAdvancementTabS2CPacket(this.field_16376 == null ? null : this.field_16376.getIdentifier()));
		}
	}

	public AdvancementProgress method_14923(SimpleAdvancement simpleAdvancement) {
		AdvancementProgress advancementProgress = (AdvancementProgress)this.field_16371.get(simpleAdvancement);
		if (advancementProgress == null) {
			advancementProgress = new AdvancementProgress();
			this.method_14920(simpleAdvancement, advancementProgress);
		}

		return advancementProgress;
	}

	private void method_14920(SimpleAdvancement simpleAdvancement, AdvancementProgress advancementProgress) {
		advancementProgress.method_14836(simpleAdvancement.getCriteria(), simpleAdvancement.getRequirements());
		this.field_16371.put(simpleAdvancement, advancementProgress);
	}

	private void method_14931(SimpleAdvancement simpleAdvancement) {
		boolean bl = this.method_14933(simpleAdvancement);
		boolean bl2 = this.field_16372.contains(simpleAdvancement);
		if (bl && !bl2) {
			this.field_16372.add(simpleAdvancement);
			this.field_16373.add(simpleAdvancement);
			if (this.field_16371.containsKey(simpleAdvancement)) {
				this.field_16374.add(simpleAdvancement);
			}
		} else if (!bl && bl2) {
			this.field_16372.remove(simpleAdvancement);
			this.field_16373.add(simpleAdvancement);
		}

		if (bl != bl2 && simpleAdvancement.getParent() != null) {
			this.method_14931(simpleAdvancement.getParent());
		}

		for (SimpleAdvancement simpleAdvancement2 : simpleAdvancement.getChildren()) {
			this.method_14931(simpleAdvancement2);
		}
	}

	private boolean method_14933(SimpleAdvancement simpleAdvancement) {
		for (int i = 0; simpleAdvancement != null && i <= 2; i++) {
			if (i == 0 && this.method_14935(simpleAdvancement)) {
				return true;
			}

			if (simpleAdvancement.getDisplay() == null) {
				return false;
			}

			AdvancementProgress advancementProgress = this.method_14923(simpleAdvancement);
			if (advancementProgress.method_14833()) {
				return true;
			}

			if (simpleAdvancement.getDisplay().method_15016()) {
				return false;
			}

			simpleAdvancement = simpleAdvancement.getParent();
		}

		return false;
	}

	private boolean method_14935(SimpleAdvancement simpleAdvancement) {
		AdvancementProgress advancementProgress = this.method_14923(simpleAdvancement);
		if (advancementProgress.method_14833()) {
			return true;
		} else {
			for (SimpleAdvancement simpleAdvancement2 : simpleAdvancement.getChildren()) {
				if (this.method_14935(simpleAdvancement2)) {
					return true;
				}
			}

			return false;
		}
	}
}
