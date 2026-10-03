package net.minecraft.client.renderer.block.model;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import dev.froyln.modelgapfix.ModelFix;

import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.BlockPartRotation;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.util.EnumFacing;

import org.junit.Test;
import org.lwjgl.util.vector.Vector3f;

import static org.junit.Assert.assertEquals;

/**
 * Covers {@link ModelFix#enlargeFaces(List)}, the geometry half of the 2D item model fix. Each
 * face is grown in the two axes it spans and indented along its own normal, which is what closes
 * the sub-pixel seam left between adjacent quads.
 *
 * <p>The only interesting decision here is the single-face gate: elements with two or more faces
 * are skipped, because growing those distorts stacked elements.
 */
public class ModelFixEnlargeFacesTest {
    private static final float EXPANSION = (float) dev.froyln.modelgapfix.Config.quadExpansion();
    private static final float INDENT = (float) dev.froyln.modelgapfix.Config.quadIndent();

    private static BlockPart part(EnumFacing... facings) {
        Map<EnumFacing, BlockPartFace> faces = new EnumMap<EnumFacing, BlockPartFace>(EnumFacing.class);
        for (EnumFacing facing : facings) {
            faces.put(facing, new BlockPartFace(facing, 0, "minecraft:missing",
                new BlockFaceUV(new float[] {0, 0, 8, 0, 8, 8, 0, 8}, 8)));
        }
        return new BlockPart(new Vector3f(0, 1, 0), new Vector3f(1, 1, 1), faces,
            new BlockPartRotation(new Vector3f(), EnumFacing.Axis.X, 0.0F, false), false);
    }

    private static void assertCorner(BlockPart part, float fromX, float fromY, float fromZ,
                                     float toX, float toY, float toZ) {
        assertEquals(fromX, part.positionFrom.x, 1e-6F);
        assertEquals(fromY, part.positionFrom.y, 1e-6F);
        assertEquals(fromZ, part.positionFrom.z, 1e-6F);
        assertEquals(toX, part.positionTo.x, 1e-6F);
        assertEquals(toY, part.positionTo.y, 1e-6F);
        assertEquals(toZ, part.positionTo.z, 1e-6F);
    }

    @Test
    public void upFaceGrowsInXzAndIndentsInY() {
        BlockPart part = part(EnumFacing.UP);
        ModelFix.enlargeFaces(Arrays.<BlockPart>asList(part));
        assertCorner(part, -EXPANSION, 1.0F - INDENT, -EXPANSION,
            1.0F + EXPANSION, 1.0F - INDENT, 1.0F + EXPANSION);
    }

    @Test
    public void downFaceGrowsInXzAndIndentsInY() {
        BlockPart part = part(EnumFacing.DOWN);
        ModelFix.enlargeFaces(Arrays.<BlockPart>asList(part));
        assertCorner(part, -EXPANSION, 1.0F + INDENT, -EXPANSION,
            1.0F + EXPANSION, 1.0F + INDENT, 1.0F + EXPANSION);
    }

    @Test
    public void westFaceGrowsInYzAndIndentsInX() {
        BlockPart part = part(EnumFacing.WEST);
        ModelFix.enlargeFaces(Arrays.<BlockPart>asList(part));
        assertCorner(part, -INDENT, 1.0F + EXPANSION, -EXPANSION,
            1.0F - INDENT, 1.0F - EXPANSION, 1.0F + EXPANSION);
    }

    @Test
    public void eastFaceGrowsInYzAndIndentsInX() {
        BlockPart part = part(EnumFacing.EAST);
        ModelFix.enlargeFaces(Arrays.<BlockPart>asList(part));
        assertCorner(part, INDENT, 1.0F + EXPANSION, -EXPANSION,
            1.0F + INDENT, 1.0F - EXPANSION, 1.0F + EXPANSION);
    }

    @Test
    public void multiFaceElementIsLeftAlone() {
        BlockPart part = part(EnumFacing.UP, EnumFacing.DOWN);
        ModelFix.enlargeFaces(Arrays.<BlockPart>asList(part));
        assertCorner(part, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Test
    public void northAndSouthAreNotHandled() {
        BlockPart part = part(EnumFacing.NORTH);
        ModelFix.enlargeFaces(Arrays.<BlockPart>asList(part));
        assertCorner(part, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }
}
