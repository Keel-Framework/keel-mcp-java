package io.keelframework.mcp.adapters.cache.exception;

public final class CacheServiceException extends RuntimeException {

    private final String cacheName;

    public CacheServiceException(String message, String cacheName) {
        super(buildMessage(message, cacheName));
        this.cacheName = cacheName;
    }

    public CacheServiceException(String message,
                                 String cacheName,
                                 Throwable cause) {
        super(buildMessage(message, cacheName), cause);
        this.cacheName = cacheName;
    }

    public String cacheName() {
        return cacheName;
    }

    private static String buildMessage(String message, String cacheName) {
        return "%s [cache=%s]".formatted(message, cacheName);
    }

}
