package com.bank.errorproducer.dto;

import io.quarkus.kafka.client.serialization.ObjectMapperSerializer;

public class CobolErrorDtoSerializer extends ObjectMapperSerializer<CobolErrorDto> {
    public CobolErrorDtoSerializer() {
        super();
    }
}
