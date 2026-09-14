#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.model.entity;

import java.io.Serializable;

/**
 * Entity de ejemplo "Hello World" — modelo interno, sin persistencia JPA
 * (este arquetipo MCP no incluye adaptador JDBC/JPA). Si en el futuro
 * añades persistencia real con Hibernate, esta clase deberá pasar a ser
 * una @Entity mutable con constructor vacío, ya que Hibernate no soporta
 * records de forma estándar.
 */
public record HelloWorldItemEntity(
        Long id,
        String message
) implements Serializable {
}