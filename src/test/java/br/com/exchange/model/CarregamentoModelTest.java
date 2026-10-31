package br.com.exchange.model;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CarregamentoModelTest {

    @Test
    void deveCarregarIdECarteiraDoInvestidor() {
        Carteira carteira = new Carteira(new BigDecimal("150.50"), new BigDecimal("0.12345678"),
                new BigDecimal("2"), BigDecimal.ZERO);
        Investidor investidor = new Investidor(7, "Ana", "01234567890", "012345", carteira);

        assertEquals(7, investidor.getId());
        assertSame(carteira, investidor.getCarteira());
        assertEquals(new BigDecimal("150.50"), carteira.getSaldoReais());
        assertEquals(new BigDecimal("0.12345678"), carteira.getSaldoBitcoin());
        assertEquals(new BigDecimal("2.00000000"), carteira.getSaldoEthereum());
        assertEquals(new BigDecimal("0.00000000"), carteira.getSaldoRipple());
        assertTrue(investidor.verificarSenha("012345"));
    }

    @Test
    void novoInvestidorDeveComecarSemIdDoBanco() {
        Investidor investidor = new Investidor("Ana", "01234567890", "012345");

        assertEquals(0, investidor.getId());
    }

    @Test
    void deveRecusarSaldosNegativosOuNulosAoCarregar() {
        BigDecimal zero = BigDecimal.ZERO;
        BigDecimal negativo = new BigDecimal("-0.000000001");

        assertThrows(IllegalArgumentException.class, () -> new Carteira(negativo, zero, zero, zero));
        assertThrows(IllegalArgumentException.class, () -> new Carteira(zero, negativo, zero, zero));
        assertThrows(IllegalArgumentException.class, () -> new Carteira(zero, zero, negativo, zero));
        assertThrows(IllegalArgumentException.class, () -> new Carteira(zero, zero, zero, negativo));
        assertThrows(IllegalArgumentException.class, () -> new Carteira(null, zero, zero, zero));
        assertThrows(IllegalArgumentException.class, () -> new Carteira(zero, null, zero, zero));
        assertThrows(IllegalArgumentException.class, () -> new Carteira(zero, zero, null, zero));
        assertThrows(IllegalArgumentException.class, () -> new Carteira(zero, zero, zero, null));
    }

    @Test
    void deveRecusarIdNegativoOuCarteiraAusente() {
        assertThrows(IllegalArgumentException.class,
                () -> new Investidor(-1, "Ana", "01234567890", "012345", new Carteira()));
        assertThrows(IllegalArgumentException.class,
                () -> new Investidor(1, "Ana", "01234567890", "012345", null));
    }
}
