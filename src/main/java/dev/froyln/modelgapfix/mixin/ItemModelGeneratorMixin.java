package dev.froyln.modelgapfix.mixin;

import java.util.List;

import dev.froyln.modelgapfix.ModelFix;
import dev.froyln.modelgapfix.Config;
import net.minecraft.client.renderer.block.model.BlockPart;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Expands the generated quads of flat item models to close their hairline seams. */
@Mixin(ItemModelGenerator.class)
public abstract class ItemModelGeneratorMixin {
    /**
     * The descriptor is required because ItemModelGenerator has two getBlockParts overloads.
     */
    @Inject(
        method = "getBlockParts(Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;Ljava/lang/String;I)Ljava/util/List;",
        at = @At("RETURN"), cancellable = true)
    private void modelgapfix$enlargeFaces(TextureAtlasSprite sprite, String texture, int tintIndex,
                                          CallbackInfoReturnable<List<BlockPart>> cir) {
        if (Config.enabled()) {
            cir.setReturnValue(ModelFix.generateSideParts(sprite, texture, tintIndex));
        }
    }
}
