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

    private String motd = "Welcome to the server!";

    public String getMotd() {
        return motd;
    }

    public void setMotd(String motd) {
        this.motd = motd;
    }

    public void loadConfig(File configDir) {
        File configFolder = new File(configDir, "config");
        if (!configFolder.exists()) {
            configFolder.mkdirs();
        }

        File configFile = new File(configFolder, CONFIG_FILE_NAME);
        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                MotdConfig config = GSON.fromJson(reader, MotdConfig.class);
                this.motd = config.motd;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            saveConfig(configDir);
        }
    }

    public void saveConfig(File configDir) {
        File configFolder = new File(configDir, "config");
        if (!configFolder.exists()) {
            configFolder.mkdirs();
        }

        File configFile = new File(configFolder, CONFIG_FILE_NAME);
        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

