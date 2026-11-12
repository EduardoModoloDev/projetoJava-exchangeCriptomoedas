package br.com.exchange.dao;

/** Erros de acesso ao banco de dados. */
public class DaoException extends RuntimeException {

    /**
     * Cria uma exceção de acesso ao banco.
     *
     * param mensagem descrição do erro
     */
    public DaoException(String mensagem) {
        super(mensagem);
    }

    /**
     * Cria uma exceção de acesso ao banco.
     *
     * param mensagem descrição do erro
     * param causa erro que causou a falha
     */
    public DaoException(String mensagem, Throwable causa) {
        super(mensagem + " " + causa.getMessage(), causa);
    }
}
