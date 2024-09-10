package uk.co.suskins;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MotdConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String CONFIG_FILE_NAME = "motd_config.json";

    private String motd = "New default!";

    public String getMotd() {
        return motd;
    }

    public void setMotd(String motd) {
        this.motd = motd;
    }

    public void loadConfig(File configDir) {
        File configFile = new File(configDir, CONFIG_FILE_NAME);
        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                MotdConfig config = GSON.fromJson(reader, MotdConfig.class);
                this.motd = config.motd;
            } catch (IOException e) {
                e.printStackTrace(); // Handle the error gracefully
            }
        } else {
            saveConfig(configDir); // Save default config if it doesn't exist
        }
    }

    public void saveConfig(File configDir) {
        File configFile = new File(configDir, CONFIG_FILE_NAME);
        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            e.printStackTrace(); // Handle the error gracefully
        }
    }
}
