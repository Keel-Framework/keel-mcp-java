package io.keelframework.mcp.adapters.rest.interceptor;

import java.util.List;

/**
 * Headers estandar SCA para propagacion entre MCP Server y Microservicios.
 *
 * INTERNOS del ecosistema — no configurables por el desarrollador.
 * Definidos por el equipo de arquitectura SCA como estandar obligatorio.
 *
 * Estos headers se propagan automaticamente en cada peticion HTTP saliente
 * del adaptador REST hacia los microservicios backend.
 *
 * El interceptor McpHeadersInterceptor los gestiona de forma transparente.
 */

public final class McpHeaders {

    // No instanciable — solo constantes
    private McpHeaders() {}

    // ================================================
    // JWT — siempre propagado
    // ================================================

    /**
     * Header de autenticacion JWT.
     * Propagado SIEMPRE desde el SecurityContextHolder.
     * Valor: "Bearer <jwt-token>"
     */
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    // ================================================
    // HEADERS DE PROPAGACION SCA
    // Estandar de arquitectura — no modificables
    // ================================================

    /**
     * Canal de origen de la peticion.
     * Ejemplo: WEB, MOBILE, API, BATCH
     */
    public static final String CHANNEL = "channel";

    /**
     * Identificador de la origen del request.
     * Ejemplo: mcp-server-poc, mcp-server-pagos
     */
    public static final String LOCALE = "locale";

    /**
     * Identificador de la aplicacion origen.
     * Ejemplo: mcp-server-poc, mcp-server-pagos
     */
    public static final String APPLICATION_ID = "applicationid";


    /**
     * Identificador de traza distribuida.
     * Compatible con OpenTelemetry / Jaeger / Zipkin.
     */
    public static final String TRACE_ID = "traceid";

    /**
     * Identificador del span actual en la traza.
     */
    public static final String SPAN_ID = "spanid";

    /**
     * Identificador del dispositivo del cliente.
     * Ejemplo: DESKTOP, MOBILE, TABLET
     */
    public static final String X_ADESLAS_DEVICE = "x-adeslas-device";

    public static final String X_ADESLAS_UUID = "x-adeslas-uuid";

    public static final String X_IBM_CLIENT_ID = "x-ibm-client-id";

    /**
     * Lista ordenada de headers a propagar en cada request saliente.
     * Se leen del MDC y se añaden automaticamente via McpHeadersInterceptor.
     *
     * El MDC es poblado por el interceptor o filtro del servidor MCP
     * al recibir el request del cliente.
     */
    public static final List<String> PROPAGATION_HEADERS = List.of(
            CHANNEL,
            APPLICATION_ID,
            TRACE_ID,
            SPAN_ID,
            X_ADESLAS_DEVICE,
            X_ADESLAS_UUID,
            X_IBM_CLIENT_ID
    );

}
