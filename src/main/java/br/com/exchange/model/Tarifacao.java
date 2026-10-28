package br.com.exchange.model;

import java.math.BigDecimal;

/**
 * Taxas de compra e venda das criptomoedas.
 */
public interface Tarifacao {

    /** Calcula a taxa de compra sobre um valor em reais. */
    BigDecimal calcularTaxaCompra(BigDecimal valor);

    /** Calcula a taxa de venda sobre um valor em reais. */
    BigDecimal calcularTaxaVenda(BigDecimal valor);
}
