package com.brcm.config;

import com.brcm.utils.ConfigReader;

import java.util.HashMap;
import java.util.Map;

/**
 * One-stop credential API for tests.
 *
 * QUICK USAGE:
 *   // 1. By school type
 *   Resolved creds = LoginCredentials.forSchool(baseUrl, "secondary");
 *
 *   // 2. By explicit schoolId
 *   Resolved creds = LoginCredentials.forSchool(baseUrl, "283001");
 *
 *   // 3. With custom overrides (map)
 *   Resolved creds = LoginCredentials.forSchool(baseUrl, Map.of(
 *       "schoolType", "primary",
 *       "username", "alice"
 *   ));
 *
 *   // 4. Server-keyed overrides
 *   Resolved creds = LoginCredentials.forSchool(baseUrl, Map.of(
 *       "ALL", Map.of("username", "bob"),
 *       "TEAM5", Map.of("schoolType", "secondary")
 *   ));
 *
 *   // 5. Defaults from config.properties
 *   Resolved creds = LoginCredentials.defaults();
 *
 *   // 6. Fluent builder
 *   Resolved creds = LoginCredentials.custom(baseUrl)
 *       .withSchoolType("primary")
 *       .withUsername("alice")
 *       .resolve();
 */
public final class LoginCredentials {

    private LoginCredentials() {}

    // ========== PUBLIC API ==========

    /** Resolve credentials from a simple string (school type or schoolId). */
    public static Resolved forSchool(String baseUrl, String schoolTypeOrId) {
        if (schoolTypeOrId == null || schoolTypeOrId.isBlank()) {
            throw new IllegalArgumentException("schoolTypeOrId cannot be blank");
        }
        Server server = Server.getServerForUrl(baseUrl);
        return resolveString(server, schoolTypeOrId.trim());
    }

    /** Resolve credentials from a flexible map (supports server-keyed overrides). */
    public static Resolved forSchool(String baseUrl, Map<String, Object> config) {
        if (config == null || config.isEmpty()) {
            throw new IllegalArgumentException("config map cannot be null or empty");
        }
        Server server = Server.getServerForUrl(baseUrl);
        return resolveMap(server, config);
    }

    /**
     * Resolve from config.properties defaults.
     * Reads login.school.type + default.base.url, looks up schoolId in SchoolRegistry,
     * then combines with login.username / login.password.
     */
    public static Resolved fromProperties(String baseUrl) {
//        String baseUrl   = ConfigReader.getOrDefault("default.base.url", "").trim();
        String typeKey   = ConfigReader.getOrDefault("login.school.type", "").trim();
        String username  = ConfigReader.getOrDefault("login.username", "").trim();
        String password  = ConfigReader.getOrDefault("login.password", "").trim();

        if (typeKey.isBlank()) {
            throw new IllegalStateException("login.school.type is missing in config.properties");
        }

        Server server = Server.getServerForUrl(baseUrl);
        String schoolId = SchoolRegistry.getSchoolId(server, SchoolType.of(typeKey));

        if (schoolId == null || schoolId.isBlank()) {
            throw new IllegalStateException(
                    "No schoolId in registry for server=" + server + ", schoolType=" + typeKey);
        }

        return new Resolved(schoolId, username, password);
    }

    /** Start a fluent builder. */
    public static Builder builder(String baseUrl) {
        return new Builder(baseUrl);
    }

    // ========== NESTED TYPES ==========

    /** Final resolved credentials ready for login. */
    public record Resolved(String schoolId, String username, String password) {
        public Resolved {
            if (schoolId == null || schoolId.isBlank()) {
                throw new IllegalArgumentException("schoolId cannot be blank");
            }
            if (username == null || username.isBlank()) {
                throw new IllegalArgumentException("username cannot be blank");
            }
            if (password == null || password.isBlank()) {
                throw new IllegalArgumentException("password cannot be blank");
            }
        }
    }

    /** Server environments (maps base URL → server key). */
    public enum Server {
        TEAM0("https://mis-teams-team0.bromcom.dev/"),
        TEAM1("https://mis-teams-team1.bromcom.dev/"),
        TEAM2("https://mis-teams-team2.bromcom.dev/"),
        TEAM3("https://mis-teams-team3.bromcom.dev/"),
        TEAM4("https://mis-teams-team4.bromcom.dev/"),
        TEAM5("https://mis-teams-team5.bromcom.dev/"),
        TEAM6("https://mis-teams-team6.bromcom.dev/"),
        DELIVERY("https://mis-delivery.bromcom.dev/"),
        RELEASE("https://mis-release.bromcom.dev/"),
        HOTFIX("https://mis-hotfix.bromcom.dev/"),
        CLOUD("https://cloudmis.bromcom.com/");

        private final String baseUrl;

        Server(String baseUrl) { this.baseUrl = baseUrl; }

        public String baseUrl() { return baseUrl; }

        public static Server getServerForUrl(String baseUrl) {
            if (baseUrl == null || baseUrl.isBlank()) return RELEASE;
            String normalized = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
            for (Server s : values()) {
                if (normalized.startsWith(s.baseUrl)) return s;
            }
            throw new IllegalArgumentException("No server found for URL: " + baseUrl);
        }
    }

    /** Supported school types. */
    public enum SchoolType {
        PRIMARY("primary"),
        SECONDARY("secondary"),
        PRIMARY_STANDALONE("primary_standalone"),
        SECONDARY_STANDALONE("secondary_standalone"),
        SPECIAL("special"),
        EOTAS("eotas"),
        SECONDARY_EXAM_TIMETABLE("secondary_exam_timetable");

        private final String key;

        SchoolType(String key) { this.key = key; }

        public String key() { return key; }

        public static SchoolType of(String value) {
            if (value == null || value.isBlank()) return null;
            String normalized = value.trim().toLowerCase().replace('-', '_');
            for (SchoolType t : values()) {
                if (t.key.equals(normalized) || t.name().equalsIgnoreCase(normalized)) {
                    return t;
                }
            }
            throw new IllegalArgumentException("Unknown school type: " + value);
        }
    }

    /** Fluent builder for complex credential setup. */
    public static final class Builder {
        private final String baseUrl;
        private final Map<String, Object> data = new HashMap<>();

        private Builder(String baseUrl) { this.baseUrl = baseUrl; }

        public Builder withSchoolType(String schoolType) { data.put("schoolType", schoolType); return this; }
        public Builder withSchoolId(String schoolId)     { data.put("schoolId",   schoolId);   return this; }
        public Builder withUsername(String username)     { data.put("username",   username);   return this; }
        public Builder withPassword(String password)     { data.put("password",   password);   return this; }

        public Resolved resolve() {
            return forSchool(baseUrl, new HashMap<>(data));
        }
    }

    // ========== INTERNAL RESOLUTION LOGIC ==========

    private static String globalUsername() {
        return ConfigReader.getOrDefault("login.username", "").trim();
    }

    private static String globalPassword() {
        return ConfigReader.getOrDefault("login.password", "").trim();
    }

    /** If string parses as SchoolType → pool lookup. Otherwise treat as raw schoolId. */
    private static Resolved resolveString(Server server, String value) {
        try {
            SchoolType type = SchoolType.of(value);
            String schoolId = SchoolRegistry.getSchoolId(server, type);
            if (schoolId == null || schoolId.isBlank()) {
                throw new IllegalStateException("No schoolId for " + server + " + " + type);
            }
            return new Resolved(schoolId, globalUsername(), globalPassword());
        } catch (IllegalArgumentException e) {
            // Not a school type → treat as raw schoolId
            return new Resolved(value, globalUsername(), globalPassword());
        }
    }

    private static Resolved resolveMap(Server server, Map<String, Object> map) {
        return isServerKeyed(map) ? resolveServerKeyed(server, map) : resolveProperties(server, map);
    }

    private static boolean isServerKeyed(Map<String, Object> map) {
        for (String key : map.keySet()) {
            if (key.equalsIgnoreCase("ALL")) return true;
            try {
                Server.valueOf(key.toUpperCase());
                return true;
            } catch (IllegalArgumentException ignored) {}
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private static Resolved resolveServerKeyed(Server server, Map<String, Object> map) {
        String username = globalUsername();
        String password = globalPassword();
        String schoolType = null;
        String schoolId = null;

        // Apply "ALL" first
        if (map.get("ALL") instanceof Map<?, ?> all) {
            Map<String, Object> allMap = (Map<String, Object>) all;
            username   = getOr(allMap, "username",   username);
            password   = getOr(allMap, "password",   password);
            schoolType = getOr(allMap, "schoolType", schoolType);
            schoolId   = getOr(allMap, "schoolId",   schoolId);
        }

        // Apply server-specific
        if (map.get(server.name()) instanceof Map<?, ?> s) {
            Map<String, Object> serverMap = (Map<String, Object>) s;
            username   = getOr(serverMap, "username",   username);
            password   = getOr(serverMap, "password",   password);
            schoolType = getOr(serverMap, "schoolType", schoolType);
            schoolId   = getOr(serverMap, "schoolId",   schoolId);
        }

        if (schoolId == null || schoolId.isBlank()) {
            if (schoolType == null || schoolType.isBlank()) {
                throw new IllegalStateException("No schoolType or schoolId for " + server);
            }
            schoolId = SchoolRegistry.getSchoolId(server, SchoolType.of(schoolType));
        }

        return new Resolved(schoolId, username, password);
    }

    private static Resolved resolveProperties(Server server, Map<String, Object> map) {
        String schoolType = getOr(map, "schoolType", null);
        String schoolId   = getOr(map, "schoolId",   null);
        String username   = getOr(map, "username",   globalUsername());
        String password   = getOr(map, "password",   globalPassword());

        if ((schoolId == null || schoolId.isBlank()) && schoolType != null && !schoolType.isBlank()) {
            schoolId = SchoolRegistry.getSchoolId(server, SchoolType.of(schoolType));
        }

        if (schoolId == null || schoolId.isBlank()) {
            throw new IllegalStateException("Map must contain 'schoolType' or 'schoolId'");
        }

        return new Resolved(schoolId, username, password);
    }

    private static String getOr(Map<String, Object> map, String key, String fallback) {
        Object v = map.get(key);
        if (v instanceof String s && !s.isBlank()) return s.trim();
        return fallback;
    }
}
