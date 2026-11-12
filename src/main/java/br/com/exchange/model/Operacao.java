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
     *
     * param dataHora data e hora da operação
     * param sinal sinal de entrada ou saída de reais, + ou -
     * param valor valor em reais ou unidades da moeda indicada
     * param siglaMoeda sigla da criptomoeda
     * param cotacao cotação da moeda em reais
     * param taxa taxa decimal aplicada
     * param carteira carteira com os saldos atuais
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

    /**
     * Consulta a data e a hora da operação.
     *
     * return data e hora registradas
     */
    public LocalDateTime getDataHora() {
        return dataHora;
    }

    /**
     * Consulta o sinal da operação.
     *
     * return sinal registrado, + ou -
     */
    public char getSinal() {
        return sinal;
    }

    /**
     * Consulta o valor da operação em reais.
     *
     * return valor em reais antes da taxa
     */
    public BigDecimal getValor() {
        return valor;
    }

    /**
     * Consulta a moeda da operação.
     *
     * return sigla da moeda da operação
     */
    public String getSiglaMoeda() {
        return siglaMoeda;
    }

    /**
     * Consulta a cotação em reais.
     *
     * return cotação em reais com duas casas decimais
     */
    public BigDecimal getCotacao() {
        return cotacao;
    }

    /**
     * Consulta a taxa decimal da operação.
     *
     * return alíquota decimal registrada
     */
    public BigDecimal getTaxa() {
        return taxa;
    }

    /**
     * Consulta o saldo em reais.
     *
     * return saldo em reais com duas casas decimais
     */
    public BigDecimal getSaldoReais() {
        return saldoReais;
    }

    /**
     * Consulta o saldo em Bitcoin.
     *
     * return saldo em Bitcoin com oito casas decimais
     */
    public BigDecimal getSaldoBitcoin() {
        return saldoBitcoin;
    }

    /**
     * Consulta o saldo em Ethereum.
     *
     * return saldo em Ethereum com oito casas decimais
     */
    public BigDecimal getSaldoEthereum() {
        return saldoEthereum;
    }

    /**
     * Consulta o saldo em Ripple.
     *
     * return saldo em Ripple com oito casas decimais
     */
    public BigDecimal getSaldoRipple() {
        return saldoRipple;
    }

    /**
     * Formata os dados da operação em uma linha do extrato.
     *
     * return linha formatada do extrato
     */
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
