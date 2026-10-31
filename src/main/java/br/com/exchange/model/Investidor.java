package br.com.exchange.model;

/**
 * Investidor e sua carteira.
 */
public class Investidor extends Pessoa {

    private final int id;
    private final Carteira carteira;

    /** Cria o investidor com a carteira zerada. */
    public Investidor(String nome, String cpf, String senha) {
        this(0, nome, cpf, senha, new Carteira());
    }

    /** Carrega os dados do investidor que já existe no banco. */
    public Investidor(int id, String nome, String cpf, String senha, Carteira carteira) {
        super(nome, cpf, senha);
        if (id < 0 || carteira == null) {
            throw new IllegalArgumentException("Informe um ID válido e uma carteira.");
        }
        this.id = id;
        this.carteira = carteira;
    }

    public int getId() {
        return id;
    }

    public Carteira getCarteira() {
        return carteira;
    }

    /** Confere a senha do investidor. */
    public boolean verificarSenha(String senhaInformada) {
        return getSenha().equals(senhaInformada);
    }
}
