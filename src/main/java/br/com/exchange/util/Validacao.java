package br.com.exchange.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Valida CPF, senha e valores.
 */
public final class Validacao {

    private Validacao() {
    }

    /**
     * Confere os 11 números do CPF, sem calcular os dígitos verificadores.
     *
     * @param cpf CPF com 11 dígitos, sem pontuação
     */
    public static void validarCpf(String cpf) {
        if (cpf == null || cpf.length() != 11 || !somenteNumeros(cpf)) {
            throw new IllegalArgumentException("O CPF deve conter exatamente 11 dígitos numéricos, sem pontuação.");
        }
    }

    /**
     * A senha deve ter 6 números.
     *
     * @param senha senha com seis dígitos numéricos
     */
    public static void validarSenha(String senha) {
        if (senha == null || senha.length() != 6 || !somenteNumeros(senha)) {
            throw new IllegalArgumentException("A senha deve conter exatamente 6 dígitos numéricos.");
        }
    }

    /**
     * O valor deve ser informado e não pode ser negativo.
     *
     * @param valor valor em reais ou unidades da moeda indicada
     * @param campo nome do campo usado na mensagem de validação
     */
    public static void validarValorNaoNegativo(BigDecimal valor, String campo) {
        if (valor == null || valor.signum() < 0) {
            throw new IllegalArgumentException(campo + " deve ser informado e não pode ser negativo.");
        }
    }

    /**
     * Confere o ID antes de usar o tipo INTEGER do banco.
     *
     * @param id ID do investidor
     * @return ID convertido para o tipo INTEGER do banco
     */
    public static int validarInvestidorId(long id) {
        if (id <= 0 || id > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("O ID do investidor deve ser válido.");
        }
        return (int) id;
    }

    /**
     * Ajusta o valor em reais para centavos.
     *
     * @param valor valor em reais ou unidades da moeda indicada
     * @return valor em reais arredondado para centavos
     */
    public static BigDecimal validarValorPositivo(BigDecimal valor) {
        if (valor == null || valor.signum() <= 0) {
            throw new IllegalArgumentException("O valor deve ser maior que zero.");
        }
        BigDecimal ajustado = valor.setScale(2, RoundingMode.HALF_UP);
        if (ajustado.signum() == 0) {
            throw new IllegalArgumentException("O valor deve ser de pelo menos R$ 0,01 após o arredondamento.");
        }
        return ajustado;
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
