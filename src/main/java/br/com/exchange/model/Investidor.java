package br.com.exchange.model;

/**
 * Investidor e sua carteira.
 */
public class Investidor extends Pessoa {

    private final Carteira carteira;

    /** Cria o investidor com a carteira zerada. */
    public Investidor(String nome, String cpf, String senha) {
        super(nome, cpf, senha);
        carteira = new Carteira();
    }

    public Carteira getCarteira() {
        return carteira;
    }

    /** Confere a senha do investidor. */
    public boolean verificarSenha(String senhaInformada) {
        return getSenha().equals(senhaInformada);
    }
}
