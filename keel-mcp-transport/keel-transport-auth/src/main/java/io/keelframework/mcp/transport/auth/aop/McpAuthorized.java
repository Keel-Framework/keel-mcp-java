/*
 * Copyright 2026 Keel Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.keelframework.mcp.transport.auth.aop;

import java.lang.annotation.*;

/**
 * Anotación declarativa para proteger métodos con autorización de Keycloak.
 * El advisor AOP verifica roles y/o scopes en el SecurityContext.
 *
 * Uso:
 *   {@literal @}KeycloakAuthorized(roles = "ADMIN")
 *   public void deleteUser(String id) { ... }
 *
 *   {@literal @}KeycloakAuthorized(roles = {"ADMIN", "MANAGER"}, requireAllRoles = false)
 *   public void updateUser(String id) { ... }
 *
 *   {@literal @}KeycloakAuthorized(scopes = "write:users")
 *   public void createUser(UserDto dto) { ... }
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface McpAuthorized {

    /** Roles de realm requeridos. Vacío = no se valida rol. */
    String[] roles() default {};

    /** Scopes OAuth2 requeridos. Vacío = no se valida scope. */
    String[] scopes() default {};

    /**
     * Si true (default), el usuario debe tener TODOS los roles declarados.
     * Si false, basta con tener al menos UNO.
     */
    boolean requireAllRoles() default true;

    /**
     * Si true (default), el usuario debe tener TODOS los scopes declarados.
     * Si false, basta con tener al menos UNO.
     */
    boolean requireAllScopes() default true;
}
