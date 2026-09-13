package com.itskillmaster.plhideplus.update;

import com.itskillmaster.plhideplus.PlHidePlus;
import com.itskillmaster.plhideplus.config.ConfigManager;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateChecker {

    private final PlHidePlus plugin;
    private final ConfigManager configManager;
    private String latestVersion;
    private boolean updateAvailable;
    private boolean checked;

    public UpdateChecker(PlHidePlus plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.latestVersion = plugin.getDescription().getVersion();
        this.updateAvailable = false;
        this.checked = false;
    }

    public void checkForUpdates() {
        if (!configManager.isUpdateCheckerEnabled()) return;

        String repository = configManager.getUpdateRepository();
        String apiUrl = "https://api.github.com/repos/" + repository + "/releases/latest";

        CompletableFuture.supplyAsync(() -> {
            try {
                return fetchLatestVersion(apiUrl);
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to check for updates: " + e.getMessage());
                return null;
            }
        }).thenAccept(version -> {
            if (version != null) {
                this.latestVersion = version;
                this.updateAvailable = isNewerVersion(version, plugin.getDescription().getVersion());
                this.checked = true;

                if (updateAvailable) {
                    plugin.log("&e[PlHidePlus] Update available: &f" + version + " &e(current: &f" + plugin.getDescription().getVersion() + "&e)");
                } else {
                    plugin.log("&a[PlHidePlus] You are running the latest version (&f" + version + "&a)");
                }
            }
        });
    }

    private String fetchLatestVersion(String apiUrl) throws Exception {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/vnd.github.v3+json");
        connection.setRequestProperty("User-Agent", "PlHidePlus-UpdateChecker");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("GitHub API returned: " + responseCode);
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }

        String json = response.toString();
        Pattern pattern = Pattern.compile("\"tag_name\"\\s*:\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(json);

        if (matcher.find()) {
            String tag = matcher.group(1);
            return tag.startsWith("v") ? tag.substring(1) : tag;
        }

        pattern = Pattern.compile("\"name\"\\s*:\\s*\"([^\"]+)\"");
        matcher = pattern.matcher(json);

        if (matcher.find()) {
            String name = matcher.group(1);
            Pattern versionPattern = Pattern.compile("(\\d+\\.\\d+\\.\\d+)");
            Matcher versionMatcher = versionPattern.matcher(name);
            if (versionMatcher.find()) {
                return versionMatcher.group(1);
            }
        }

        return null;
    }

    private boolean isNewerVersion(String latest, String current) {
        try {
            String[] latestParts = latest.split("[.-]");
            String[] currentParts = current.split("[.-]");

            int maxLen = Math.max(latestParts.length, currentParts.length);
            for (int i = 0; i < maxLen; i++) {
                int latestNum = i < latestParts.length ? parseVersionPart(latestParts[i]) : 0;
                int currentNum = i < currentParts.length ? parseVersionPart(currentParts[i]) : 0;

                if (latestNum > currentNum) return true;
                if (latestNum < currentNum) return false;
            }
            return false;
        } catch (Exception e) {
            return !latest.equals(current);
        }
    }

    private int parseVersionPart(String part) {
        try {
            return Integer.parseInt(part.replaceAll("\\D", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public boolean isUpdateAvailable() {
        return updateAvailable;
    }

    public boolean isChecked() {
        return checked;
    }
}