package br.com.exchange.model;

import java.math.BigDecimal;

/**
 * Taxas de compra e venda das criptomoedas.
 */
public interface Tarifacao {

    /**
     * Consulta a alíquota de compra.
     * return alíquota decimal da compra
     */
    BigDecimal getAliquotaCompra();

    /**
     * Consulta a alíquota de venda.
     * return alíquota decimal da venda
     */
    BigDecimal getAliquotaVenda();

    /**
     * Calcula a taxa de compra em reais.
     * param valor valor da operação em reais
     * return valor da taxa em reais
     */
    BigDecimal calcularTaxaCompra(BigDecimal valor);

    /**
     * Calcula a taxa de venda em reais.
     * param valor valor da operação em reais
     * return valor da taxa em reais
     */
    BigDecimal calcularTaxaVenda(BigDecimal valor);
}
