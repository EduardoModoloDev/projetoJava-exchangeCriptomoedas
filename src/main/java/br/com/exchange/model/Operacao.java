package br.com.exchange.model;

import br.com.exchange.util.Validacao;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Dados de uma operação do extrato.
 */
public final class Operacao {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    private final LocalDateTime dataHora;
    private final char sinal;
    private final BigDecimal valor;
    private final String siglaMoeda;
    private final BigDecimal cotacao;
    private final BigDecimal taxa;
    private final BigDecimal saldoReais;
    private final BigDecimal saldoBitcoin;
    private final BigDecimal saldoEthereum;
    private final BigDecimal saldoRipple;

    /**
     * Guarda os saldos da carteira após a operação.
     * O valor é em reais. A taxa é decimal: 0.01 = 1%.
     */
    public Operacao(LocalDateTime dataHora, char sinal, BigDecimal valor,
                    String siglaMoeda, BigDecimal cotacao, BigDecimal taxa, Carteira carteira) {
        if (dataHora == null) {
            throw new IllegalArgumentException("A data e a hora devem ser informadas.");
        }
        if (sinal != '+' && sinal != '-') {
            throw new IllegalArgumentException("O sinal da operação deve ser + ou -.");
        }
        if (carteira == null) {
            throw new IllegalArgumentException("A carteira deve ser informada.");
        }
        carteira.obterSaldo(siglaMoeda); // Confere se a moeda existe.
        Validacao.validarValorNaoNegativo(valor, "O valor da operação");
        Validacao.validarValorNaoNegativo(cotacao, "A cotação da operação");
        Validacao.validarValorNaoNegativo(taxa, "A taxa");
        if (taxa.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("A alíquota da taxa deve estar entre zero e um.");
        }

        this.dataHora = dataHora;
        this.sinal = sinal;
        this.valor = valor.setScale(2, RoundingMode.HALF_UP);
        this.siglaMoeda = siglaMoeda;
        this.cotacao = cotacao.setScale(2, RoundingMode.HALF_UP);
        this.taxa = taxa.setScale(2, RoundingMode.HALF_UP);
        this.saldoReais = carteira.getSaldoReais();
        this.saldoBitcoin = carteira.getSaldoBitcoin();
        this.saldoEthereum = carteira.getSaldoEthereum();
        this.saldoRipple = carteira.getSaldoRipple();
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public char getSinal() {
        return sinal;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public String getSiglaMoeda() {
        return siglaMoeda;
    }

    public BigDecimal getCotacao() {
        return cotacao;
    }

    public BigDecimal getTaxa() {
        return taxa;
    }

    public BigDecimal getSaldoReais() {
        return saldoReais;
    }

    public BigDecimal getSaldoBitcoin() {
        return saldoBitcoin;
    }

    public BigDecimal getSaldoEthereum() {
        return saldoEthereum;
    }

    public BigDecimal getSaldoRipple() {
        return saldoRipple;
    }

    @Override
    public String toString() {
        return dataHora.format(FORMATO_DATA) + " " + sinal + " " + valor.toPlainString()
                + " " + siglaMoeda + " CT: " + cotacao.toPlainString()
                + " TX: " + taxa.toPlainString() + " REAL: " + saldoReais.toPlainString()
                + " BTC: " + formatarCriptomoeda(saldoBitcoin)
                + " ETH: " + formatarCriptomoeda(saldoEthereum)
                + " XRP: " + formatarCriptomoeda(saldoRipple);
    }

    private String formatarCriptomoeda(BigDecimal saldo) {
        String texto = saldo.stripTrailingZeros().toPlainString();
        if (texto.contains(".")) {
            return texto;
        }
        return texto + ".0";
    }
}
