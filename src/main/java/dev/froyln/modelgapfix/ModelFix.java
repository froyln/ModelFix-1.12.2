package dev.froyln.modelgapfix;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.EnumMap;

import net.minecraft.client.renderer.block.model.BlockPart;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

import org.lwjgl.util.vector.Vector3f;

/**
 * The model-gap algorithm, ported from MehVahdJukaar's Model Gap Fix
 * (<a href="https://github.com/MehVahdJukaar/modelfix-multi">modelfix-multi</a>, GPL-3.0).
 * <p>This helper lives outside the mixin package so Mixin's class loader can load it directly.
 */
public final class ModelFix {
    public static final double EXPANSION = 0.02d;
    public static final double INDENT = 0.0001d;

    private ModelFix() {
    }

    public static List<BlockPart> generateSideParts(TextureAtlasSprite sprite, String texture, int tint) {
        int width = sprite.getIconWidth();
        int height = sprite.getIconHeight();
        List<BlockPart> parts = new ArrayList<BlockPart>();
        int[][] frames = new int[sprite.getFrameCount()][];
        for (int frame = 0; frame < frames.length; frame++) {
            frames[frame] = sprite.getFrameTextureData(frame)[0];
        }
        for (EnumFacing face : new EnumFacing[] {EnumFacing.UP, EnumFacing.DOWN,
                                                 EnumFacing.EAST, EnumFacing.WEST}) {
            boolean horizontal = face == EnumFacing.UP || face == EnumFacing.DOWN;
            int anchors = horizontal ? height : width;
            int length = horizontal ? width : height;
            int dx = face == EnumFacing.EAST ? -1 : face == EnumFacing.WEST ? 1 : 0;
            int dy = face == EnumFacing.UP ? -1 : face == EnumFacing.DOWN ? 1 : 0;
            for (int anchor = 0; anchor < anchors; anchor++) {
                int start = -1;
                for (int pos = 0; pos <= length; pos++) {
                    int x = horizontal ? pos : anchor;
                    int y = horizontal ? anchor : pos;
                    boolean edge = false;
                    if (pos < length) {
                        // Union boundary positions before building runs, so animation
                        // frames with different run lengths cannot create overlapping faces.
                        for (int[] pixels : frames) {
                            if (opaque(pixels, width, height, x, y)
                                && !opaque(pixels, width, height, x + dx, y + dy)) {
                                edge = true;
                                break;
                            }
                        }
                    }
                    if (edge && start < 0) start = pos;
                    if (!edge && start >= 0) {
                        parts.add(sidePart(face, anchor, start, pos,
                                           width, height, texture, tint));
                        start = -1;
                    }
                }
            }
        }
        enlargeFaces(parts);
        return parts;
    }

    private static boolean opaque(int[] pixels, int width, int height, int x, int y) {
        return x >= 0 && y >= 0 && x < width && y < height
            && (pixels[y * width + x] >>> 24) != 0;
    }

    private static BlockPart sidePart(EnumFacing face, int anchor, int start, int end,
                                      int width, int height, String texture, int tint) {
        float sx = 16.0f / width;
        float sy = 16.0f / height;
        // Sample inside the opaque texel. Vanilla's anchor/(size-1) lands on texel
        // boundaries; expanding a face then exposes transparent neighbours.
        float inset = 0.0001f;
        boolean horizontal = face == EnumFacing.UP || face == EnumFacing.DOWN;
        float[] uv;
        Vector3f from;
        Vector3f to;
        if (horizontal) {
            float y = 16.0f - (anchor + (face == EnumFacing.DOWN ? 1 : 0)) * sy;
            from = new Vector3f(start * sx, y, 7.5f);
            to = new Vector3f(end * sx, y, 8.5f);
            float v = (anchor + 0.5f) * sy;
            uv = new float[] {(start + inset) * sx, v, (end - inset) * sx, v};
        } else {
            float x = (anchor + (face == EnumFacing.WEST ? 1 : 0)) * sx;
            from = new Vector3f(x, 16.0f - start * sy, 7.5f);
            to = new Vector3f(x, 16.0f - end * sy, 8.5f);
            float u = (anchor + 0.5f) * sx;
            uv = new float[] {u, (end - inset) * sy, u, (start + inset) * sy};
        }
        Map<EnumFacing, BlockPartFace> faces = new EnumMap<EnumFacing, BlockPartFace>(EnumFacing.class);
        faces.put(face, new BlockPartFace(null, tint, texture, new BlockFaceUV(uv, 0)));
        return new BlockPart(from, to, faces, null, true);
    }

    public static void enlargeFaces(List<BlockPart> parts) {
        float expand = (float) Config.quadExpansion();
        float indent = (float) Config.quadIndent();
        if (expand == 0.0f && indent == 0.0f) {
            return;
        }
        for (BlockPart part : parts) {
            Map<EnumFacing, ?> faces = part.mapFaces;
            if (faces.size() != 1) {
                continue;
            }
            Vector3f from = part.positionFrom;
            Vector3f to = part.positionTo;
            switch (faces.keySet().iterator().next()) {
                case UP:
                    from.set(from.x - expand, from.y - indent, from.z - expand);
                    to.set(to.x + expand, to.y - indent, to.z + expand);
                    break;
                case DOWN:
                    from.set(from.x - expand, from.y + indent, from.z - expand);
                    to.set(to.x + expand, to.y + indent, to.z + expand);
                    break;
                case WEST:
                    from.set(from.x - indent, from.y + expand, from.z - expand);
                    to.set(to.x - indent, to.y - expand, to.z + expand);
                    break;
                case EAST:
                    from.set(from.x + indent, from.y + expand, from.z - expand);
                    to.set(to.x + indent, to.y - expand, to.z + expand);
                    break;
                default:
                    break;
            }
        }
    }
}
