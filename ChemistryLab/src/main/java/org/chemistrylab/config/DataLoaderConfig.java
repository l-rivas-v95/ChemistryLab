package org.chemistrylab.config;

import org.chemistrylab.dto.ElementoDTO;
import org.chemistrylab.entity.ElementoEntity;
import org.chemistrylab.mapper.ElementoMapper;
import org.chemistrylab.repository.ElementoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.List;

@Configuration
public class DataLoaderConfig {

    @Bean
    public CommandLineRunner cargarElementos(
            ElementoRepository elementoRepository,
            ElementoMapper elementoMapper,
            ObjectMapper objectMapper
    ) {
        return args -> {
            if (elementoRepository.count() > 0) {
                return;
            }

            ClassPathResource resource = new ClassPathResource("data/elementos.json");

            try (InputStream inputStream = resource.getInputStream()) {
                List<ElementoDTO> elementos = objectMapper.readValue(
                        inputStream,
                        new TypeReference<List<ElementoDTO>>() {}
                );

                List<ElementoEntity> entidades = elementos.stream()
                        .map(elementoMapper::toEntity)
                        .toList();

                elementoRepository.saveAll(entidades);
            }
        };
    }
}