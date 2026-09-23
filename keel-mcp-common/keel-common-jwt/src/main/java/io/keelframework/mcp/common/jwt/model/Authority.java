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
