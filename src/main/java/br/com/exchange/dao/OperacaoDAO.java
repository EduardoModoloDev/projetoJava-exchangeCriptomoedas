package br.com.exchange.dao;

import br.com.exchange.model.Carteira;
import br.com.exchange.model.Operacao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/** Salva e consulta as operações do extrato. */
public class OperacaoDAO {

    public int inserir(Operacao operacao, int investidorId) {
        try (Connection conexao = ConnectionFactory.getConnection()) {
            return inserir(conexao, operacao, investidorId);
        } catch (SQLException e) {
            throw new DaoException("Não foi possível salvar a operação.", e);
        }
    }

    /** Não fecha nem confirma a conexão recebida. */
    public int inserir(Connection conexao, Operacao operacao, int investidorId) {
        String sql = "INSERT INTO operacao (investidor_id, data_hora, sinal, valor, moeda, "
                + "cotacao, taxa, saldo_real, saldo_btc, saldo_eth, saldo_xrp) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement comando = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            comando.setInt(1, investidorId);
            comando.setTimestamp(2, Timestamp.valueOf(operacao.getDataHora()));
            comando.setString(3, String.valueOf(operacao.getSinal()));
            comando.setBigDecimal(4, operacao.getValor());
            comando.setString(5, operacao.getSiglaMoeda());
            comando.setBigDecimal(6, operacao.getCotacao());
            comando.setBigDecimal(7, operacao.getTaxa());
            comando.setBigDecimal(8, operacao.getSaldoReais());
            comando.setBigDecimal(9, operacao.getSaldoBitcoin());
            comando.setBigDecimal(10, operacao.getSaldoEthereum());
            comando.setBigDecimal(11, operacao.getSaldoRipple());
            comando.executeUpdate();
            try (ResultSet resultado = comando.getGeneratedKeys()) {
                if (!resultado.next()) {
                    throw new DaoException("O banco não retornou o ID da operação.");
                }
                return resultado.getInt(1);
            }
        } catch (SQLException e) {
            throw new DaoException("Não foi possível salvar a operação.", e);
        }
    }

    public List<Operacao> listarPorInvestidor(int investidorId) {
        try (Connection conexao = ConnectionFactory.getConnection()) {
            return listarPorInvestidor(conexao, investidorId);
        } catch (SQLException e) {
            throw new DaoException("Não foi possível consultar o extrato.", e);
        }
    }

    public List<Operacao> listarPorInvestidor(Connection conexao, int investidorId) {
        String sql = "SELECT data_hora, sinal, valor, moeda, cotacao, taxa, "
                + "saldo_real, saldo_btc, saldo_eth, saldo_xrp FROM operacao "
                + "WHERE investidor_id = ? ORDER BY data_hora, id";
        List<Operacao> operacoes = new ArrayList<>();
        try (PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, investidorId);
            try (ResultSet resultado = comando.executeQuery()) {
                while (resultado.next()) {
                    Carteira saldos = new Carteira(resultado.getBigDecimal("saldo_real"),
                            resultado.getBigDecimal("saldo_btc"), resultado.getBigDecimal("saldo_eth"),
                            resultado.getBigDecimal("saldo_xrp"));
                    Operacao operacao = new Operacao(resultado.getTimestamp("data_hora").toLocalDateTime(),
                            resultado.getString("sinal").charAt(0), resultado.getBigDecimal("valor"),
                            resultado.getString("moeda"), resultado.getBigDecimal("cotacao"),
                            resultado.getBigDecimal("taxa"), saldos);
                    operacoes.add(operacao);
                }
            }
            return operacoes;
        } catch (SQLException e) {
            throw new DaoException("Não foi possível consultar o extrato.", e);
        }
    }
}
