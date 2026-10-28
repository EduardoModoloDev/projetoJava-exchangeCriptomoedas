package br.com.exchange.model;

import br.com.exchange.util.Validacao;

/**
 * Dados básicos de uma pessoa.
 */
public abstract class Pessoa {

    private String nome;
    private String cpf;
    private String senha;

    protected Pessoa(String nome, String cpf, String senha) {
        setNome(nome);
        setCpf(cpf);
        setSenha(senha);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome deve ser informado.");
        }
        this.nome = nome.trim();
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        Validacao.validarCpf(cpf);
        this.cpf = cpf;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        Validacao.validarSenha(senha);
        this.senha = senha;
    }
}
