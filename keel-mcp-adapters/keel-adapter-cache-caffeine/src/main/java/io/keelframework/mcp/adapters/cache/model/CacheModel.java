package io.keelframework.mcp.adapters.cache.model;

public record CacheModel(
        String cacheName,
        long   hitCount,
        long   missCount,
        double hitRate,
        long   evictionCount,
        long   estimatedSize
) {
    /**
     * Instancia vacía cuando no hay estadísticas disponibles.
     */
    public static CacheModel empty(String cacheName) {
        return new CacheModel(cacheName, 0L, 0L, 0.0, 0L, 0L);
    }

    /**
     * Porcentaje de misses — complemento del hitRate.
     */
    public double missRate() {
        return 1.0 - hitRate;
    }

    /**
     * Total de requests — hits + misses.
     */
    public long totalRequests() {
        return hitCount + missCount;
    }

}
