package com.pop.pvp.ui;

/**
 * Animation system for smooth UI transitions.
 * Handles open/close animations and hover effects using easing functions.
 */
public class Animation {
    private float currentValue;
    private float targetValue;
    private float speed;
    private boolean animating;
    
    public Animation(float initialValue, float speed) {
        this.currentValue = initialValue;
        this.targetValue = initialValue;
        this.speed = speed;
        this.animating = false;
    }
    
    /**
     * Updates animation based on partial ticks.
     * Called every frame to smoothly interpolate values.
     * 
     * @param partialTicks Frame interpolation value (0.0-1.0)
     */
    public void update(float partialTicks) {
        if (animating) {
            float delta = targetValue - currentValue;
            if (Math.abs(delta) < 0.001F) {
                currentValue = targetValue;
                animating = false;
            } else {
                // Smooth interpolation using easing
                float easedDelta = UIUtils.easeOutCubic(Math.min(1.0F, Math.abs(delta) * speed * partialTicks));
                currentValue += delta * easedDelta;
            }
        }
    }
    
    /**
     * Sets target value and starts animation.
     */
    public void animateTo(float target) {
        this.targetValue = target;
        this.animating = true;
    }
    
    /**
     * Instantly sets value without animation.
     */
    public void setValue(float value) {
        this.currentValue = value;
        this.targetValue = value;
        this.animating = false;
    }
    
    public float getValue() {
        return currentValue;
    }
    
    public float getTarget() {
        return targetValue;
    }
    
    public boolean isAnimating() {
        return animating;
    }
    
    public void setSpeed(float speed) {
        this.speed = speed;
    }
}

