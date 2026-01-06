package com.pop.pvp.ui;

/**
 * Base class for all UI components.
 * Provides common functionality like position, size, and hover detection.
 * 
 * Why: Modular design allows reusable components and consistent behavior.
 */
public abstract class Component {
    protected int x, y;
    protected int width, height;
    protected boolean visible = true;
    protected boolean hovered = false;
    protected Animation hoverAnimation;
    
    public Component(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.hoverAnimation = new Animation(0.0F, 8.0F);
    }
    
    /**
     * Updates component state (animations, hover detection).
     * Called every frame before rendering.
     */
    public void update(int mouseX, int mouseY, float partialTicks) {
        if (!visible) return;
        
        // Update hover state with smooth interpolation
        boolean wasHovered = hovered;
        hovered = isMouseOver(mouseX, mouseY);
        
        if (hovered != wasHovered) {
            hoverAnimation.animateTo(hovered ? 1.0F : 0.0F);
        }
        
        hoverAnimation.update(partialTicks);
    }
    
    /**
     * Renders the component.
     * Must be implemented by subclasses.
     */
    public abstract void render(int mouseX, int mouseY, float partialTicks);
    
    /**
     * Handles mouse click events.
     * Returns true if click was handled.
     */
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        return false;
    }
    
    /**
     * Handles mouse release events.
     */
    public void mouseReleased(int mouseX, int mouseY, int state) {
    }
    
    /**
     * Checks if mouse is over this component.
     */
    public boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
    
    // Getters and setters
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public boolean isVisible() { return visible; }
    public boolean isHovered() { return hovered; }
    
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    /**
     * Gets hover interpolation value (0.0 = not hovered, 1.0 = fully hovered).
     * Used for smooth hover effects.
     */
    public float getHoverProgress() {
        return hoverAnimation.getValue();
    }
}

