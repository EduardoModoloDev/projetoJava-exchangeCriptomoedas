package br.com.exchange.model;

import java.math.BigDecimal;

/**
 * Ripple: 1% de taxa na compra e na venda.
 */
public class Ripple extends Moedas implements Tarifacao {

    public static final BigDecimal ALIQUOTA_COMPRA = new BigDecimal("0.01");
    public static final BigDecimal ALIQUOTA_VENDA = new BigDecimal("0.01");

    public Ripple() {
        this(new BigDecimal("2.50"));
    }

    public Ripple(BigDecimal cotacao) {
        super("Ripple", "XRP", cotacao);
    }

    @Override
    public BigDecimal calcularTaxaCompra(BigDecimal valor) {
        return calcularTaxa(valor, ALIQUOTA_COMPRA);
    }

    @Override
    public BigDecimal calcularTaxaVenda(BigDecimal valor) {
        return calcularTaxa(valor, ALIQUOTA_VENDA);
    }
}
