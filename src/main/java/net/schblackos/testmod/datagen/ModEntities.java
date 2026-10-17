package net.schblackos.testmod.datagen;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.schblackos.testmod.Testmod;
import net.schblackos.testmod.entity.custom.TestArrowEntity;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.createEntities(Testmod.MODID);

    public static final ResourceKey<EntityType<?>> TEST_ARROW_KEY = ResourceKey.create(Registries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(Testmod.MODID, "test_arrow"));

    public static final Supplier<EntityType<TestArrowEntity>> TEST_ARROW = ENTITY_TYPES.register("test_arrow",
            () -> EntityType.Builder.<TestArrowEntity>of(TestArrowEntity::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .build(TEST_ARROW_KEY));



    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
