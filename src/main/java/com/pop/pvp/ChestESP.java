package com.pop.pvp;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

/**
 * Chest ESP mod that highlights chests through walls.
 */
public class ChestESP {
    private final ConfigManager configManager;
    
    public ChestESP(ConfigManager configManager) {
        this.configManager = configManager;
    }
    
    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (!configManager.getConfig().chestESPEnabled) {
            return;
        }
        
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.theWorld == null || mc.thePlayer == null) {
            return;
        }
        
        // Get player position
        double playerX = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * event.partialTicks;
        double playerY = mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * event.partialTicks;
        double playerZ = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * event.partialTicks;
        
        // Get chest ESP color from config
        int espColor = configManager.getChestESPColor();
        float red = ((espColor >> 16) & 255) / 255.0F;
        float green = ((espColor >> 8) & 255) / 255.0F;
        float blue = (espColor & 255) / 255.0F;
        
        // Setup OpenGL
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        
        // Optimized: Use TileEntity list instead of checking every block
        // This is MUCH more efficient - only iterates through existing tile entities
        // Instead of checking 2+ million blocks, we only check loaded chests
        int renderRange = 64;
        double renderRangeSq = renderRange * renderRange;
        
        // Get all tile entities and filter for chests
        for (Object obj : mc.theWorld.loadedTileEntityList) {
            if (obj instanceof TileEntityChest) {
                TileEntityChest chest = (TileEntityChest) obj;
                BlockPos pos = chest.getPos();
                
                // Check distance (squared for efficiency - no sqrt needed)
                double dx = pos.getX() - playerX;
                double dy = pos.getY() - playerY;
                double dz = pos.getZ() - playerZ;
                double distSq = dx * dx + dy * dy + dz * dz;
                
                if (distSq <= renderRangeSq) {
                    // Draw box around chest
                    drawBox(pos, playerX, playerY, playerZ, red, green, blue, 0.3F);
                }
            }
        }
        
        // Restore OpenGL state
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableLighting();
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }
    
    private void drawBox(BlockPos pos, double playerX, double playerY, double playerZ, 
                        float red, float green, float blue, float alpha) {
        double x = pos.getX() - playerX;
        double y = pos.getY() - playerY;
        double z = pos.getZ() - playerZ;
        
        // Chest bounding box (slightly larger than block for visibility)
        double minX = x - 0.01;
        double minY = y - 0.01;
        double minZ = z - 0.01;
        double maxX = x + 1.01;
        double maxY = y + 1.01;
        double maxZ = z + 1.01;
        
        GlStateManager.color(red, green, blue, alpha);
        
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer renderer = tessellator.getWorldRenderer();
        renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        
        // Bottom face
        renderer.pos(minX, minY, minZ).endVertex();
        renderer.pos(maxX, minY, minZ).endVertex();
        renderer.pos(maxX, minY, maxZ).endVertex();
        renderer.pos(minX, minY, maxZ).endVertex();
        
        // Top face
        renderer.pos(minX, maxY, minZ).endVertex();
        renderer.pos(minX, maxY, maxZ).endVertex();
        renderer.pos(maxX, maxY, maxZ).endVertex();
        renderer.pos(maxX, maxY, minZ).endVertex();
        
        // North face
        renderer.pos(minX, minY, minZ).endVertex();
        renderer.pos(minX, maxY, minZ).endVertex();
        renderer.pos(maxX, maxY, minZ).endVertex();
        renderer.pos(maxX, minY, minZ).endVertex();
        
        // South face
        renderer.pos(minX, minY, maxZ).endVertex();
        renderer.pos(maxX, minY, maxZ).endVertex();
        renderer.pos(maxX, maxY, maxZ).endVertex();
        renderer.pos(minX, maxY, maxZ).endVertex();
        
        // West face
        renderer.pos(minX, minY, minZ).endVertex();
        renderer.pos(minX, minY, maxZ).endVertex();
        renderer.pos(minX, maxY, maxZ).endVertex();
        renderer.pos(minX, maxY, minZ).endVertex();
        
        // East face
        renderer.pos(maxX, minY, minZ).endVertex();
        renderer.pos(maxX, maxY, minZ).endVertex();
        renderer.pos(maxX, maxY, maxZ).endVertex();
        renderer.pos(maxX, minY, maxZ).endVertex();
        
        tessellator.draw();
    }
}

