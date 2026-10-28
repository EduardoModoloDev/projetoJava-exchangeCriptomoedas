package br.com.exchange.model;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class CarteiraTest {

    @Test
    void deveComecarComSaldosZeradosEExtratoVazio() {
        Carteira carteira = new Carteira();

        assertEquals(new BigDecimal("0.00"), carteira.getSaldoReais());
        assertEquals(new BigDecimal("0.00000000"), carteira.getSaldoBitcoin());
        assertEquals(new BigDecimal("0.00000000"), carteira.getSaldoEthereum());
        assertEquals(new BigDecimal("0.00000000"), carteira.getSaldoRipple());
        assertTrue(carteira.getExtrato().isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"REAL", "BTC", "ETH", "XRP"})
    void devePermitirAdicionarERemoverTodoOSaldo(String sigla) {
        Carteira carteira = new Carteira();
        carteira.adicionarSaldo(sigla, new BigDecimal("10.00"));
        carteira.removerSaldo(sigla, new BigDecimal("10.00"));

        assertEquals(0, carteira.obterSaldo(sigla).signum());
    }

    @ParameterizedTest
    @ValueSource(strings = {"REAL", "BTC", "ETH", "XRP"})
    void retiradaSemSaldoSuficienteNaoDeveAlterarSaldo(String sigla) {
        Carteira carteira = new Carteira();
        carteira.adicionarSaldo(sigla, new BigDecimal("10.00"));
        BigDecimal saldoAnterior = carteira.obterSaldo(sigla);

        assertThrows(IllegalArgumentException.class,
                () -> carteira.removerSaldo(sigla, new BigDecimal("10.01")));
        assertEquals(saldoAnterior, carteira.obterSaldo(sigla));
    }

    @Test
    void deveSomarDecimaisSemImprecisaoBinaria() {
        Carteira carteira = new Carteira();
        carteira.adicionarSaldo("REAL", new BigDecimal("0.10"));
        carteira.adicionarSaldo("REAL", new BigDecimal("0.20"));

        assertEquals(new BigDecimal("0.30"), carteira.getSaldoReais());
    }

    @Test
    void deveArredondarReaisEDiferenciarQuantidadeDeCriptomoedas() {
        Carteira carteira = new Carteira();
        carteira.adicionarSaldo("REAL", new BigDecimal("10.125"));
        carteira.adicionarSaldo("BTC", new BigDecimal("0.123456785"));
        carteira.adicionarSaldo("ETH", new BigDecimal("0.00000001"));
        carteira.adicionarSaldo("XRP", new BigDecimal("38.9"));

        assertEquals(new BigDecimal("10.13"), carteira.getSaldoReais());
        assertEquals(new BigDecimal("0.12345679"), carteira.getSaldoBitcoin());
        assertEquals(new BigDecimal("0.00000001"), carteira.getSaldoEthereum());
        assertEquals(new BigDecimal("38.90000000"), carteira.getSaldoRipple());
        assertTrue(carteira.getExtrato().isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"REAL", "BTC", "ETH", "XRP"})
    void deveRecusarValoresInvalidosSemAlterarSaldo(String sigla) {
        Carteira carteira = new Carteira();
        carteira.adicionarSaldo(sigla, BigDecimal.ONE);
        BigDecimal anterior = carteira.obterSaldo(sigla);
        BigDecimal[] invalidos = {null, BigDecimal.ZERO, new BigDecimal("-1"),
                new BigDecimal("0.000000001")};

        for (BigDecimal valor : invalidos) {
            assertThrows(IllegalArgumentException.class, () -> carteira.adicionarSaldo(sigla, valor));
            assertThrows(IllegalArgumentException.class, () -> carteira.removerSaldo(sigla, valor));
        }
        assertEquals(anterior, carteira.obterSaldo(sigla));
    }

    @Test
    void deveRecusarMoedasDesconhecidasOuNulas() {
        Carteira carteira = new Carteira();

        assertThrows(IllegalArgumentException.class, () -> carteira.obterSaldo("USD"));
        assertThrows(IllegalArgumentException.class, () -> carteira.obterSaldo(null));
        assertThrows(IllegalArgumentException.class, () -> carteira.adicionarSaldo("USD", BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class, () -> carteira.removerSaldo("USD", BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class, () -> carteira.adicionarSaldo(null, BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class, () -> carteira.removerSaldo(null, BigDecimal.ONE));
    }
}
