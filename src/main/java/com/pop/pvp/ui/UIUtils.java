package com.pop.pvp.ui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

/**
 * Utility class for drawing UI elements with modern styling.
 * Provides rounded rectangles, shadows, and smooth rendering.
 */
public class UIUtils {
    
    /**
     * Draws a rounded rectangle with customizable corner radius.
     * Uses tessellation for smooth curves.
     * 
     * Why: Rounded corners create a softer, more modern appearance.
     * They reduce visual harshness and improve aesthetic appeal.
     * 
     * @param x Left position
     * @param y Top position
     * @param width Width of rectangle
     * @param height Height of rectangle
     * @param radius Corner radius in pixels
     * @param color ARGB color (0xAARRGGBB)
     */
    public static void drawRoundedRect(int x, int y, int width, int height, int radius, int color) {
        if (radius <= 0) {
            // Fallback to regular rectangle if no radius
            drawRect(x, y, x + width, y + height, color);
            return;
        }
        
        // Clamp radius to half the smallest dimension
        radius = Math.min(radius, Math.min(width, height) / 2);
        
        float alpha = (float)(color >> 24 & 255) / 255.0F;
        float red = (float)(color >> 16 & 255) / 255.0F;
        float green = (float)(color >> 8 & 255) / 255.0F;
        float blue = (float)(color & 255) / 255.0F;
        
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.color(red, green, blue, alpha);
        
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        
        int x1 = x;
        int y1 = y;
        int x2 = x + width;
        int y2 = y + height;
        
        // Draw main rectangle (center area without corners)
        if (width > radius * 2 && height > radius * 2) {
            addVertex(worldrenderer, x1 + radius, y1, 0);
            addVertex(worldrenderer, x2 - radius, y1, 0);
            addVertex(worldrenderer, x2 - radius, y2, 0);
            addVertex(worldrenderer, x1 + radius, y2, 0);
        }
        
        // Draw side rectangles
        if (height > radius * 2) {
            // Left side
            addVertex(worldrenderer, x1, y1 + radius, 0);
            addVertex(worldrenderer, x1 + radius, y1 + radius, 0);
            addVertex(worldrenderer, x1 + radius, y2 - radius, 0);
            addVertex(worldrenderer, x1, y2 - radius, 0);
            
            // Right side
            addVertex(worldrenderer, x2 - radius, y1 + radius, 0);
            addVertex(worldrenderer, x2, y1 + radius, 0);
            addVertex(worldrenderer, x2, y2 - radius, 0);
            addVertex(worldrenderer, x2 - radius, y2 - radius, 0);
        }
        
        // Draw corner arcs (simplified approach)
        int segments = 8;
        drawCornerArc(worldrenderer, x1 + radius, y1 + radius, radius, 180, 270, segments); // Top-left
        drawCornerArc(worldrenderer, x2 - radius, y1 + radius, radius, 270, 360, segments); // Top-right
        drawCornerArc(worldrenderer, x1 + radius, y2 - radius, radius, 90, 180, segments);  // Bottom-left
        drawCornerArc(worldrenderer, x2 - radius, y2 - radius, radius, 0, 90, segments);    // Bottom-right
        
        tessellator.draw();
        
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
    
    /**
     * Draws a corner arc (quarter circle) for rounded rectangles.
     */
    private static void drawCornerArc(WorldRenderer renderer, int centerX, int centerY, int radius, int startAngle, int endAngle, int segments) {
        for (int i = 0; i < segments; i++) {
            double angle1 = Math.toRadians(startAngle + (endAngle - startAngle) * i / segments);
            double angle2 = Math.toRadians(startAngle + (endAngle - startAngle) * (i + 1) / segments);
            
            double x1 = centerX + Math.cos(angle1) * radius;
            double y1 = centerY + Math.sin(angle1) * radius;
            double x2 = centerX + Math.cos(angle2) * radius;
            double y2 = centerY + Math.sin(angle2) * radius;
            
            addVertex(renderer, centerX, centerY, 0);
            addVertex(renderer, x1, y1, 0);
            addVertex(renderer, x2, y2, 0);
            addVertex(renderer, centerX, centerY, 0);
        }
    }
    
    private static void addVertex(WorldRenderer renderer, double x, double y, double z) {
        renderer.pos(x, y, z).endVertex();
    }
    
    /**
     * Draws a simple rectangle (fallback for non-rounded).
     */
    public static void drawRect(int left, int top, int right, int bottom, int color) {
        float alpha = (float)(color >> 24 & 255) / 255.0F;
        float red = (float)(color >> 16 & 255) / 255.0F;
        float green = (float)(color >> 8 & 255) / 255.0F;
        float blue = (float)(color & 255) / 255.0F;
        
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.color(red, green, blue, alpha);
        
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        worldrenderer.pos(left, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, top, 0.0D).endVertex();
        worldrenderer.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
    
    /**
     * Draws a shadow below a rounded rectangle.
     * Creates depth perception and improves visual hierarchy.
     * 
     * @param x Left position
     * @param y Top position
     * @param width Width
     * @param height Height
     * @param radius Corner radius
     * @param shadowSize Shadow blur size
     * @param opacity Shadow opacity (0.0-1.0)
     */
    public static void drawShadow(int x, int y, int width, int height, int radius, int shadowSize, float opacity) {
        // Draw multiple layers with decreasing opacity for smooth shadow
        for (int i = 0; i < shadowSize; i++) {
            float layerOpacity = opacity * (1.0F - (float)i / shadowSize) * 0.3F;
            int shadowColor = (int)(layerOpacity * 255) << 24; // Black with alpha
            
            drawRoundedRect(
                x - shadowSize + i,
                y - shadowSize + i,
                width + (shadowSize - i) * 2,
                height + (shadowSize - i) * 2,
                radius,
                shadowColor
            );
        }
    }
    
    /**
     * Easing function for smooth animations (ease-out cubic).
     * Provides natural deceleration for UI transitions.
     */
    public static float easeOutCubic(float t) {
        return 1.0F - (float)Math.pow(1.0 - t, 3);
    }
    
    /**
     * Easing function for smooth animations (ease-in-out cubic).
     * Smooth acceleration and deceleration.
     */
    public static float easeInOutCubic(float t) {
        return t < 0.5F ? 4.0F * t * t * t : 1.0F - (float)Math.pow(-2.0 * t + 2.0, 3) / 2.0F;
    }
    
    /**
     * Linear interpolation between two values.
     * Essential for smooth animations and hover effects.
     */
    public static float lerp(float start, float end, float t) {
        return start + (end - start) * t;
    }
    
    /**
     * Converts ARGB color components to integer.
     */
    public static int color(int alpha, int red, int green, int blue) {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }
}

