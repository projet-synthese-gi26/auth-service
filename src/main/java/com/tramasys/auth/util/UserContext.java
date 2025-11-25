package com.tramasys.auth.util;

import java.util.List;
import java.util.UUID;

public class UserContext {

    private static final ThreadLocal<Context> context = new ThreadLocal<>();

    public record Context(UUID userId, String username, List<String> roles, List<String> permissions) {}

    public static void set(UUID userId, String username, List<String> roles, List<String> permissions) {
        context.set(new Context(userId, username, roles, permissions));
    }

    public static UUID getUserId() {
        return context.get() != null ? context.get().userId() : null;
    }

    public static String getUsername() {
        return context.get() != null ? context.get().username() : null;
    }

    public static List<String> getRoles() {
        return context.get() != null ? context.get().roles() : List.of();
    }

    public static List<String> getPermissions() {
        return context.get() != null ? context.get().permissions() : List.of();
    }

    public static void clear() {
        context.remove();
    }
}
