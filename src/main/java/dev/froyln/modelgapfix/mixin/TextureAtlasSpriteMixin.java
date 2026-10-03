package dev.froyln.modelgapfix.mixin;

import dev.froyln.modelgapfix.Config;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

/** Replaces 1.12.2's hardcoded 0.01-texel atlas inset with the configured value. */
@Mixin(TextureAtlasSprite.class)
public abstract class TextureAtlasSpriteMixin {
    @Shadow protected int originX;
    @Shadow protected int originY;
    @Shadow protected int width;
    @Shadow protected int height;
    @Shadow protected boolean rotated;
    @Shadow private float minU;
    @Shadow private float maxU;
    @Shadow private float minV;
    @Shadow private float maxV;

    /** @author MehVahdJukaar @reason remove the 0.01-texel model seam */
    @Overwrite
    public void initSprite(int atlasWidth, int atlasHeight, int x, int y, boolean rotated) {
        this.originX = x;
        this.originY = y;
        this.rotated = rotated;
        float insetU = Config.uvInset() / atlasWidth;
        float insetV = Config.uvInset() / atlasHeight;
        this.minU = (float) x / atlasWidth + insetU;
        this.maxU = (float) (x + this.width) / atlasWidth - insetU;
        this.minV = (float) y / atlasHeight + insetV;
        this.maxV = (float) (y + this.height) / atlasHeight - insetV;
    }
}
