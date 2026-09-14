package io.keelframework.mcp.common.jwt.model;

public record Role(String name) {
    public Role {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("KeycloakRole name must not be blank");
        }
    }

    public static Role of(String name) {
        return new Role(name.trim());
    }

    @Override
    public String toString() {
        return "ROLE_" + name.toUpperCase();
    }

}
