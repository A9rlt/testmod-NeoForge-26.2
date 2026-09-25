package net.schblackos.testmod.datagen;

import com.mojang.serialization.Codec;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.schblackos.testmod.Testmod;
import net.schblackos.testmod.item.ModArmorMaterial;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import static net.minecraft.core.component.DataComponents.bootstrap;

public class ModEquipmentAssetProvider implements DataProvider {
    private final PackOutput.PathProvider pathProvider;


    public ModEquipmentAssetProvider(PackOutput packOutput){
        this.pathProvider = packOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK,"equipment");
    }


private static void  bootstrap(BiConsumer<ResourceKey<EquipmentAsset>, EquipmentClientInfo> output){

        //Generates the JSON file to point to the humanoid;baby;leggings textures
        output.accept(ModArmorMaterial.TEST_KEY, EquipmentClientInfo.builder()
                        .addHumanoidLayers(Identifier.fromNamespaceAndPath(Testmod.MODID, "test"),false)

                .build());

}


    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        Map<ResourceKey<EquipmentAsset>, EquipmentClientInfo> equipmentAssets = new HashMap<>();
        bootstrap((id, asset) -> {
            if (equipmentAssets.putIfAbsent(id, asset) != null) {
                throw new IllegalStateException("Tried to register equipment asset twice for id: " + id);
            }
        });
        return DataProvider.saveAll(cache, EquipmentClientInfo.CODEC, this.pathProvider::json, equipmentAssets);
    }


    @Override
    public String getName() {
        return "Testmod Equipment Definitions";
    }
}
