package com.pop.pvp.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

/**
 * Slider component for numeric input.
 * Allows users to adjust values within a range.
 */
public class Slider extends Component {
    private String label;
    private double value;
    private double minValue;
    private double maxValue;
    private Runnable onChange;
    private int accentColor;
    private boolean dragging = false;
    
    private static final int HEIGHT = 16; // Increased height for better visibility
    private static final int SLIDER_WIDTH = 100;
    private static final int SLIDER_HEIGHT = 6; // Thicker track for visibility
    private static final int HANDLE_SIZE = 10; // Larger handle
    
    public Slider(int x, int y, int width, String label, double initialValue, double minValue, double maxValue, Runnable onChange, int accentColor) {
        super(x, y, width, HEIGHT);
        this.label = label;
        this.value = initialValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.onChange = onChange;
        this.accentColor = accentColor;
    }
    
    @Override
    public void update(int mouseX, int mouseY, float partialTicks) {
        super.update(mouseX, mouseY, partialTicks);
        
        if (dragging) {
            FontRenderer font = Minecraft.getMinecraft().fontRendererObj;
            int actualSliderWidth = 80; // Fixed width for alignment
            int sliderStartX = x + 60; // Fixed position - matches render
            int sliderX = sliderStartX;
            
            double normalizedX = (double)(mouseX - sliderX) / actualSliderWidth;
            normalizedX = Math.max(0.0, Math.min(1.0, normalizedX));
            value = minValue + (maxValue - minValue) * normalizedX;
            if (onChange != null) {
                onChange.run();
            }
        }
    }
    
    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (!visible) return;
        
        FontRenderer font = Minecraft.getMinecraft().fontRendererObj;
        
        // Calculate slider position - align all sliders to start at the same X position
        int labelWidth = font.getStringWidth(label);
        int valueTextWidth = font.getStringWidth(String.format("%.1f", maxValue)); // Use max value for width calculation
        
        // Fixed slider width for alignment - use consistent width across all sliders
        int actualSliderWidth = 80; // Fixed width for alignment
        
        // Fixed starting X position for all sliders (after longest label + spacing)
        // Use a consistent offset from the left to align all sliders vertically
        int sliderStartX = x + 60; // Fixed position - all sliders start here
        
        // Draw label
        font.drawString(label, x, y, 0xFFFFFF);
        
        // Calculate slider X position (right-aligned, but track starts at fixed position)
        int sliderX = sliderStartX;
        int sliderY = y + (height - SLIDER_HEIGHT) / 2;
        
        // Calculate normalized value first
        double normalizedValue = (value - minValue) / (maxValue - minValue);
        normalizedValue = Math.max(0.0, Math.min(1.0, normalizedValue)); // Clamp
        int filledWidth = (int)(actualSliderWidth * normalizedValue);
        
        // Draw slider track background (VERY light grey, fully opaque and visible)
        // Use a much brighter color to ensure visibility - make it almost white
        int trackColor = UIUtils.color(255, 200, 200, 200); // Very light grey, almost white, fully opaque
        
        // Use simple rectangle first to ensure it's visible (no rounded corners for track)
        UIUtils.drawRect(sliderX, sliderY, sliderX + actualSliderWidth, sliderY + SLIDER_HEIGHT, trackColor);
        
        // Draw filled portion (accent color - very visible) ON TOP of track
        if (filledWidth > 1) { // Make sure it's at least 2px wide
            int filledColor = UIUtils.color(255,
                (accentColor >> 16) & 255,
                (accentColor >> 8) & 255,
                accentColor & 255);
            // Draw filled portion using simple rectangle for visibility
            UIUtils.drawRect(sliderX, sliderY, sliderX + filledWidth, sliderY + SLIDER_HEIGHT, filledColor);
        }
        
        // Draw handle (bright white circle with border for visibility)
        int handleX = sliderX + (int)(actualSliderWidth * normalizedValue) - HANDLE_SIZE / 2;
        int handleY = y + (height - HANDLE_SIZE) / 2;
        boolean handleHovered = mouseX >= handleX && mouseX <= handleX + HANDLE_SIZE &&
                                mouseY >= handleY && mouseY <= handleY + HANDLE_SIZE;
        
        // Draw handle shadow/outline first
        int handleOutlineColor = UIUtils.color(255, 100, 100, 100);
        UIUtils.drawRoundedRect(handleX - 1, handleY - 1, HANDLE_SIZE + 2, HANDLE_SIZE + 2, (HANDLE_SIZE + 2) / 2, handleOutlineColor);
        
        // Draw handle (bright white)
        int handleColor = handleHovered || dragging ?
            UIUtils.color(255, 255, 255, 255) :
            UIUtils.color(255, 240, 240, 240);
        UIUtils.drawRoundedRect(handleX, handleY, HANDLE_SIZE, HANDLE_SIZE, HANDLE_SIZE / 2, handleColor);
        
        // Draw value text (right-aligned, after slider)
        String valueTextStr = String.format("%.1f", value);
        int textX = sliderX + actualSliderWidth + 4;
        font.drawString(valueTextStr, textX, y, 0xAAAAAA);
    }
    
    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (!visible) return false;
        
        FontRenderer font = Minecraft.getMinecraft().fontRendererObj;
        int actualSliderWidth = 80; // Fixed width for alignment
        int sliderStartX = x + 60; // Fixed position - matches render
        int sliderX = sliderStartX;
        
        if (mouseX >= sliderX && mouseX <= sliderX + actualSliderWidth &&
            mouseY >= y && mouseY <= y + height) {
            if (mouseButton == 0) {
                dragging = true;
                // Update value immediately
                double normalizedX = (double)(mouseX - sliderX) / actualSliderWidth;
                normalizedX = Math.max(0.0, Math.min(1.0, normalizedX));
                value = minValue + (maxValue - minValue) * normalizedX;
                if (onChange != null) {
                    onChange.run();
                }
                Minecraft.getMinecraft().thePlayer.playSound("random.click", 0.3F, 1.0F);
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        dragging = false;
    }
    
    public double getValue() {
        return value;
    }
    
    public void setValue(double value) {
        this.value = Math.max(minValue, Math.min(maxValue, value));
    }
}

