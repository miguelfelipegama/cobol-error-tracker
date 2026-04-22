package com.bank.errorconsumer.service;

import com.bank.errorconsumer.dto.CobolErrorDto;
import com.bank.errorconsumer.entity.CobolError;
import com.bank.errorconsumer.entity.ErrorCorrelation;
import com.bank.errorconsumer.entity.ErrorVariable;
import com.bank.errorconsumer.repository.CobolErrorRepository;
import com.bank.errorconsumer.repository.ErrorCorrelationRepository;
import com.bank.errorconsumer.repository.ErrorVariableRepository;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import io.smallrye.reactive.messaging.annotations.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.UUID;

@ApplicationScoped
public class ErrorConsumerService {

    @Inject
    ErrorCorrelationRepository correlationRepository;

    @Inject
    CobolErrorRepository errorRepository;

    @Inject
    ErrorVariableRepository variableRepository;

    @Incoming("cobol-errors-topic")
    @Blocking
    @Transactional
    public void consumeError(CobolErrorDto errorDto) {
        
        // 1. Buscar ou Criar a Tabela Mãe (Correlação)
        ErrorCorrelation correlation = correlationRepository
            .findByContext(
                errorDto.getCicsCpu(),
                errorDto.getTaskCode(),
                errorDto.getCreditObjectNumber(),
                errorDto.getCreditObjectType()
            );
            
        if (correlation == null) {
            correlation = new ErrorCorrelation();
            correlation.setId(UUID.randomUUID());
            correlation.setCicsCpu(errorDto.getCicsCpu());
            correlation.setTaskCode(errorDto.getTaskCode());
            correlation.setCreditObjectNumber(errorDto.getCreditObjectNumber());
            correlation.setCreditObjectType(errorDto.getCreditObjectType());
            correlation.setCreatedAt(LocalDateTime.now());
            correlationRepository.persist(correlation);
        }

        // 2. Gerar Hash Salted Intrinssecamente Único para o Erro (Tabela 2)
        String plainTextForHash = String.join("|", 
                errorDto.getCicsCpu(), errorDto.getTaskCode(), 
                errorDto.getCreditObjectNumber(), errorDto.getCreditObjectType(),
                errorDto.getErrorCode(), errorDto.getProgramName(), String.valueOf(System.nanoTime()));
        
        String hashId = generateSha256(plainTextForHash);
        
        CobolError cobolError = new CobolError();
        cobolError.setId(hashId);
        cobolError.setCorrelation(correlation);
        cobolError.setErrorCode(errorDto.getErrorCode());
        cobolError.setErrorMessage(errorDto.getErrorMessage());
        cobolError.setProgramName(errorDto.getProgramName());
        cobolError.setTimestamp(LocalDateTime.now());
        
        errorRepository.persist(cobolError);

        // 3. Cadastrar Múltiplas Variáveis Iterativamente (Tabela 3)
        if (errorDto.getVariables() != null) {
            int sequence = 1;
            for (var varDto : errorDto.getVariables()) {
                ErrorVariable variable = new ErrorVariable();
                variable.setErrorId(hashId);
                variable.setSequence(sequence++);
                variable.setVariableName(varDto.getName());
                variable.setVariableValue(varDto.getValue());
                variableRepository.persist(variable);
            }
        }
    }

    private String generateSha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodedhash);
        } catch (Exception e) {
            throw new RuntimeException("Error generating hash", e);
        }
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
