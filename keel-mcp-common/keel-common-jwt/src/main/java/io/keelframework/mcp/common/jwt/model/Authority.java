package io.keelframework.mcp.common.jwt.model;

import org.springframework.security.core.GrantedAuthority;

/**
 * Adapta Role al contrato GrantedAuthority de Spring Security.
 * Permite que el SecurityContext funcione con las APIs estándar de Spring Security
 * sin acoplar el modelo interno a Spring.
 */
public record Authority(Role role)  implements GrantedAuthority {

    @Override
    public String getAuthority() {
        return role.toString();
    }

    public static Authority of(String roleName){
        return new Authority(Role.of(roleName));
    }


}
