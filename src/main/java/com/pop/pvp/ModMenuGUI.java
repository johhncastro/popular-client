package com.pop.pvp;

import com.pop.pvp.ui.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Modern mod menu GUI inspired by RusherHack and Meteor Client.
 * Features multiple category windows, clean navigation, and organized mod settings.
 * 
 * Design Philosophy:
 * - Category-based organization improves discoverability
 * - Multiple windows allow parallel access to different mod groups
 * - Clean navigation reduces cognitive load
 * - Scalable architecture supports unlimited mods
 */
public class ModMenuGUI extends GuiScreen {
    private final ConfigManager configManager;
    private Map<String, Window> windows;
    private Animation openAnimation;
    private int accentColor;
    
    public ModMenuGUI(ConfigManager configManager) {
        this.configManager = configManager;
        this.windows = new HashMap<String, Window>();
        this.openAnimation = new Animation(0.0F, 15.0F);
        this.accentColor = configManager.getAccentColor();
    }
    
    @Override
    public void initGui() {
        super.initGui();
        
        ScaledResolution scaled = new ScaledResolution(Minecraft.getMinecraft());
        int screenWidth = scaled.getScaledWidth();
        int screenHeight = scaled.getScaledHeight();
        
        // Create windows for each category (all visible at once)
        createWindows(screenWidth, screenHeight);
        
        // Load saved window positions
        loadWindowPositions();
        
        // Start open animation
        openAnimation.animateTo(1.0F);
    }
    
    private void createWindows(int screenWidth, int screenHeight) {
        windows.clear();
        
        // Default window positions (horizontal row, directly adjacent)
        int minWidth = 200; // Minimum window width (increased to fit slider text)
        int defaultY = 50;
        int windowSpacing = 0; // No spacing between windows
        
        // Calculate starting X to center the windows horizontally
        int windowCount = 4; // Combat, Render, Movement, Client
        int totalWidth = (minWidth + windowSpacing) * windowCount - windowSpacing; // Total width of all windows
        int defaultX = (screenWidth - totalWidth) / 2;
        
        // Combat Category Window
        Window combatWindow = createCombatWindow(defaultX, defaultY, screenWidth, screenHeight);
        windows.put("Combat", combatWindow);
        
        // Render Category Window (directly next to Combat)
        Window renderWindow = createRenderWindow(defaultX + minWidth + windowSpacing, defaultY, screenWidth, screenHeight);
        windows.put("Render", renderWindow);
        
        // Movement Category Window (directly next to Render)
        Window movementWindow = createMovementWindow(defaultX + (minWidth + windowSpacing) * 2, defaultY, screenWidth, screenHeight);
        windows.put("Movement", movementWindow);
        
        // Client Category Window (directly next to Movement)
        Window clientWindow = createClientWindow(defaultX + (minWidth + windowSpacing) * 3, defaultY, screenWidth, screenHeight);
        windows.put("Client", clientWindow);
    }
    
    private void loadWindowPositions() {
        ConfigManager.ModConfig config = configManager.getConfig();
        
        // Load Combat window layout
        if (config.combatWindowLayout != null) {
            Window window = windows.get("Combat");
            if (window != null) {
                window.setPosition(config.combatWindowLayout.x, config.combatWindowLayout.y);
                window.setSize(config.combatWindowLayout.width, config.combatWindowLayout.height);
            }
        }
        
        // Load Render window layout
        if (config.renderWindowLayout != null) {
            Window window = windows.get("Render");
            if (window != null) {
                window.setPosition(config.renderWindowLayout.x, config.renderWindowLayout.y);
                window.setSize(config.renderWindowLayout.width, config.renderWindowLayout.height);
            }
        }
        
        // Load Movement window layout
        if (config.movementWindowLayout != null) {
            Window window = windows.get("Movement");
            if (window != null) {
                window.setPosition(config.movementWindowLayout.x, config.movementWindowLayout.y);
                window.setSize(config.movementWindowLayout.width, config.movementWindowLayout.height);
            }
        }
        
        // Load Client window layout
        if (config.clientWindowLayout != null) {
            Window window = windows.get("Client");
            if (window != null) {
                window.setPosition(config.clientWindowLayout.x, config.clientWindowLayout.y);
                window.setSize(config.clientWindowLayout.width, config.clientWindowLayout.height);
            }
        }
    }
    
    private void saveWindowPositions() {
        ConfigManager.ModConfig config = configManager.getConfig();
        
        // Save Combat window layout
        Window combatWindow = windows.get("Combat");
        if (combatWindow != null) {
            config.combatWindowLayout = new ConfigManager.WindowLayout(
                combatWindow.getX(), combatWindow.getY(),
                combatWindow.getWidth(), combatWindow.getHeight());
        }
        
        // Save Render window layout
        Window renderWindow = windows.get("Render");
        if (renderWindow != null) {
            config.renderWindowLayout = new ConfigManager.WindowLayout(
                renderWindow.getX(), renderWindow.getY(),
                renderWindow.getWidth(), renderWindow.getHeight());
        }
        
        // Save Movement window layout
        Window movementWindow = windows.get("Movement");
        if (movementWindow != null) {
            config.movementWindowLayout = new ConfigManager.WindowLayout(
                movementWindow.getX(), movementWindow.getY(),
                movementWindow.getWidth(), movementWindow.getHeight());
        }
        
        // Save Client window layout
        Window clientWindow = windows.get("Client");
        if (clientWindow != null) {
            config.clientWindowLayout = new ConfigManager.WindowLayout(
                clientWindow.getX(), clientWindow.getY(),
                clientWindow.getWidth(), clientWindow.getHeight());
        }
        
        configManager.saveConfig();
    }
    
    /**
     * Resets all windows to their default positions and sizes.
     * Clears saved layouts from config.
     */
    private void resetWindowPositions() {
        ScaledResolution scaled = new ScaledResolution(Minecraft.getMinecraft());
        int screenWidth = scaled.getScaledWidth();
        int screenHeight = scaled.getScaledHeight();
        
        // Calculate default positions (same as createWindows - horizontal row)
        int minWidth = 200; // Increased to fit slider text
        int defaultY = 50;
        int windowSpacing = 0;
        int windowCount = 4; // Combat, Render, Movement, Client
        int totalWidth = (minWidth + windowSpacing) * windowCount - windowSpacing;
        int defaultX = (screenWidth - totalWidth) / 2;
        
        // Reset Combat window
        Window combatWindow = windows.get("Combat");
        if (combatWindow != null) {
            combatWindow.setPosition(defaultX, defaultY);
            combatWindow.setSize(minWidth, 150);
        }
        
        // Reset Render window
        Window renderWindow = windows.get("Render");
        if (renderWindow != null) {
            renderWindow.setPosition(defaultX + minWidth + windowSpacing, defaultY);
            renderWindow.setSize(minWidth, 150);
        }
        
        // Reset Movement window
        Window movementWindow = windows.get("Movement");
        if (movementWindow != null) {
            movementWindow.setPosition(defaultX + (minWidth + windowSpacing) * 2, defaultY);
            movementWindow.setSize(minWidth, 150);
        }
        
        // Reset Client window
        Window clientWindow = windows.get("Client");
        if (clientWindow != null) {
            clientWindow.setPosition(defaultX + (minWidth + windowSpacing) * 3, defaultY);
            clientWindow.setSize(minWidth, 150);
        }
        
        // Clear saved layouts from config
        ConfigManager.ModConfig config = configManager.getConfig();
        config.combatWindowLayout = null;
        config.renderWindowLayout = null;
        config.movementWindowLayout = null;
        config.clientWindowLayout = null;
        configManager.saveConfig();
        
        // Play sound feedback
        Minecraft.getMinecraft().thePlayer.playSound("random.click", 0.5F, 1.2F);
    }
    
    private Window createRenderWindow(int windowX, int windowY, int screenWidth, int screenHeight) {
        int windowWidth = 150; // Minimum width
        int windowHeight = 150;
        
        Window window = new Window(windowX, windowY, windowWidth, windowHeight, "Render", accentColor);
        
        // Placeholder for render mods
        // Example: ESP, Tracers, Nametags, etc.
        
        return window;
    }
    
    private Window createMovementWindow(int windowX, int windowY, int screenWidth, int screenHeight) {
        int windowWidth = 200; // Increased width to match other windows
        int windowHeight = 150;
        
        Window window = new Window(windowX, windowY, windowWidth, windowHeight, "Movement", accentColor);
        
        // Sprint toggle
        final ModToggle[] sprintRef = new ModToggle[1];
        sprintRef[0] = new ModToggle(
            0, 0, windowWidth - 16,
            "Sprint",
            configManager.getConfig().sprintEnabled,
            new Runnable() {
                @Override
                public void run() {
                    configManager.getConfig().sprintEnabled = !configManager.getConfig().sprintEnabled;
                    configManager.saveConfig();
                    sprintRef[0].setValue(configManager.getConfig().sprintEnabled);
                }
            },
            accentColor
        );
        window.addChild(sprintRef[0]);
        
        // Placeholder for other movement mods
        // Example: Speed, Flight, etc.
        
        return window;
    }
    
    private Window createCombatWindow(int windowX, int windowY, int screenWidth, int screenHeight) {
        int windowWidth = 200; // Increased width to fit slider text
        int windowHeight = 200; // Increased height for expandable settings
        
        Window window = new Window(windowX, windowY, windowWidth, windowHeight, "Combat", accentColor);
        
        // Auto-Clicker (expandable with settings)
        final ExpandableMod[] autoClickerRef = new ExpandableMod[1];
        autoClickerRef[0] = new ExpandableMod(
            0, 0, windowWidth - 16,
            "Auto-Clicker",
            configManager.getConfig().autoClickerEnabled,
            new Runnable() {
                @Override
                public void run() {
                    configManager.getConfig().autoClickerEnabled = !configManager.getConfig().autoClickerEnabled;
                    configManager.saveConfig();
                    autoClickerRef[0].setValue(configManager.getConfig().autoClickerEnabled);
                }
            },
            accentColor
        );
        
        // Min CPS Slider
        final Slider[] minCPSRef = new Slider[1];
        minCPSRef[0] = new Slider(
            0, 0, windowWidth - 16,
            "Min CPS",
            configManager.getConfig().autoClickerMinCPS,
            1.0, 30.0,
            new Runnable() {
                @Override
                public void run() {
                    configManager.getConfig().autoClickerMinCPS = minCPSRef[0].getValue();
                    // Ensure min doesn't exceed max
                    if (configManager.getConfig().autoClickerMinCPS > configManager.getConfig().autoClickerMaxCPS) {
                        configManager.getConfig().autoClickerMinCPS = configManager.getConfig().autoClickerMaxCPS;
                        minCPSRef[0].setValue(configManager.getConfig().autoClickerMinCPS);
                    }
                    configManager.saveConfig();
                }
            },
            accentColor
        );
        autoClickerRef[0].addSetting(minCPSRef[0]);
        
        // Max CPS Slider
        final Slider[] maxCPSRef = new Slider[1];
        maxCPSRef[0] = new Slider(
            0, 0, windowWidth - 16,
            "Max CPS",
            configManager.getConfig().autoClickerMaxCPS,
            1.0, 30.0,
            new Runnable() {
                @Override
                public void run() {
                    configManager.getConfig().autoClickerMaxCPS = maxCPSRef[0].getValue();
                    // Ensure max doesn't go below min
                    if (configManager.getConfig().autoClickerMaxCPS < configManager.getConfig().autoClickerMinCPS) {
                        configManager.getConfig().autoClickerMaxCPS = configManager.getConfig().autoClickerMinCPS;
                        maxCPSRef[0].setValue(configManager.getConfig().autoClickerMaxCPS);
                    }
                    configManager.saveConfig();
                }
            },
            accentColor
        );
        autoClickerRef[0].addSetting(maxCPSRef[0]);
        
        window.addChild(autoClickerRef[0]);
        
        // Placeholder for other combat mods
        // Example: KillAura, Criticals, etc.
        
        return window;
    }
    
    private Window createClientWindow(int windowX, int windowY, int screenWidth, int screenHeight) {
        int windowWidth = 150; // Minimum width
        int windowHeight = 150;
        
        Window window = new Window(windowX, windowY, windowWidth, windowHeight, "Client", accentColor);
        
        // Show Version toggle (simple highlighted text)
        final ModToggle[] showVersionRef = new ModToggle[1];
        showVersionRef[0] = new ModToggle(
            0, 0, windowWidth - 16,
            "Show Version",
            configManager.getConfig().showVersion,
            new Runnable() {
                @Override
                public void run() {
                    configManager.getConfig().showVersion = !configManager.getConfig().showVersion;
                    configManager.saveConfig();
                    showVersionRef[0].setValue(configManager.getConfig().showVersion);
                }
            },
            accentColor
        );
        window.addChild(showVersionRef[0]);
        
        // Reset Position toggle
        final ModToggle[] resetPositionRef = new ModToggle[1];
        resetPositionRef[0] = new ModToggle(
            0, 0, windowWidth - 16,
            "Reset Position",
            false, // This is not a persistent state, just a button
            new Runnable() {
                @Override
                public void run() {
                    resetWindowPositions();
                    // Reset the toggle state immediately (it's not a persistent setting)
                    resetPositionRef[0].setValue(false);
                }
            },
            accentColor
        );
        window.addChild(resetPositionRef[0]);
        
        return window;
    }
    
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // Update open animation
        openAnimation.update(partialTicks);
        
        float openProgress = openAnimation.getValue();
        if (openProgress < 0.01F) return;
        
        // Get scaled resolution once for reuse
        ScaledResolution scaled = new ScaledResolution(Minecraft.getMinecraft());
        
        // Draw very dark background overlay (Wurst/RusherHack style)
        int bgAlpha = (int)(openProgress * 200);
        int bgColor = UIUtils.color(bgAlpha, 0, 0, 0); // Black background
        UIUtils.drawRoundedRect(0, 0, scaled.getScaledWidth(), scaled.getScaledHeight(), 0, bgColor);
        
        // Draw mod name and version in top left (Wurst style)
        int topLeftX = 10;
        int topLeftY = 10;
        
        String modName = "Popular";
        String modVersion = "v" + PopPvPMod.VERSION;
        
        // Draw mod name with accent color (Wurst style)
        int nameColor = (int)(openProgress * 255) << 24 | 
            ((accentColor >> 16) & 255) << 16 | 
            ((accentColor >> 8) & 255) << 8 | 
            (accentColor & 255);
        fontRendererObj.drawString(modName, topLeftX, topLeftY, nameColor);
        
        // Draw version below name (lighter gray)
        int versionColor = (int)(openProgress * 200) << 24 | 0xBBBBBB;
        fontRendererObj.drawString(modVersion, topLeftX, topLeftY + 12, versionColor);
        
        // Render all windows (all visible at once)
        for (Window window : windows.values()) {
            window.update(mouseX, mouseY, partialTicks);
            window.render(mouseX, mouseY, partialTicks);
        }
        
        // Draw close hint (fades in)
        if (openProgress > 0.5F) {
            int centerX = scaled.getScaledWidth() / 2;
            int hintY = scaled.getScaledHeight() - 30;
            
            String hint = "Press ESC to close";
            int hintWidth = fontRendererObj.getStringWidth(hint);
            int hintColor = (int)(openProgress * 200) << 24 | 0xCCCCCC;
            
            fontRendererObj.drawString(hint, centerX - hintWidth / 2, hintY, hintColor);
        }
    }
    
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        // Handle window interactions (check all windows)
        for (Window window : windows.values()) {
            if (window.mouseClicked(mouseX, mouseY, mouseButton)) {
                return;
            }
        }
        
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
    
    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        // Save window positions when mouse is released (after dragging)
        saveWindowPositions();
        
        // Handle window interactions
        for (Window window : windows.values()) {
            window.mouseReleased(mouseX, mouseY, state);
        }
        
        super.mouseReleased(mouseX, mouseY, state);
    }
    
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) { // ESC key
            // Save window positions before closing
            saveWindowPositions();
            
            // Close immediately without waiting for animation
            this.mc.displayGuiScreen((GuiScreen)null);
            if (this.mc.currentScreen == null) {
                this.mc.setIngameFocus();
            }
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
