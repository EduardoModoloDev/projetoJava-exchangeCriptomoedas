package br.com.exchange.model;

import br.com.exchange.util.Validacao;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

/**
 * Dados da moeda e sua cotação em reais.
 */
public abstract class Moedas {

    private final String nome;
    private final String sigla;
    private BigDecimal cotacao;

    /** A cotação deve ser positiva e é guardada com duas casas decimais. */
    protected Moedas(String nome, String sigla, BigDecimal cotacao) {
        Validacao.validarValorNaoNegativo(cotacao, "A cotação");
        BigDecimal valorAjustado = cotacao.setScale(2, RoundingMode.HALF_UP);
        if (valorAjustado.signum() == 0) {
            throw new IllegalArgumentException("A cotação deve ser maior que zero após o arredondamento.");
        }
        this.nome = nome;
        this.sigla = sigla;
        this.cotacao = valorAjustado;
    }

    public String getNome() {
        return nome;
    }

    public String getSigla() {
        return sigla;
    }

    public BigDecimal getCotacao() {
        return cotacao;
    }

    public void atualizarCotacao() {
        atualizarCotacao(new Random());
    }

    /**
     * Sorteia uma variação entre -5% e +5%.
     * Mantém a cotação anterior se o arredondamento passar do limite.
     */
    public void atualizarCotacao(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("O gerador aleatório deve ser informado.");
        }

        // Converte o sorteio para uma variação entre -0.05 e 0.05.
        int numeroSorteado = random.nextInt(10001) - 5000;
        BigDecimal variacao = new BigDecimal(numeroSorteado).divide(new BigDecimal("100000"));
        BigDecimal valorDaVariacao = cotacao.multiply(variacao);
        BigDecimal novaCotacao = cotacao.add(valorDaVariacao);
        novaCotacao = novaCotacao.setScale(2, RoundingMode.HALF_UP);
        BigDecimal minimo = cotacao.multiply(new BigDecimal("0.95"));
        BigDecimal maximo = cotacao.multiply(new BigDecimal("1.05"));

        if (novaCotacao.compareTo(minimo) >= 0 && novaCotacao.compareTo(maximo) <= 0) {
            cotacao = novaCotacao;
        }
    }

    /** Calcula o valor da taxa em reais. */
    protected BigDecimal calcularTaxa(BigDecimal valor, BigDecimal aliquota) {
        Validacao.validarValorNaoNegativo(valor, "O valor da operação");
        BigDecimal taxa = valor.multiply(aliquota);
        return taxa.setScale(2, RoundingMode.HALF_UP);
    }
}
