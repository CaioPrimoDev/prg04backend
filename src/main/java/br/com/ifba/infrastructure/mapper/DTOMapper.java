package br.com.ifba.infrastructure.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DTOMapper {

    private final ModelMapper modelMapper;

    public <Input, Output> Output map(final Input object, final Class<Output> clazz) {
        return modelMapper.map(object, clazz);
    }
}
