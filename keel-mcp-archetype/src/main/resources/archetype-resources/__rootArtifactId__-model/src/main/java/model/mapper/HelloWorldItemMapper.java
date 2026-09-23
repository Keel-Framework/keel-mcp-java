#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.model.mapper;

import org.mapstruct.Mapper;
import java.util.List;
import ${package}.model.dto.HelloWorldItemDTO;
import ${package}.model.entity.HelloWorldItemEntity;

/**
 * HelloWorldItemMapper
 *
 * "Hello World" sample mapper — demonstrates Entity ↔ DTO mapping via
 * MapStruct. Since HelloWorldItemDTO and HelloWorldItemEntity are records
 * with matching component names (id, message), MapStruct maps them
 * automatically with no need for an explicit @Mapping.
 * Replace it with your own business mappers, or delete it if you don't
 * need it.
 */
@Mapper
public interface HelloWorldItemMapper {

    HelloWorldItemDTO asHelloWorldItemDTO(HelloWorldItemEntity helloWorldItemEntity);
    HelloWorldItemEntity asHelloWorldItemEntity(HelloWorldItemDTO helloWorldItemDTO);
    List<HelloWorldItemDTO> asHelloWorldItemDTOs(List<HelloWorldItemEntity> src);
}