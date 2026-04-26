package com.brcm.config;

import com.brcm.config.LoginCredentials.Server;
import com.brcm.config.LoginCredentials.SchoolType;

import java.util.HashMap;
import java.util.Map;

/**
 * School ID pool per server environment.
 * Edit this file when new servers or school types are added.
 *
 * Usage (internal — called by LoginCredentials):
 *   String schoolId = CredentialPool.getSchoolId(Server.TEAM5, SchoolType.PRIMARY);
 */
public final class SchoolRegistry {

    private static final Map<Server, Map<SchoolType, String>> POOL = build();

    private SchoolRegistry() {}

    public static String getSchoolId(Server server, SchoolType type) {
        if (server == null || type == null) return "";
        Map<SchoolType, String> ids = POOL.get(server);
        return ids == null ? "" : ids.getOrDefault(type, "");
    }

    private static Map<Server, Map<SchoolType, String>> build() {
        Map<Server, Map<SchoolType, String>> pool = new HashMap<>();

        pool.put(Server.TEAM0, Map.ofEntries(
                Map.entry(SchoolType.PRIMARY, "283006"),
                Map.entry(SchoolType.SECONDARY, "283001"),
                Map.entry(SchoolType.PRIMARY_STANDALONE, "230006"),
                Map.entry(SchoolType.SECONDARY_STANDALONE, "230001"),
                Map.entry(SchoolType.SPECIAL, "230007"),
                Map.entry(SchoolType.EOTAS, "230003"),
                Map.entry(SchoolType.SECONDARY_EXAM_TIMETABLE, "230004")
        ));

        pool.put(Server.TEAM1, Map.ofEntries(
                Map.entry(SchoolType.SECONDARY, "880901"),
                Map.entry(SchoolType.SECONDARY_EXAM_TIMETABLE, "830004")
        ));

        pool.put(Server.TEAM4, Map.ofEntries(
                Map.entry(SchoolType.PRIMARY, "840006"),
                Map.entry(SchoolType.SECONDARY, "840001")
        ));

        pool.put(Server.TEAM5, Map.ofEntries(
                Map.entry(SchoolType.PRIMARY, "880006"),
                Map.entry(SchoolType.SECONDARY, "880001"),
                Map.entry(SchoolType.PRIMARY_STANDALONE, "853006"),
                Map.entry(SchoolType.SECONDARY_STANDALONE, "853002"),
                Map.entry(SchoolType.SPECIAL, "853007"),
                Map.entry(SchoolType.EOTAS, "853003")
        ));

        pool.put(Server.TEAM6, Map.ofEntries(
                Map.entry(SchoolType.PRIMARY, "583006"),
                Map.entry(SchoolType.SECONDARY, "583001")
        ));

        pool.put(Server.RELEASE, Map.ofEntries(
                Map.entry(SchoolType.PRIMARY, "383006"),
                Map.entry(SchoolType.SECONDARY, "383001"),
                Map.entry(SchoolType.PRIMARY_STANDALONE, "330006"),
                Map.entry(SchoolType.SECONDARY_STANDALONE, "330001"),
                Map.entry(SchoolType.SPECIAL, "330007"),
                Map.entry(SchoolType.EOTAS, "330003"),
                Map.entry(SchoolType.SECONDARY_EXAM_TIMETABLE, "330004")
        ));

        pool.put(Server.HOTFIX, Map.ofEntries(
                Map.entry(SchoolType.PRIMARY, "683006"),
                Map.entry(SchoolType.SECONDARY, "683001"),
                Map.entry(SchoolType.PRIMARY_STANDALONE, "630006"),
                Map.entry(SchoolType.SECONDARY_STANDALONE, "630001"),
                Map.entry(SchoolType.SPECIAL, "630007"),
                Map.entry(SchoolType.EOTAS, "630003"),
                Map.entry(SchoolType.SECONDARY_EXAM_TIMETABLE, "630004")
        ));

        pool.put(Server.DELIVERY, Map.ofEntries(
                Map.entry(SchoolType.PRIMARY, "783006"),
                Map.entry(SchoolType.SECONDARY, "783001"),
                Map.entry(SchoolType.PRIMARY_STANDALONE, "730006"),
                Map.entry(SchoolType.SECONDARY_STANDALONE, "730001"),
                Map.entry(SchoolType.SPECIAL, "730007"),
                Map.entry(SchoolType.EOTAS, "730003"),
                Map.entry(SchoolType.SECONDARY_EXAM_TIMETABLE, "730004")
        ));

        return pool;
    }
}
