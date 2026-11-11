package br.com.exchange.model;

import java.math.BigDecimal;

/**
 * Bitcoin: 2% de taxa na compra e 3% na venda.
 */
public class Bitcoin extends Moedas implements Tarifacao {

    /** Alíquota decimal de compra. */
    public static final BigDecimal ALIQUOTA_COMPRA = new BigDecimal("0.02");
    /** Alíquota decimal de venda. */
    public static final BigDecimal ALIQUOTA_VENDA = new BigDecimal("0.03");

    /**
     * Cria a moeda com a cotação padrão.
     */
    public Bitcoin() {
        this(new BigDecimal("350000.00"));
    }

    /**
     * Cria a moeda com a cotação informada.
     *
     * @param cotacao cotação da moeda em reais
     */
    public Bitcoin(BigDecimal cotacao) {
        super("Bitcoin", "BTC", cotacao);
    }

    /**
     * Consulta a alíquota de compra.
     *
     * @return alíquota decimal da compra
     */
    @Override
    public BigDecimal getAliquotaCompra() {
        return ALIQUOTA_COMPRA;
    }

    /**
     * Consulta a alíquota de venda.
     *
     * @return alíquota decimal da venda
     */
    @Override
    public BigDecimal getAliquotaVenda() {
        return ALIQUOTA_VENDA;
    }

    /**
     * Calcula a taxa de compra em reais.
     *
     * @param valor valor em reais ou unidades da moeda indicada
     * @return valor da taxa de compra em reais
     */
    @Override
    public BigDecimal calcularTaxaCompra(BigDecimal valor) {
        return calcularTaxa(valor, ALIQUOTA_COMPRA);
    }

    /**
     * Calcula a taxa de venda em reais.
     *
     * @param valor valor em reais ou unidades da moeda indicada
     * @return valor da taxa de venda em reais
     */
    @Override
    public BigDecimal calcularTaxaVenda(BigDecimal valor) {
        return calcularTaxa(valor, ALIQUOTA_VENDA);
    }
}
