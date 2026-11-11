package br.com.exchange.dao;

import br.com.exchange.model.Carteira;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Consulta e atualiza os saldos da carteira. */
public class CarteiraDAO {

    /**
     * Cria o acesso aos dados.
     */
    public CarteiraDAO() {
    }

    /**
     * Consulta a carteira do investidor.
     *
     * @param investidorId ID do investidor
     * @return carteira, ou null se não existir
     */
    public Carteira buscarPorInvestidor(int investidorId) {
        try (Connection conexao = ConnectionFactory.getConnection()) {
            return buscarPorInvestidor(conexao, investidorId);
        } catch (SQLException e) {
            throw new DaoException("Não foi possível consultar a carteira.", e);
        }
    }

    /**
     * Retorna null quando o investidor não tem carteira.
     *
     * @param conexao conexão controlada por quem chamou o método
     * @param investidorId ID do investidor
     * @return carteira, ou null se não existir
     */
    public Carteira buscarPorInvestidor(Connection conexao, int investidorId) {
        return buscar(conexao, investidorId, false);
    }

    /**
     * Evita duas movimentações alterarem o mesmo saldo ao mesmo tempo.
     *
     * @param conexao conexão controlada por quem chamou o método
     * @param investidorId ID do investidor
     * @return carteira bloqueada até o fim da transação, ou null
     */
    public Carteira buscarParaAtualizar(Connection conexao, int investidorId) {
        return buscar(conexao, investidorId, true);
    }

    private Carteira buscar(Connection conexao, int investidorId, boolean bloquear) {
        String sql = "SELECT saldo_real, saldo_bitcoin, saldo_ethereum, saldo_ripple "
                + "FROM carteira WHERE investidor_id = ?";
        if (bloquear) {
            sql += " FOR UPDATE";
        }
        try (PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, investidorId);
            try (ResultSet resultado = comando.executeQuery()) {
                if (resultado.next()) {
                    return new Carteira(resultado.getBigDecimal("saldo_real"),
                            resultado.getBigDecimal("saldo_bitcoin"),
                            resultado.getBigDecimal("saldo_ethereum"),
                            resultado.getBigDecimal("saldo_ripple"));
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DaoException("Não foi possível consultar a carteira.", e);
        }
    }

    /**
     * Salva os quatro saldos da carteira.
     *
     * @param investidorId ID do investidor
     * @param carteira carteira com os saldos atuais
     */
    public void atualizarSaldos(int investidorId, Carteira carteira) {
        try (Connection conexao = ConnectionFactory.getConnection()) {
            atualizarSaldos(conexao, investidorId, carteira);
        } catch (SQLException e) {
            throw new DaoException("Não foi possível atualizar a carteira.", e);
        }
    }

    /**
     * A conexão recebida continua sob controle de quem chamou o método.
     *
     * @param conexao conexão controlada por quem chamou o método
     * @param investidorId ID do investidor
     * @param carteira carteira com os saldos atuais
     */
    public void atualizarSaldos(Connection conexao, int investidorId, Carteira carteira) {
        String sql = "UPDATE carteira SET saldo_real = ?, saldo_bitcoin = ?, "
                + "saldo_ethereum = ?, saldo_ripple = ? WHERE investidor_id = ?";
        try (PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setBigDecimal(1, carteira.getSaldoReais());
            comando.setBigDecimal(2, carteira.getSaldoBitcoin());
            comando.setBigDecimal(3, carteira.getSaldoEthereum());
            comando.setBigDecimal(4, carteira.getSaldoRipple());
            comando.setInt(5, investidorId);
            if (comando.executeUpdate() != 1) {
                throw new DaoException("Carteira não encontrada para o investidor informado.");
            }
        } catch (SQLException e) {
            throw new DaoException("Não foi possível atualizar a carteira.", e);
        }
    }
}
