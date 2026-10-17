package net.schblackos.testmod.client.renderer;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.resources.Identifier;
import net.schblackos.testmod.Testmod;
import net.schblackos.testmod.entity.custom.TestArrowEntity;

public class TestArrowRenderer extends ArrowRenderer<TestArrowEntity, ArrowRenderState> {
    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(Testmod.MODID, "textures/entity/arrow/test_arrow_item.png");

    public TestArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected Identifier getTextureLocation(ArrowRenderState state) {
        return TEXTURE;
    }

    @Override
    public ArrowRenderState createRenderState() {
        return new ArrowRenderState();
    }
}
