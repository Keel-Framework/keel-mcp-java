package io.keelframework.mcp.adapters.auth.idp.jwks;

public class JwksKeyResolutionException extends RuntimeException {

    public JwksKeyResolutionException(String message) {
        super(message);
    }

    public JwksKeyResolutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
