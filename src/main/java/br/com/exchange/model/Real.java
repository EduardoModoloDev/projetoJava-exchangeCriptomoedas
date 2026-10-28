package br.com.exchange.model;

import java.math.BigDecimal;
import java.util.Random;

/**
 * Real com cotação fixa em 1.00.
 */
public class Real extends Moedas {

    public Real() {
        super("Real", "REAL", new BigDecimal("1.00"));
    }

    @Override
    public void atualizarCotacao(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("O gerador aleatório deve ser informado.");
        }
        // O real fica sempre em 1.00.
    }
}
