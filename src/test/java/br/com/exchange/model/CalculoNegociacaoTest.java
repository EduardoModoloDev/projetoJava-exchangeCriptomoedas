package br.com.exchange.model;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class CalculoNegociacaoTest {

    @Test
    void compraDeBitcoinDeveSomarDoisReaisDeTaxa() {
        CalculoNegociacao calculo = CalculoNegociacao.compra(new Bitcoin(), new BigDecimal("100.00"));

        assertEquals(new BigDecimal("2.00"), calculo.getValorTaxa());
        assertEquals(new BigDecimal("102.00"), calculo.getTotalEmReais());
        assertEquals(new BigDecimal("0.02"), calculo.getAliquota());
        assertEquals(new BigDecimal("0.00028571"), calculo.getQuantidade());
    }

    @Test
    void vendaDeBitcoinDeveDescontarTresReaisDeTaxa() {
        CalculoNegociacao calculo = CalculoNegociacao.venda(new Bitcoin(), new BigDecimal("100.00"));

        assertEquals(new BigDecimal("3.00"), calculo.getValorTaxa());
        assertEquals(new BigDecimal("97.00"), calculo.getTotalEmReais());
        assertEquals(new BigDecimal("0.03"), calculo.getAliquota());
        assertEquals(new BigDecimal("0.00028571"), calculo.getQuantidade());
    }

    @Test
    void rippleDeveCobrarUmRealNosDoisCasos() {
        CalculoNegociacao compra = CalculoNegociacao.compra(new Ripple(), new BigDecimal("100.00"));
        CalculoNegociacao venda = CalculoNegociacao.venda(new Ripple(), new BigDecimal("100.00"));

        assertEquals(new BigDecimal("1.00"), compra.getValorTaxa());
        assertEquals(new BigDecimal("1.00"), venda.getValorTaxa());
        assertEquals(new BigDecimal("101.00"), compra.getTotalEmReais());
        assertEquals(new BigDecimal("99.00"), venda.getTotalEmReais());
        assertEquals(new BigDecimal("40.00000000"), compra.getQuantidade());
        assertEquals(compra.getQuantidade(), venda.getQuantidade());
    }

    @Test
    void ethereumDeveUsarTaxasDeUmEDoisPorCento() {
        CalculoNegociacao compra = CalculoNegociacao.compra(new Ethereum(), new BigDecimal("100.00"));
        CalculoNegociacao venda = CalculoNegociacao.venda(new Ethereum(), new BigDecimal("100.00"));

        assertEquals(new BigDecimal("1.00"), compra.getValorTaxa());
        assertEquals(new BigDecimal("2.00"), venda.getValorTaxa());
        assertEquals(new BigDecimal("101.00"), compra.getTotalEmReais());
        assertEquals(new BigDecimal("98.00"), venda.getTotalEmReais());
        assertEquals(new BigDecimal("0.01"), compra.getAliquota());
        assertEquals(new BigDecimal("0.02"), venda.getAliquota());
    }

    @Test
    void deveAjustarCentavosAntesDeCalcularTaxasEQuantidade() {
        CalculoNegociacao calculo = CalculoNegociacao.compra(new Ripple(new BigDecimal("3.00")),
                new BigDecimal("10.005"));

        assertEquals(new BigDecimal("10.01"), calculo.getValor());
        assertEquals(new BigDecimal("0.10"), calculo.getValorTaxa());
        assertEquals(new BigDecimal("10.11"), calculo.getTotalEmReais());
        assertEquals(new BigDecimal("3.33666667"), calculo.getQuantidade());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "0.001"})
    void deveRecusarValorSemCentavosPositivos(String valor) {
        assertThrows(IllegalArgumentException.class,
                () -> CalculoNegociacao.compra(new Bitcoin(), new BigDecimal(valor)));
        assertThrows(IllegalArgumentException.class,
                () -> CalculoNegociacao.venda(new Bitcoin(), new BigDecimal(valor)));
    }

    @Test
    void deveRecusarValorNuloOuMoedaSemTaxa() {
        assertThrows(IllegalArgumentException.class, () -> CalculoNegociacao.compra(new Bitcoin(), null));
        assertThrows(IllegalArgumentException.class, () -> CalculoNegociacao.venda(new Bitcoin(), null));
        assertThrows(IllegalArgumentException.class, () -> CalculoNegociacao.compra(new Real(), BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class, () -> CalculoNegociacao.venda(null, BigDecimal.ONE));
    }

    @Test
    void deveRecusarQuantidadeQueArredondaParaZero() {
        Bitcoin bitcoin = new Bitcoin(new BigDecimal("100000000.00"));

        assertThrows(IllegalArgumentException.class,
                () -> CalculoNegociacao.compra(bitcoin, new BigDecimal("0.01")));
        assertThrows(IllegalArgumentException.class,
                () -> CalculoNegociacao.venda(bitcoin, new BigDecimal("0.01")));
    }
}
