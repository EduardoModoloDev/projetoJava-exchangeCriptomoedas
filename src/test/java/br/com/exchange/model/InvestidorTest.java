package br.com.exchange.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvestidorTest {

    @Test
    void devePreservarZerosIniciaisECriarCarteiraPropria() {
        Investidor primeiro = new Investidor("Ana", "01234567890", "012345");
        Investidor segundo = new Investidor("Bruno", "12345678900", "123456");

        assertEquals("Ana", primeiro.getNome());
        assertEquals("01234567890", primeiro.getCpf());
        assertEquals("012345", primeiro.getSenha());
        assertNotNull(primeiro.getCarteira());
        assertNotSame(primeiro.getCarteira(), segundo.getCarteira());
    }

    @Test
    void deveConferirSenhaSemAceitarSenhaIncorretaOuNula() {
        Investidor investidor = new Investidor("Ana", "01234567890", "012345");

        assertTrue(investidor.verificarSenha("012345"));
        assertFalse(investidor.verificarSenha("123456"));
        assertFalse(investidor.verificarSenha(null));
    }

    @Test
    void devePermitirAlterarDadosValidos() {
        Investidor investidor = new Investidor("Ana", "01234567890", "012345");
        investidor.setNome("Ana Silva");
        investidor.setCpf("12345678900");
        investidor.setSenha("654321");

        assertEquals("Ana Silva", investidor.getNome());
        assertEquals("12345678900", investidor.getCpf());
        assertTrue(investidor.verificarSenha("654321"));
        assertFalse(investidor.verificarSenha("012345"));
    }

    @Test
    void deveRecusarDadosInvalidosNoConstrutor() {
        assertThrows(IllegalArgumentException.class,
                () -> new Investidor("Ana", "123", "123456"));
        assertThrows(IllegalArgumentException.class,
                () -> new Investidor("Ana", "12345678900", "abc123"));
        assertThrows(IllegalArgumentException.class,
                () -> new Investidor(" ", "12345678900", "123456"));
        assertThrows(IllegalArgumentException.class,
                () -> new Investidor(null, "12345678900", "123456"));
    }

    @Test
    void settersInvalidosDevemPreservarOsDadosAnteriores() {
        Investidor investidor = new Investidor("Ana", "01234567890", "012345");

        assertThrows(IllegalArgumentException.class, () -> investidor.setCpf("123"));
        assertThrows(IllegalArgumentException.class, () -> investidor.setSenha("123"));
        assertThrows(IllegalArgumentException.class, () -> investidor.setNome(""));
        assertEquals("01234567890", investidor.getCpf());
        assertEquals("012345", investidor.getSenha());
        assertEquals("Ana", investidor.getNome());
    }
}
