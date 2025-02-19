package net.minecraft.client.render.entity.feature;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.IOException;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithHat;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;

public class VillagerClothingFeatureRenderer<T extends LivingEntity & VillagerDataContainer, M extends EntityModel<T> & ModelWithHat>
	extends FeatureRenderer<T, M> {
	private static final Int2ObjectMap<Identifier> LEVEL_TO_ID = Util.make(new Int2ObjectOpenHashMap(), levelToId -> {
		levelToId.put(1, new Identifier("stone"));
		levelToId.put(2, new Identifier("iron"));
		levelToId.put(3, new Identifier("gold"));
		levelToId.put(4, new Identifier("emerald"));
		levelToId.put(5, new Identifier("diamond"));
	});
	private final Object2ObjectMap<VillagerType, VillagerResourceMetadata.HatType> villagerTypeToHat = new Object2ObjectOpenHashMap();
	private final Object2ObjectMap<VillagerProfession, VillagerResourceMetadata.HatType> professionToHat = new Object2ObjectOpenHashMap();
	private final ResourceManager resourceManager;
	private final String entityType;

	public VillagerClothingFeatureRenderer(FeatureRendererContext<T, M> context, ResourceManager resourceManager, String entityType) {
		super(context);
		this.resourceManager = resourceManager;
		this.entityType = entityType;
	}

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l
	) {
		if (!livingEntity.isInvisible()) {
			VillagerData villagerData = livingEntity.getVillagerData();
			VillagerType villagerType = villagerData.getType();
			VillagerProfession villagerProfession = villagerData.getProfession();
			VillagerResourceMetadata.HatType hatType = this.getHatType(this.villagerTypeToHat, "type", Registry.VILLAGER_TYPE, villagerType);
			VillagerResourceMetadata.HatType hatType2 = this.getHatType(this.professionToHat, "profession", Registry.VILLAGER_PROFESSION, villagerProfession);
			M entityModel = this.getContextModel();
			entityModel.setHatVisible(
				hatType2 == VillagerResourceMetadata.HatType.NONE
					|| hatType2 == VillagerResourceMetadata.HatType.PARTIAL && hatType != VillagerResourceMetadata.HatType.FULL
			);
			Identifier identifier = this.findTexture("type", Registry.VILLAGER_TYPE.getId(villagerType));
			renderModel(entityModel, identifier, matrixStack, vertexConsumerProvider, i, livingEntity, 1.0F, 1.0F, 1.0F);
			entityModel.setHatVisible(true);
			if (villagerProfession != VillagerProfession.NONE && !livingEntity.isBaby()) {
				Identifier identifier2 = this.findTexture("profession", Registry.VILLAGER_PROFESSION.getId(villagerProfession));
				renderModel(entityModel, identifier2, matrixStack, vertexConsumerProvider, i, livingEntity, 1.0F, 1.0F, 1.0F);
				if (villagerProfession != VillagerProfession.NITWIT) {
					Identifier identifier3 = this.findTexture(
						"profession_level", (Identifier)LEVEL_TO_ID.get(MathHelper.clamp(villagerData.getLevel(), 1, LEVEL_TO_ID.size()))
					);
					renderModel(entityModel, identifier3, matrixStack, vertexConsumerProvider, i, livingEntity, 1.0F, 1.0F, 1.0F);
				}
			}
		}
	}

	private Identifier findTexture(String keyType, Identifier keyId) {
		return new Identifier(keyId.getNamespace(), "textures/entity/" + this.entityType + "/" + keyType + "/" + keyId.getPath() + ".png");
	}

	public <K> VillagerResourceMetadata.HatType getHatType(
		Object2ObjectMap<K, VillagerResourceMetadata.HatType> hatLookUp, String keyType, DefaultedRegistry<K> registry, K key
	) {
		return (VillagerResourceMetadata.HatType)hatLookUp.computeIfAbsent(key, k -> {
			try {
				Resource resource = this.resourceManager.getResource(this.findTexture(keyType, registry.getId(key)));

				VillagerResourceMetadata.HatType var7;
				label49: {
					try {
						VillagerResourceMetadata villagerResourceMetadata = resource.getMetadata(VillagerResourceMetadata.READER);
						if (villagerResourceMetadata != null) {
							var7 = villagerResourceMetadata.getHatType();
							break label49;
						}
					} catch (Throwable var9) {
						if (resource != null) {
							try {
								resource.close();
							} catch (Throwable var8) {
								var9.addSuppressed(var8);
							}
						}

						throw var9;
					}

					if (resource != null) {
						resource.close();
					}

					return VillagerResourceMetadata.HatType.NONE;
				}

				if (resource != null) {
					resource.close();
				}

				return var7;
			} catch (IOException var10) {
				return VillagerResourceMetadata.HatType.NONE;
			}
		});
	}
}
