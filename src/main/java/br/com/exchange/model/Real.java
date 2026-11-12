package br.com.exchange.model;

import java.math.BigDecimal;
import java.util.Random;

/**
 * Real com cotação fixa em 1.00.
 */
public class Real extends Moedas {

    /**
     * Cria a moeda com a cotação padrão.
     */
    public Real() {
        super("Real", "REAL", new BigDecimal("1.00"));
    }

    /**
     * Atualiza a cotação com uma variação entre -5% e +5%.
     *
     * param random gerador da variação aleatória
     */
    @Override
    public void atualizarCotacao(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("O gerador aleatório deve ser informado.");
        }
        // O real fica sempre em 1.00.
    }
}
