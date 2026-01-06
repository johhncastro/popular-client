package com.pop.pvp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraftforge.fml.common.FMLLog;

import java.io.*;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final File configFile;
    private ModConfig config;
    
    public ConfigManager(File configDir) {
        this.configFile = new File(configDir, "popular.json");
        this.config = new ModConfig(); // Default config
    }
    
    public void loadConfig() {
        if (configFile.exists()) {
            InputStreamReader reader = null;
            try {
                reader = new InputStreamReader(new FileInputStream(configFile), "UTF-8");
                config = GSON.fromJson(reader, ModConfig.class);
                if (config == null) {
                    config = new ModConfig();
                }
            } catch (IOException e) {
                FMLLog.warning("[Popular] Failed to load config, using defaults: " + e.getMessage());
                config = new ModConfig();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        // Ignore
                    }
                }
            }
        }
        saveConfig(); // Save to ensure all fields are present
    }
    
    public void saveConfig() {
        // Ensure parent directory exists
        configFile.getParentFile().mkdirs();
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(configFile), "UTF-8");
            GSON.toJson(config, writer);
        } catch (IOException e) {
            FMLLog.severe("[Popular] Failed to save config: " + e.getMessage());
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
    }
    
    public ModConfig getConfig() {
        return config;
    }
    
    public static class ModConfig {
        public boolean autoClickerEnabled = false;
        public double autoClickerMinCPS = 18.1; // Minimum CPS
        public double autoClickerMaxCPS = 22.14; // Maximum CPS
        public boolean sprintEnabled = false; // Sprint mod enabled
        public boolean chestESPEnabled = false; // Chest ESP mod enabled
        // Chest ESP color settings
        public int chestESPRed = 100;   // 0-255
        public int chestESPGreen = 150; // 0-255
        public int chestESPBlue = 255;  // 0-255 (cyan/blue default)
        public int toggleKey = 46; // C key by default
        public boolean showVersion = false; // Show mod name/version in HUD
        // UI Theme settings
        public int accentColorRed = 100;   // 0-255
        public int accentColorGreen = 150; // 0-255
        public int accentColorBlue = 255;  // 0-255 (cyan/blue accent)
        // Window positions and sizes (for layout persistence)
        public WindowLayout combatWindowLayout = null;
        public WindowLayout renderWindowLayout = null;
        public WindowLayout movementWindowLayout = null;
        public WindowLayout clientWindowLayout = null;
    }
    
    public static class WindowPosition {
        public int x;
        public int y;
        
        public WindowPosition() {
            // Default constructor for Gson
        }
        
        public WindowPosition(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
    
    public static class WindowLayout {
        public int x;
        public int y;
        public int width;
        public int height;
        
        public WindowLayout() {
            // Default constructor for Gson
        }
        
        public WindowLayout(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }
    
    /**
     * Gets the accent color as an RGB integer.
     */
    public int getAccentColor() {
        ModConfig config = getConfig();
        return (config.accentColorRed << 16) | (config.accentColorGreen << 8) | config.accentColorBlue;
    }
    
    /**
     * Gets the chest ESP color as an RGB integer.
     */
    public int getChestESPColor() {
        ModConfig config = getConfig();
        return (config.chestESPRed << 16) | (config.chestESPGreen << 8) | config.chestESPBlue;
    }
}

