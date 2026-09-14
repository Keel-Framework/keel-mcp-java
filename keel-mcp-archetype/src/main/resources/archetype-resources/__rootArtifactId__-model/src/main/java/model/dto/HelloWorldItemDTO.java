#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.model.dto;

import java.io.Serializable;

/**
 * DTO de ejemplo "Hello World" — demuestra el mapeo Entity ↔ DTO vía
 * MapStruct (ver HelloWorldItemMapper). Sustitúyelo por tus propios DTOs
 * de negocio, o bórralo si no lo necesitas.
 */
public record HelloWorldItemDTO(
        Long id,
        String message
) implements Serializable {
}