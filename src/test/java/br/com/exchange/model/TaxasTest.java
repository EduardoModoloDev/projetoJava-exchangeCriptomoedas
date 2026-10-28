package br.com.exchange.model;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TaxasTest {

    @Test
    void bitcoinDeveCobrarDoisPorCentoNaCompraETresNaVenda() {
        Bitcoin bitcoin = new Bitcoin();

        assertEquals(new BigDecimal("2.00"), bitcoin.calcularTaxaCompra(new BigDecimal("100.00")));
        assertEquals(new BigDecimal("3.00"), bitcoin.calcularTaxaVenda(new BigDecimal("100.00")));
    }

    @Test
    void ethereumDeveCobrarUmPorCentoNaCompraEDoisNaVenda() {
        Ethereum ethereum = new Ethereum();

        assertEquals(new BigDecimal("1.00"), ethereum.calcularTaxaCompra(new BigDecimal("100.00")));
        assertEquals(new BigDecimal("2.00"), ethereum.calcularTaxaVenda(new BigDecimal("100.00")));
    }

    @Test
    void rippleDeveCobrarUmPorCentoNaCompraENaVenda() {
        Ripple ripple = new Ripple();

        assertEquals(new BigDecimal("1.00"), ripple.calcularTaxaCompra(new BigDecimal("100.00")));
        assertEquals(new BigDecimal("1.00"), ripple.calcularTaxaVenda(new BigDecimal("100.00")));
    }

    @Test
    void deveArredondarMeioCentavoParaCima() {
        BigDecimal taxa = new Bitcoin().calcularTaxaCompra(new BigDecimal("0.25"));

        assertEquals(new BigDecimal("0.01"), taxa);
    }

    @Test
    void taxaSobreZeroDeveSerZero() {
        Tarifacao[] moedas = {new Bitcoin(), new Ethereum(), new Ripple()};
        for (Tarifacao moeda : moedas) {
            assertEquals(new BigDecimal("0.00"), moeda.calcularTaxaCompra(BigDecimal.ZERO));
            assertEquals(new BigDecimal("0.00"), moeda.calcularTaxaVenda(BigDecimal.ZERO));
        }
    }

    @Test
    void deveRecusarValoresNegativosOuNulos() {
        Tarifacao[] moedas = {new Bitcoin(), new Ethereum(), new Ripple()};
        for (Tarifacao moeda : moedas) {
            assertThrows(IllegalArgumentException.class,
                    () -> moeda.calcularTaxaCompra(new BigDecimal("-1.00")));
            assertThrows(IllegalArgumentException.class,
                    () -> moeda.calcularTaxaVenda(new BigDecimal("-1.00")));
            assertThrows(IllegalArgumentException.class, () -> moeda.calcularTaxaCompra(null));
            assertThrows(IllegalArgumentException.class, () -> moeda.calcularTaxaVenda(null));
        }
    }
}
