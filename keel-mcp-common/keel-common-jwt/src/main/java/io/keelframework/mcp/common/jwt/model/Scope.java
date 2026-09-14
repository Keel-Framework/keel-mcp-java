package io.keelframework.mcp.common.jwt.model;

public record Scope(String name) {
    public Scope {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("KeycloakScope name must not be blank");
        }
    }

    public static Scope of(String name) {
        return new Scope(name.trim().toLowerCase());
    }
}
