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
package io.keelframework.mcp.adapters.auth.idp.validator;

import io.keelframework.mcp.common.jwt.provider.JwtClaims;

public sealed interface TokenValidationResult permits
        TokenValidationResult.Valid,
        TokenValidationResult.Invalid{

    record Valid(JwtClaims claims) implements  TokenValidationResult{}
    record Invalid(String reason) implements TokenValidationResult{}

    static Valid valid(JwtClaims claims){
        return new Valid(claims);
    }

    static Invalid invalid(String reason){
        return new Invalid(reason);
    }

}
