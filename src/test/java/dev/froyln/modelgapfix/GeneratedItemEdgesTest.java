package dev.froyln.modelgapfix;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.block.model.BlockPart;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import org.junit.Test;
import static org.junit.Assert.*;

public class GeneratedItemEdgesTest {
    private static class Sprite extends TextureAtlasSprite {
        Sprite(int width, int height, int[]... pixels) {
            super("test");
            setIconWidth(width);
            setIconHeight(height);
            List<int[][]> frames = new ArrayList<int[][]>();
            for (int[] frame : pixels) frames.add(new int[][] {frame});
            setFramesTextureData(frames);
        }
    }

    @Test public void transparentPixelsSplitRunsAndSidesSampleOpaquePixelCenters() {
        // Two isolated pixels: vanilla joins their top/bottom faces across the transparent gap.
        List<BlockPart> parts = ModelFix.generateSideParts(
            new Sprite(3, 1, new int[] {-1, 0, -1}), "layer0", 2);
        assertEquals(8, parts.size());
        for (BlockPart part : parts) {
            EnumFacing facing = part.mapFaces.keySet().iterator().next();
            BlockPartFace face = part.mapFaces.get(facing);
            assertEquals(2, face.tintIndex);
            assertEquals("layer0", face.texture);
            float[] uv = face.blockFaceUV.uvs;
            for (float value : uv) assertTrue(Float.isFinite(value));
            if (facing == EnumFacing.UP || facing == EnumFacing.DOWN) {
                assertEquals(8.0f, uv[1], 0.0001f);
                assertEquals(8.0f, uv[3], 0.0001f);
                assertTrue(Math.abs(uv[2] - uv[0]) < 16.0f / 3.0f);
            } else {
                assertEquals(uv[0], uv[2], 0.0f);
                assertTrue(uv[0] < 16.0f / 3.0f || uv[0] > 32.0f / 3.0f);
            }
        }
    }

    @Test public void animationFramesContributeTheirOwnContiguousEdges() {
        List<BlockPart> parts = ModelFix.generateSideParts(
            new Sprite(2, 1, new int[] {-1, 0}, new int[] {0, -1}), "layer0", 0);
        // Retain internal boundaries from each frame; coalesce shared top/bottom runs.
        assertEquals(6, parts.size());
    }

    @Test public void animationWithChangingRunLengthsDoesNotCreateOverlappingFaces() {
        List<BlockPart> parts = ModelFix.generateSideParts(
            new Sprite(3, 1, new int[] {-1, -1, 0}, new int[] {-1, -1, -1}), "layer0", 0);
        assertEquals(5, parts.size());
    }

    @Test public void solidSpriteMergesEdgesAndKeepsFrontAndBackPlanes() {
        List<BlockPart> parts = ModelFix.generateSideParts(
            new Sprite(2, 2, new int[] {-1, -1, -1, -1}), "layer0", 0);
        assertEquals(4, parts.size());
        for (BlockPart part : parts) {
            assertEquals(7.5f - (float) ModelFix.EXPANSION, part.positionFrom.z, 0.0001f);
            assertEquals(8.5f + (float) ModelFix.EXPANSION, part.positionTo.z, 0.0001f);
        }
    }
}
