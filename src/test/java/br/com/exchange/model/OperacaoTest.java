package br.com.exchange.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OperacaoTest {

    private static final LocalDateTime DATA = LocalDateTime.of(2026, 10, 1, 13, 0);

    @Test
    void deveReproduzirOFormatoDoExemploDoExtrato() {
        Carteira carteira = new Carteira();
        carteira.adicionarSaldo("REAL", new BigDecimal("399.00"));
        carteira.adicionarSaldo("XRP", new BigDecimal("38.9"));
        Operacao operacao = new Operacao(DATA, '+', new BigDecimal("100.00"), "XRP",
                new BigDecimal("2.57"), new BigDecimal("0.01"), carteira);

        assertEquals("01-10-2026 13:00 + 100.00 XRP CT: 2.57 TX: 0.01 REAL: 399.00"
                + " BTC: 0.0 ETH: 0.0 XRP: 38.9", operacao.toString());
        assertEquals(DATA, operacao.getDataHora());
        assertEquals('+', operacao.getSinal());
        assertEquals("XRP", operacao.getSiglaMoeda());
        assertEquals(new BigDecimal("100.00"), operacao.getValor());
        assertEquals(new BigDecimal("2.57"), operacao.getCotacao());
        assertEquals(new BigDecimal("0.01"), operacao.getTaxa());
    }

    @Test
    void alteracoesPosterioresNaoDevemMudarOsSaldosDaOperacao() {
        Carteira carteira = new Carteira();
        carteira.adicionarSaldo("REAL", new BigDecimal("500.00"));
        carteira.adicionarSaldo("BTC", new BigDecimal("0.01"));
        carteira.adicionarSaldo("ETH", new BigDecimal("0.02"));
        carteira.adicionarSaldo("XRP", new BigDecimal("100"));
        Operacao operacao = new Operacao(DATA, '+', new BigDecimal("500.00"), "REAL",
                BigDecimal.ZERO, BigDecimal.ZERO, carteira);
        carteira.adicionarOperacao(operacao);

        carteira.removerSaldo("REAL", new BigDecimal("100.00"));
        carteira.removerSaldo("BTC", new BigDecimal("0.01"));
        carteira.removerSaldo("ETH", new BigDecimal("0.02"));
        carteira.removerSaldo("XRP", new BigDecimal("100"));

        assertEquals(new BigDecimal("500.00"), operacao.getSaldoReais());
        assertEquals(new BigDecimal("0.01000000"), operacao.getSaldoBitcoin());
        assertEquals(new BigDecimal("0.02000000"), operacao.getSaldoEthereum());
        assertEquals(new BigDecimal("100.00000000"), operacao.getSaldoRipple());
        assertSame(operacao, carteira.getExtrato().get(0));
    }

    @Test
    void extratoDevePreservarOrdemENaoPermitirAlteracoesExternas() {
        Carteira carteira = new Carteira();
        Operacao primeira = new Operacao(DATA, '+', BigDecimal.ONE, "REAL",
                BigDecimal.ZERO, BigDecimal.ZERO, carteira);
        Operacao segunda = new Operacao(DATA.plusMinutes(1), '-', BigDecimal.ONE, "REAL",
                BigDecimal.ZERO, BigDecimal.ZERO, carteira);

        carteira.adicionarOperacao(primeira);
        List<Operacao> copia = carteira.getExtrato();
        carteira.adicionarOperacao(segunda);

        assertEquals(1, copia.size());
        assertEquals(List.of(primeira, segunda), carteira.getExtrato());
        assertThrows(UnsupportedOperationException.class, copia::clear);
        assertThrows(IllegalArgumentException.class, () -> carteira.adicionarOperacao(null));
        assertEquals(2, carteira.getExtrato().size());
    }

    @Test
    void deveAceitarCotacaoETaxaZeroParaMovimentacoesEmReais() {
        Operacao saque = new Operacao(DATA, '-', new BigDecimal("10.00"), "REAL",
                BigDecimal.ZERO, BigDecimal.ZERO, new Carteira());

        assertEquals(new BigDecimal("0.00"), saque.getCotacao());
        assertEquals(new BigDecimal("0.00"), saque.getTaxa());
        assertTrue(saque.toString().startsWith("01-10-2026 13:00 - 10.00 REAL"));
    }

    @Test
    void deveRecusarDataSinalMoedaOuCarteiraInvalidos() {
        Carteira carteira = new Carteira();

        assertThrows(IllegalArgumentException.class, () -> new Operacao(null, '+', BigDecimal.ONE,
                "REAL", BigDecimal.ZERO, BigDecimal.ZERO, carteira));
        assertThrows(IllegalArgumentException.class, () -> new Operacao(DATA, '*', BigDecimal.ONE,
                "REAL", BigDecimal.ZERO, BigDecimal.ZERO, carteira));
        assertThrows(IllegalArgumentException.class, () -> new Operacao(DATA, '+', BigDecimal.ONE,
                "USD", BigDecimal.ZERO, BigDecimal.ZERO, carteira));
        assertThrows(IllegalArgumentException.class, () -> new Operacao(DATA, '+', BigDecimal.ONE,
                null, BigDecimal.ZERO, BigDecimal.ZERO, carteira));
        assertThrows(IllegalArgumentException.class, () -> new Operacao(DATA, '+', BigDecimal.ONE,
                "REAL", BigDecimal.ZERO, BigDecimal.ZERO, null));
    }

    @Test
    void deveRecusarValoresNegativosOuAusentesEAliquotaMaiorQueUm() {
        Carteira carteira = new Carteira();
        BigDecimal negativo = new BigDecimal("-1");

        assertThrows(IllegalArgumentException.class, () -> new Operacao(DATA, '+', negativo,
                "REAL", BigDecimal.ZERO, BigDecimal.ZERO, carteira));
        assertThrows(IllegalArgumentException.class, () -> new Operacao(DATA, '+', null,
                "REAL", BigDecimal.ZERO, BigDecimal.ZERO, carteira));
        assertThrows(IllegalArgumentException.class, () -> new Operacao(DATA, '+', BigDecimal.ONE,
                "REAL", negativo, BigDecimal.ZERO, carteira));
        assertThrows(IllegalArgumentException.class, () -> new Operacao(DATA, '+', BigDecimal.ONE,
                "REAL", null, BigDecimal.ZERO, carteira));
        assertThrows(IllegalArgumentException.class, () -> new Operacao(DATA, '+', BigDecimal.ONE,
                "REAL", BigDecimal.ZERO, negativo, carteira));
        assertThrows(IllegalArgumentException.class, () -> new Operacao(DATA, '+', BigDecimal.ONE,
                "REAL", BigDecimal.ZERO, null, carteira));
        assertThrows(IllegalArgumentException.class, () -> new Operacao(DATA, '+', BigDecimal.ONE,
                "REAL", BigDecimal.ZERO, new BigDecimal("1.01"), carteira));
    }
}
