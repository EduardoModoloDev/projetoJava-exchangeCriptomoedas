package br.com.exchange.model;

import java.math.BigDecimal;

/**
 * Ethereum: 1% de taxa na compra e 2% na venda.
 */
public class Ethereum extends Moedas implements Tarifacao {

    public static final BigDecimal ALIQUOTA_COMPRA = new BigDecimal("0.01");
    public static final BigDecimal ALIQUOTA_VENDA = new BigDecimal("0.02");

    public Ethereum() {
        this(new BigDecimal("16000.00"));
    }

    public Ethereum(BigDecimal cotacao) {
        super("Ethereum", "ETH", cotacao);
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
