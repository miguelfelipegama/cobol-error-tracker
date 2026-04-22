package com.bank.errorproducer.service;

import com.bank.errorproducer.dto.CobolErrorDto;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ErrorProducerService {

    @Inject
    @Channel("cobol-errors-topic")
    Emitter<CobolErrorDto> emitter;

    public void sendError(CobolErrorDto errorDto) {
        emitter.send(errorDto)
               .whenComplete((success, failure) -> {
                   if (failure != null) {
                       System.err.println("Erro ao enviar mensagem pro Kafka: " + failure.getMessage());
                   } else {
                       System.out.println("Mensagem enviada com sucesso pro topico cobol-errors-topic!");
                   }
               });
    }
}
