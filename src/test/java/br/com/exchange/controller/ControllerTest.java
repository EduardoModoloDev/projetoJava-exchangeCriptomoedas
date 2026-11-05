package br.com.exchange.controller;

import br.com.exchange.dao.DaoException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ControllerTest {

    private final Supplier<Connection> semBanco = () -> {
        throw new AssertionError("Entrada inválida não deve abrir conexão.");
    };
    private final InvestidorController investidor = new InvestidorController(semBanco);
    private final MercadoController mercado = new MercadoController(semBanco);

    @Test
    void loginDeveValidarOFormatoAntesDeAbrirConexao() {
        IllegalArgumentException cpf = assertThrows(IllegalArgumentException.class,
                () -> investidor.autenticar("111.111.111-11", "123456"));
        IllegalArgumentException senha = assertThrows(IllegalArgumentException.class,
                () -> investidor.autenticar("11111111111", "abcdef"));

        assertTrue(cpf.getMessage().contains("11 dígitos"));
        assertTrue(senha.getMessage().contains("6 dígitos"));
    }

    @Test
    void confirmacaoDeSenhaMalformadaDeveTerMensagemSimples() {
        IllegalArgumentException erro = assertThrows(IllegalArgumentException.class,
                () -> investidor.validarSenha(1, null));

        assertEquals("Senha inválida", erro.getMessage());
        assertThrows(IllegalArgumentException.class, () -> investidor.consultarSaldo(1, "12"));
        assertThrows(IllegalArgumentException.class, () -> investidor.consultarExtrato(1, "abcdef"));
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -1, 2147483648L, Long.MAX_VALUE})
    void deveRecusarIdsInvalidosSemTruncarOValor(long id) {
        assertThrows(IllegalArgumentException.class, () -> investidor.depositar(id, BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class, () -> investidor.sacar(id, BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class, () -> investidor.consultarSaldo(id, "123456"));
        assertThrows(IllegalArgumentException.class, () -> mercado.comprar(id, "XRP", BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class, () -> mercado.vender(id, "XRP", BigDecimal.ONE));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "0.001"})
    void deveRecusarValoresInvalidosAntesDeAbrirConexao(String texto) {
        BigDecimal valor = new BigDecimal(texto);

        assertThrows(IllegalArgumentException.class, () -> investidor.depositar(1, valor));
        assertThrows(IllegalArgumentException.class, () -> investidor.sacar(1, valor));
        assertThrows(IllegalArgumentException.class, () -> mercado.comprar(1, "XRP", valor));
        assertThrows(IllegalArgumentException.class, () -> mercado.vender(1, "XRP", valor));
    }

    @Test
    void deveRecusarValorNuloESiglasInvalidas() {
        assertThrows(IllegalArgumentException.class, () -> investidor.depositar(1, null));
        assertThrows(IllegalArgumentException.class, () -> investidor.sacar(1, null));
        assertThrows(IllegalArgumentException.class, () -> mercado.comprar(1, "BTC", null));
        assertThrows(IllegalArgumentException.class, () -> mercado.vender(1, "BTC", null));
        assertThrows(IllegalArgumentException.class, () -> mercado.comprar(1, "REAL", BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class, () -> mercado.vender(1, null, BigDecimal.ONE));
    }

    @Test
    void falhaDeConexaoDeveVirarMensagemParaATela() {
        Supplier<Connection> indisponivel = () -> {
            throw new DaoException("Banco indisponível.");
        };
        InvestidorController investidorSemBanco = new InvestidorController(indisponivel);
        MercadoController mercadoSemBanco = new MercadoController(indisponivel);

        IllegalArgumentException erro = assertThrows(IllegalArgumentException.class,
                () -> investidorSemBanco.autenticar("11111111111", "123456"));
        assertTrue(erro.getMessage().contains("conexão com o banco"));
        assertInstanceOf(DaoException.class, erro.getCause());
        assertThrows(IllegalArgumentException.class, () -> investidorSemBanco.validarSenha(1, "123456"));
        assertThrows(IllegalArgumentException.class, () -> investidorSemBanco.depositar(1, BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class, mercadoSemBanco::consultarCotacoes);
        assertThrows(IllegalArgumentException.class, mercadoSemBanco::atualizarCotacoes);
        assertThrows(IllegalArgumentException.class, () -> mercadoSemBanco.comprar(1, "XRP", BigDecimal.ONE));
    }
}
