package br.com.exchange.model;

import java.math.BigDecimal;

/**
 * Bitcoin: 2% de taxa na compra e 3% na venda.
 */
public class Bitcoin extends Moedas implements Tarifacao {

    public static final BigDecimal ALIQUOTA_COMPRA = new BigDecimal("0.02");
    public static final BigDecimal ALIQUOTA_VENDA = new BigDecimal("0.03");

    public Bitcoin() {
        this(new BigDecimal("350000.00"));
    }

    public Bitcoin(BigDecimal cotacao) {
        super("Bitcoin", "BTC", cotacao);
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
