package br.com.exchange.model;

import br.com.exchange.util.Validacao;
import java.math.BigDecimal;
import java.math.RoundingMode;

/** Calcula uma compra ou venda sem acessar o banco. */
public final class CalculoNegociacao {

    private final BigDecimal valor;
    private final BigDecimal valorTaxa;
    private final BigDecimal totalEmReais;
    private final BigDecimal quantidade;
    private final BigDecimal aliquota;

    private CalculoNegociacao(Moedas moeda, BigDecimal valorEmReais, boolean compra) {
        if (!(moeda instanceof Tarifacao)) {
            throw new IllegalArgumentException("Moeda inválida. Use BTC, ETH ou XRP.");
        }
        valor = Validacao.validarValorPositivo(valorEmReais);
        Tarifacao tarifas = (Tarifacao) moeda;
        if (compra) {
            aliquota = tarifas.getAliquotaCompra();
            valorTaxa = tarifas.calcularTaxaCompra(valor);
            totalEmReais = valor.add(valorTaxa);
        } else {
            aliquota = tarifas.getAliquotaVenda();
            valorTaxa = tarifas.calcularTaxaVenda(valor);
            totalEmReais = valor.subtract(valorTaxa);
        }
        quantidade = valor.divide(moeda.getCotacao(), 8, RoundingMode.HALF_UP);
        if (quantidade.signum() == 0) {
            throw new IllegalArgumentException("O valor é muito pequeno para a cotação desta moeda.");
        }
    }

    /**
     * Soma a taxa ao valor pago em reais.
     *
     * @param moeda criptomoeda usada na negociação
     * @param valorEmReais valor da negociação em reais, antes da taxa
     * @return cálculo da compra com taxa, total e quantidade
     */
    public static CalculoNegociacao compra(Moedas moeda, BigDecimal valorEmReais) {
        return new CalculoNegociacao(moeda, valorEmReais, true);
    }

    /**
     * Desconta a taxa do valor recebido em reais.
     *
     * @param moeda criptomoeda usada na negociação
     * @param valorEmReais valor da negociação em reais, antes da taxa
     * @return cálculo da venda com taxa, total e quantidade
     */
    public static CalculoNegociacao venda(Moedas moeda, BigDecimal valorEmReais) {
        return new CalculoNegociacao(moeda, valorEmReais, false);
    }

    /**
     * Consulta o valor da operação em reais.
     *
     * @return valor em reais antes da taxa
     */
    public BigDecimal getValor() {
        return valor;
    }

    /**
     * Consulta o valor da taxa.
     *
     * @return valor da taxa em reais
     */
    public BigDecimal getValorTaxa() {
        return valorTaxa;
    }

    /**
     * Consulta o valor total movimentado em reais.
     *
     * @return valor total debitado na compra ou creditado na venda
     */
    public BigDecimal getTotalEmReais() {
        return totalEmReais;
    }

    /**
     * Consulta a quantidade de criptomoeda negociada.
     *
     * @return quantidade de criptomoeda com oito casas decimais
     */
    public BigDecimal getQuantidade() {
        return quantidade;
    }

    /**
     * Consulta a taxa decimal usada no cálculo.
     *
     * @return alíquota decimal usada no cálculo
     */
    public BigDecimal getAliquota() {
        return aliquota;
    }
}
