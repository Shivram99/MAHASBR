package com.mahasbr.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mahasbr.service.BrnGeneratorService;
import com.mahasbr.service.SequenceBrnGenerator;

@Component
public class BrnGeneratorFactory {

    private final SequenceBrnGenerator sequenceGenerator;
    private final SequenceBrnGenerator  tableGenerator;

    @Value("${brn.generator.mode:SEQUENCE}")
    private String mode;

    public BrnGeneratorFactory(SequenceBrnGenerator sequenceGenerator,
    		SequenceBrnGenerator  tableGenerator) {
        this.sequenceGenerator = sequenceGenerator;
        this.tableGenerator = tableGenerator;
    }

    public BrnGeneratorService getGenerator() {
        return "TABLE".equalsIgnoreCase(mode) ? tableGenerator : sequenceGenerator;
    }
}
