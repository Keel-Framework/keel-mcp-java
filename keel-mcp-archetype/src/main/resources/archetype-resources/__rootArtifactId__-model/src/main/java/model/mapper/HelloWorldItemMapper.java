#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.model.mapper;

import org.mapstruct.Mapper;
import java.util.List;
import ${package}.model.dto.HelloWorldItemDTO;
import ${package}.model.entity.HelloWorldItemEntity;

/**
 * Mapper de ejemplo "Hello World" — demuestra el mapeo Entity ↔ DTO vía
 * MapStruct. Como HelloWorldItemDTO y HelloWorldItemEntity son records con
 * los mismos nombres de componente (id, message), MapStruct los mapea
 * automáticamente sin necesidad de @Mapping explícito.
 * Sustitúyelo por tus propios mappers de negocio, o bórralo si no lo necesitas.
 */
@Mapper
public interface HelloWorldItemMapper {

    HelloWorldItemDTO asHelloWorldItemDTO(HelloWorldItemEntity helloWorldItemEntity);
    HelloWorldItemEntity asHelloWorldItemEntity(HelloWorldItemDTO helloWorldItemDTO);
    List<HelloWorldItemDTO> asHelloWorldItemDTOs(List<HelloWorldItemEntity> src);
}