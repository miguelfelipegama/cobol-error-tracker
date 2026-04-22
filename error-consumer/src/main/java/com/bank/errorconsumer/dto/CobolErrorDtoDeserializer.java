package com.bank.errorconsumer.dto;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class CobolErrorDtoDeserializer extends ObjectMapperDeserializer<CobolErrorDto> {
    public CobolErrorDtoDeserializer() {
        super(CobolErrorDto.class);
    }
}
