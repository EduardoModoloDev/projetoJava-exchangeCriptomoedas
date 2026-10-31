package br.com.exchange.dao;

import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class BancoTesteTest {

    @Test
    void deveAceitarSomenteONomeDoBancoDeTeste() {
        assertTrue(BancoTeste.urlDeTeste("jdbc:postgresql://localhost:5432/exchange_cripto_teste"));
        assertTrue(BancoTeste.urlDeTeste("jdbc:postgresql://localhost:5432/exchange_cripto_teste?ssl=false"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"jdbc:postgresql://localhost:5432/exchange_cripto",
            "jdbc:postgresql://localhost:5432/exchange_cripto_teste_copia",
            "jdbc:postgresql://localhost:5432/outro", "jdbc:postgresql:exchange_cripto_teste",
            "jdbc:postgresql://localhost:5432/exchange_cripto_teste/extra", "url inválida"})
    void deveRecusarBancoNormalEEnderecosInvalidos(String url) {
        assertFalse(BancoTeste.urlDeTeste(url));
    }

    @Test
    void daoExceptionDevePreservarAMensagemOriginal() {
        SQLException causa = new SQLException("Erro original de teste.");
        DaoException erro = new DaoException("Falha ao salvar.", causa);

        assertSame(causa, erro.getCause());
        assertTrue(erro.getMessage().contains("Erro original de teste."));
    }
}
