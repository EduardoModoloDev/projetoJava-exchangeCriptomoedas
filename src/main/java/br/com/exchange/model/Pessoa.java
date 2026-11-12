package br.com.exchange.model;

import br.com.exchange.util.Validacao;

/**
 * Dados básicos de uma pessoa.
 */
public abstract class Pessoa {

    private String nome;
    private String cpf;
    private String senha;

    /**
     * Cria os dados de Pessoa.
     *
     * param nome nome da pessoa ou moeda
     * param cpf CPF com 11 dígitos, sem pontuação
     * param senha senha com seis dígitos numéricos
     */
    protected Pessoa(String nome, String cpf, String senha) {
        setNome(nome);
        setCpf(cpf);
        setSenha(senha);
    }

    /**
     * Consulta o nome cadastrado.
     *
     * return nome cadastrado
     */
    public String getNome() {
        return nome;
    }

    /**
     * Altera o nome após a validação.
     *
     * param nome nome da pessoa ou moeda
     */
    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome deve ser informado.");
        }
        this.nome = nome.trim();
    }

    /**
     * Consulta o CPF cadastrado.
     *
     * return CPF cadastrado
     */
    public String getCpf() {
        return cpf;
    }

    /**
     * Altera o CPF após a validação.
     *
     * param cpf CPF com 11 dígitos, sem pontuação
     */
    public void setCpf(String cpf) {
        Validacao.validarCpf(cpf);
        this.cpf = cpf;
    }

    /**
     * Consulta a senha do investidor.
     *
     * return senha do investidor
     */
    public String getSenha() {
        return senha;
    }

    /**
     * Altera a senha após a validação.
     *
     * param senha senha com seis dígitos numéricos
     */
    public void setSenha(String senha) {
        Validacao.validarSenha(senha);
        this.senha = senha;
    }
}
