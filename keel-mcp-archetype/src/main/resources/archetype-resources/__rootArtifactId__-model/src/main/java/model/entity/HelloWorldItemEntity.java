#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.model.entity;

import java.io.Serializable;

/**
 * record HelloWorldItemEntity
 *
 * "Hello World" sample entity — internal model, without JPA persistence
 * (this MCP archetype does not include a JDBC/JPA adapter). If you add
 * real persistence with Hibernate in the future, this class will need
 * to become a mutable @Entity with a no-args constructor, since
 * Hibernate does not support records out of the box.
 */

public record HelloWorldItemEntity(
        Long id,
        String message
) implements Serializable {
}