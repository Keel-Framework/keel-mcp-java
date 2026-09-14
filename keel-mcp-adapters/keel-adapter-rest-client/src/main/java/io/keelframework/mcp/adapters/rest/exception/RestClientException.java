package io.keelframework.mcp.adapters.rest.exception;

/**
 * Jerarquia de excepciones del adaptador REST MCP/SCA.
 *
 * sealed class con tres implementaciones:
 * - HttpError          → error HTTP (4xx, 5xx)
 * - Timeout            → timeout de conexion o lectura
 * - ServiceUnavailable → servicio no disponible
 */
public sealed class RestClientException
    extends RuntimeException
    permits RestClientException.HttpError,
        RestClientException.Timeout,
        RestClientException.ServiceUnavailable{

    protected RestClientException(String message){

        super(message);
    }

    protected RestClientException(String message, Throwable cause){

        super(message, cause);
    }

    /**
     * Error HTTP — codigo de estado 4xx o 5xx.
     */
    public static final class HttpError extends RestClientException  {

        private final String service;
        private final String path;
        private final int statusCode;

        public HttpError(String service, String path, int statusCode, String reason, Throwable cause) {
            super("HTTP error service=%s path=%s status=%s reason=%s"
                    .formatted(service, path, statusCode, reason), cause);
            this.service = service;
            this.path = path;
            this.statusCode = statusCode;
        }

        public String service() {
            return service;
        }

        public String path() {
            return path;
        }
        public int statusCode() {
            return statusCode;
        }
    }

    /**
     * Timeout — la peticion tardo mas de lo configurado.
     */
    public static final class Timeout extends RestClientException {

        private final String service;
        private final String path;

        public Timeout(String service, String path, Throwable cause) {
            super("Timeout service=%s path=%s".formatted(service, path), cause);
            this.service = service;
            this.path = path;
        }

        public String service() { return service; }
        public String path() { return path; }
    }

    /**
     * Servicio no disponible — no se pudo conectar.
     */
    public static final class ServiceUnavailable extends RestClientException {

        private final String service;

        public ServiceUnavailable(String service, Throwable cause) {
            super("Service unavailable service=%s".formatted(service), cause);
            this.service = service;
        }
        public String service() { return service; }
    }

}
