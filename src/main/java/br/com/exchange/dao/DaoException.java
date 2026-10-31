package br.com.exchange.dao;

/** Erros de acesso ao banco de dados. */
public class DaoException extends RuntimeException {

    public DaoException(String mensagem) {
        super(mensagem);
    }

    public DaoException(String mensagem, Throwable causa) {
        super(mensagem + " " + causa.getMessage(), causa);
    }
}
