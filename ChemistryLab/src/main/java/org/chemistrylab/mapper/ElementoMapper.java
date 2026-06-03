package org.chemistrylab.mapper;

import org.chemistrylab.dto.ElementoDTO;
import org.chemistrylab.entity.ElementoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ElementoMapper {

    ElementoDTO toDTO(ElementoEntity elementoEntity);

    ElementoEntity toEntity(ElementoDTO elementoDTO);

    void updateEntityFromDTO(ElementoDTO elementoDTO, @MappingTarget ElementoEntity elementoEntity);
}