package br.com.exchange.model;

/**
 * Investidor e sua carteira.
 */
public class Investidor extends Pessoa {

    private final int id;
    private final Carteira carteira;

    /**
     * Cria o investidor com a carteira zerada.
     *
     * param nome nome da pessoa ou moeda
     * param cpf CPF com 11 dígitos, sem pontuação
     * param senha senha com seis dígitos numéricos
     */
    public Investidor(String nome, String cpf, String senha) {
        this(0, nome, cpf, senha, new Carteira());
    }

    /**
     * Carrega os dados do investidor que já existe no banco.
     *
     * param id ID do investidor
     * param nome nome da pessoa ou moeda
     * param cpf CPF com 11 dígitos, sem pontuação
     * param senha senha com seis dígitos numéricos
     * param carteira carteira com os saldos atuais
     */
    public Investidor(int id, String nome, String cpf, String senha, Carteira carteira) {
        super(nome, cpf, senha);
        if (id < 0 || carteira == null) {
            throw new IllegalArgumentException("Informe um ID válido e uma carteira.");
        }
        this.id = id;
        this.carteira = carteira;
    }

    /**
     * Consulta o ID do investidor.
     *
     * return ID do investidor, ou zero antes de ser salvo
     */
    public int getId() {
        return id;
    }

    /**
     * Consulta a carteira do investidor.
     *
     * return carteira do investidor
     */
    public Carteira getCarteira() {
        return carteira;
    }

    /**
     * Confere a senha do investidor.
     *
     * param senhaInformada senha a conferir
     * return true quando a senha confere
     */
    public boolean verificarSenha(String senhaInformada) {
        return getSenha().equals(senhaInformada);
    }
}
