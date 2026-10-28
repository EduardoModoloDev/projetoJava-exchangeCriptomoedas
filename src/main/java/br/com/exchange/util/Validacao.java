package br.com.exchange.util;

import java.math.BigDecimal;

/**
 * Valida CPF, senha e valores.
 */
public final class Validacao {

    private Validacao() {
    }

    /** Confere os 11 números do CPF, sem calcular os dígitos verificadores. */
    public static void validarCpf(String cpf) {
        if (cpf == null || cpf.length() != 11 || !somenteNumeros(cpf)) {
            throw new IllegalArgumentException("O CPF deve conter exatamente 11 dígitos numéricos, sem pontuação.");
        }
    }

    /** A senha deve ter 6 números. */
    public static void validarSenha(String senha) {
        if (senha == null || senha.length() != 6 || !somenteNumeros(senha)) {
            throw new IllegalArgumentException("A senha deve conter exatamente 6 dígitos numéricos.");
        }
    }

    /** O valor deve ser informado e não pode ser negativo. */
    public static void validarValorNaoNegativo(BigDecimal valor, String campo) {
        if (valor == null || valor.signum() < 0) {
            throw new IllegalArgumentException(campo + " deve ser informado e não pode ser negativo.");
        }
    }

    private static boolean somenteNumeros(String texto) {
        for (int i = 0; i < texto.length(); i++) {
            char caractere = texto.charAt(i);
            if (caractere < '0' || caractere > '9') {
                return false;
            }
        }
        return true;
    }
}
