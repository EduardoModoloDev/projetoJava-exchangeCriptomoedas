package br.com.exchange.model;

import java.math.BigDecimal;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class CotacaoTest {

    @Test
    void deveUsarAsCotacoesIniciaisSugeridas() {
        assertEquals(new BigDecimal("350000.00"), new Bitcoin().getCotacao());
        assertEquals(new BigDecimal("16000.00"), new Ethereum().getCotacao());
        assertEquals(new BigDecimal("2.50"), new Ripple().getCotacao());
    }

    @Test
    void deveRespeitarOsLimitesEmMilAtualizacoesDeCadaCriptomoeda() {
        Moedas[] moedas = {new Bitcoin(), new Ethereum(), new Ripple()};
        for (Moedas moeda : moedas) {
            Random random = new Random(12345);
            for (int i = 0; i < 1000; i++) {
                BigDecimal anterior = moeda.getCotacao();
                moeda.atualizarCotacao(random);
                BigDecimal atual = moeda.getCotacao();

                assertTrue(atual.compareTo(anterior.multiply(new BigDecimal("0.95"))) >= 0);
                assertTrue(atual.compareTo(anterior.multiply(new BigDecimal("1.05"))) <= 0);
                assertTrue(atual.signum() > 0);
                assertEquals(2, atual.scale());
            }
        }
    }

    @Test
    void devePermitirVariacoesDeMenosECincoPorCentoEMaisCincoPorCento() {
        Bitcoin bitcoin = new Bitcoin(new BigDecimal("100.00"));
        // Força os resultados mínimo e máximo do sorteio.
        Random minimo = new Random() {
            @Override
            public int nextInt(int limite) {
                return 0;
            }
        };
        Random maximo = new Random() {
            @Override
            public int nextInt(int limite) {
                return limite - 1;
            }
        };

        bitcoin.atualizarCotacao(minimo);
        assertEquals(new BigDecimal("95.00"), bitcoin.getCotacao());
        bitcoin.atualizarCotacao(maximo);
        assertEquals(new BigDecimal("99.75"), bitcoin.getCotacao());
    }

    @Test
    void mesmaSementeDeveProduzirAMesmaSequencia() {
        Ripple primeira = new Ripple();
        Ripple segunda = new Ripple();
        Random primeiroRandom = new Random(42);
        Random segundoRandom = new Random(42);

        for (int i = 0; i < 100; i++) {
            primeira.atualizarCotacao(primeiroRandom);
            segunda.atualizarCotacao(segundoRandom);
            assertEquals(primeira.getCotacao(), segunda.getCotacao());
        }
    }

    @Test
    void cotacaoDeUmCentavoNaoDeveVirarZeroNemUltrapassarOsLimites() {
        Ripple ripple = new Ripple(new BigDecimal("0.01"));
        Random random = new Random(7);
        for (int i = 0; i < 1000; i++) {
            ripple.atualizarCotacao(random);
            assertEquals(new BigDecimal("0.01"), ripple.getCotacao());
        }
    }

    @Test
    void arredondamentoNaoDeveUltrapassarCincoPorCento() {
        Ripple ripple = new Ripple(new BigDecimal("2.50"));
        Random maximo = new Random() {
            @Override
            public int nextInt(int limite) {
                return limite - 1;
            }
        };

        ripple.atualizarCotacao(maximo);

        assertTrue(ripple.getCotacao().compareTo(new BigDecimal("2.625")) <= 0);
        assertTrue(ripple.getCotacao().compareTo(new BigDecimal("2.375")) >= 0);
    }

    @Test
    void deveAtualizarSemExigirUmGeradorInformado() {
        Bitcoin bitcoin = new Bitcoin(new BigDecimal("100.00"));

        bitcoin.atualizarCotacao();

        assertTrue(bitcoin.getCotacao().compareTo(new BigDecimal("95.00")) >= 0);
        assertTrue(bitcoin.getCotacao().compareTo(new BigDecimal("105.00")) <= 0);
    }

    @Test
    void realDevePermanecerFixoESemTarifacao() {
        Real real = new Real();
        Random random = new Random(42);
        for (int i = 0; i < 1000; i++) {
            real.atualizarCotacao(random);
        }
        real.atualizarCotacao();

        assertEquals(new BigDecimal("1.00"), real.getCotacao());
        assertFalse(real instanceof Tarifacao);
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "0.001"})
    void deveRecusarCotacaoNaoPositivaAposArredondamento(String texto) {
        BigDecimal cotacao = new BigDecimal(texto);
        assertThrows(IllegalArgumentException.class, () -> new Bitcoin(cotacao));
        assertThrows(IllegalArgumentException.class, () -> new Ethereum(cotacao));
        assertThrows(IllegalArgumentException.class, () -> new Ripple(cotacao));
    }

    @Test
    void deveRecusarCotacaoOuGeradorNulos() {
        assertThrows(IllegalArgumentException.class, () -> new Bitcoin(null));
        assertThrows(IllegalArgumentException.class, () -> new Ethereum(null));
        assertThrows(IllegalArgumentException.class, () -> new Ripple(null));
        assertThrows(IllegalArgumentException.class, () -> new Bitcoin().atualizarCotacao(null));
        assertThrows(IllegalArgumentException.class, () -> new Real().atualizarCotacao(null));
    }
}
