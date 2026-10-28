package br.com.exchange.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidacaoTest {

    @ParameterizedTest
    @ValueSource(strings = {"12345678900", "01234567890", "00000000000"})
    void deveAceitarCpfComOnzeDigitos(String cpf) {
        assertDoesNotThrow(() -> Validacao.validarCpf(cpf));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"1234567890", "123456789000", "123.456.789-00",
            "1234567890a", " 12345678900", "１２３４５６７８９００"})
    void deveRecusarCpfForaDoFormato(String cpf) {
        assertThrows(IllegalArgumentException.class, () -> Validacao.validarCpf(cpf));
    }

    @ParameterizedTest
    @ValueSource(strings = {"123456", "012345", "000000"})
    void deveAceitarSenhaComSeisDigitos(String senha) {
        assertDoesNotThrow(() -> Validacao.validarSenha(senha));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"12345", "1234567", "12345a", "123 45", " 123456", "１２３４５６"})
    void deveRecusarSenhaForaDoFormato(String senha) {
        assertThrows(IllegalArgumentException.class, () -> Validacao.validarSenha(senha));
    }
}
