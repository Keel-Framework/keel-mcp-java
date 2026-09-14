package io.keelframework.mcp.adapters.rest.model;

/**
 * Respuesta tipada de una peticion REST.
 *
 * Encapsula el resultado de una llamada HTTP:
 * - ok(body)     → peticion success con body
 * - error(msg)   → peticion fallida con mensaje
 *
 * Uso en tools MCP:
 * RestResponse<String> response = client.get("/policy/123");
 * if (response.success()) {
 *     return response.body();
 * }
 */
public record RestResponse<T>(
        T body,
        boolean success,
        String error,
        int httpStatus
)
{
    public static <T> RestResponse<T> ok(T body){

        return new RestResponse<>(body, true, null, 200);
    }

    public static <T> RestResponse<T> error(String error, int httpStatus){

        return new RestResponse<>(null, false, error, httpStatus);
    }

    public boolean hasBody(){
        return body != null;
    }
    public boolean isError(){
        return !success;
    }

}
