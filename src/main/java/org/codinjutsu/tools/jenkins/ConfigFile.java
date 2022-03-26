package org.codinjutsu.tools.jenkins;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ConfigFile {
    private static final String JENKINSS_INTEGRATION_CFG = "jenkinsIntegrations.cfg";
    private static String resourcesLocation = null;

    public static String getResourcesLocation() {

        if (isWindows()) {
            File windowsHome = new File(System.getenv("USERPROFILE"));
            File resourcesFolder = new File(windowsHome, ".jenkinsIntegrations");
            if (!resourcesFolder.exists()) {
                if (!resourcesFolder.mkdirs()) {
                    System.out.println("not create " + resourcesFolder.getName());
                }
            }
            resourcesLocation = resourcesFolder.getAbsolutePath();
            return resourcesLocation;
        }

        File userHomeDir = new File(getHome());
        File resourcesFolder = new File(userHomeDir, ".jenkinsIntegrations");
        resourcesLocation = resourcesFolder.getAbsolutePath();
        return resourcesLocation;
    }

    private static String getConfigFilePath() {
        File file = new File(getResourcesLocation(), ConfigFile.JENKINSS_INTEGRATION_CFG);
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    System.out.println("Not create " + file.getName());
                }
            } catch (IOException e) {
                //
            }
        }
        return file.getAbsolutePath();
    }

    private static String getHome() {
        return System.getProperty("user.home");
    }

    public static String getString(String key) {
        return get("Configs", key, ConfigFile.getConfigFilePath());
    }

    public static Integer getInteger(String key) {
        String configs = get("Configs", key, ConfigFile.getConfigFilePath());
        int i = 0;
        try {
            i = Integer.parseInt(configs);
        } catch (NumberFormatException e) {
//            return null;
        }
        return i;
    }

    public static boolean getBoolean(String key) {
        String configs = get("Configs", key, ConfigFile.getConfigFilePath());
        boolean b = false;
        try {
            b = Boolean.getBoolean(configs);
        } catch (NumberFormatException e) {
//            return null;
        }
        return b;
    }

    public static String get(String key) {
        return get("Configs", key, ConfigFile.getConfigFilePath());
    }

    public static String get(String section, String key) {
        return get(section, key, ConfigFile.getConfigFilePath());
    }

    public static String get(String section, String key, String file) {
        String val = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String currentSection = "";
            try {
                String line = br.readLine();
                while (line != null) {
                    if (line.trim().startsWith("[") && line.trim().endsWith("]")) {
                        currentSection = line.trim().substring(1, line.trim().length() - 1).toLowerCase();
                    } else {
                        if (section.toLowerCase().equals(currentSection)) {
                            String[] parts = line.split("=");
                            if (parts.length == 2 && parts[0].trim().equals(key)) {
                                val = parts[1].trim();
                                br.close();
                                return val;
                            }
                        }
                    }
                    line = br.readLine();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e1) { /* ignored */ }
        return val;
    }

    public static void set(String key, Object value) {
        set("Configs", key, value != null ? value.toString() : null);
    }

    public static void set(String section, String key, String val) {
        String file = ConfigFile.getConfigFilePath();
        StringBuilder contents = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            try {
                String currentSection = "";
                String line = br.readLine();
                boolean found = false;
                while (line != null) {
                    if (line.trim().startsWith("[") && line.trim().endsWith("]")) {
                        if (section.toLowerCase().equals(currentSection) && !found) {
                            contents.append(key).append(" = ").append(val).append("\n");
                            found = true;
                        }
                        currentSection = line.trim().substring(1, line.trim().length() - 1).toLowerCase();
                        contents.append(line).append("\n");
                    } else {
                        if (section.toLowerCase().equals(currentSection)) {
                            String[] parts = line.split("=");
                            String currentKey = parts[0].trim();
                            if (currentKey.equals(key)) {
                                if (!found) {
                                    contents.append(key).append(" = ").append(val).append("\n");
                                    found = true;
                                }
                            } else {
                                contents.append(line).append("\n");
                            }
                        } else {
                            contents.append(line).append("\n");
                        }
                    }
                    line = br.readLine();
                }
                if (!found) {
                    if (!section.toLowerCase().equals(currentSection)) {
                        contents.append("[").append(section.toLowerCase()).append("]\n");
                    }
                    contents.append(key).append(" = ").append(val).append("\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e1) {

            // cannot read config file, so create it
            contents = new StringBuilder();
            contents.append("[").append(section.toLowerCase()).append("]\n");
            contents.append(key).append(" = ").append(val).append("\n");
        }

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(file, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (writer != null) {
            writer.print(contents);
            writer.close();
        }
    }

    public static boolean isWindows() {
        return System.getProperty("os.name").contains("Windows");
    }
}
