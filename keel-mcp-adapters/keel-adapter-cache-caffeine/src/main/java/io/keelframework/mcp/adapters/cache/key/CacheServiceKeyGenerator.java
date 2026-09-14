package io.keelframework.mcp.adapters.cache.key;
import org.springframework.cache.interceptor.KeyGenerator;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Generador estándar de claves de cache para el ecosistema MCP.
 *
 * Formato de clave generada:
 * {SimpleClassName}:{methodName}:{param1}:{param2}:...
 *
 * @Cacheable(
 *     cacheNames = "tipos-cambio",
 *     keyGenerator = "cacheServiceKeyGenerator"
 * )
 * public TipoCambio obtenerTipoCambio(String moneda) { ... }
 *
 * Registrado como bean con nombre "cacheServiceKeyGenerator"
 * en CacheServiceConfig.
 */
public final class CacheServiceKeyGenerator implements KeyGenerator {

    private static final String SEPARATOR = ":";
    private static final String NULL_PARAM = "null";

    @Override
    public Object generate(Object target, Method method, Object... params) {

        String className  = target.getClass().getSimpleName();
        String methodName = method.getName();
        String paramsPart = buildParamsPart(params);

        return className + SEPARATOR + methodName + SEPARATOR + paramsPart;
    }

    private String buildParamsPart(Object[] params) {
        if (params == null || params.length == 0) {
            return "no-params";
        }

        return Arrays.stream(params)
                .map(p -> p != null ? p.toString() : NULL_PARAM)
                .collect(Collectors.joining(SEPARATOR));
    }
}