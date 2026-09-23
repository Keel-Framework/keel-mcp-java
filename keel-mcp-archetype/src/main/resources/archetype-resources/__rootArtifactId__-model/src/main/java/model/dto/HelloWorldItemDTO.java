#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.model.dto;

import java.io.Serializable;

/**
 * record HelloWorldItemDTO
 *
 * "Hello World" sample DTO — demonstrates Entity ↔ DTO mapping via
 * MapStruct (see HelloWorldItemMapper). Replace it with your own
 * business DTOs, or delete it if you don't need it.
 */

public record HelloWorldItemDTO(
        Long id,
        String message
) implements Serializable {
}