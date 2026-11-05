package br.com.exchange.dao;

import br.com.exchange.model.Bitcoin;
import br.com.exchange.model.Ethereum;
import br.com.exchange.model.Moedas;
import br.com.exchange.model.Ripple;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Consulta e salva as cotações das criptomoedas. */
public class CotacaoDAO {

    public List<Moedas> buscarTodas() {
        try (Connection conexao = ConnectionFactory.getConnection()) {
            return buscarTodas(conexao);
        } catch (SQLException e) {
            throw new DaoException("Não foi possível consultar as cotações.", e);
        }
    }

    public List<Moedas> buscarTodas(Connection conexao) {
        return buscar(conexao, false);
    }

    /** Mantém as três cotações reservadas até o fim da atualização. */
    public List<Moedas> buscarParaAtualizar(Connection conexao) {
        return buscar(conexao, true);
    }

    private List<Moedas> buscar(Connection conexao, boolean bloquear) {
        String sql = "SELECT moeda, valor FROM cotacao ORDER BY moeda";
        if (bloquear) {
            sql += " FOR UPDATE";
        }
        List<Moedas> moedas = new ArrayList<>();
        try (PreparedStatement comando = conexao.prepareStatement(sql);
             ResultSet resultado = comando.executeQuery()) {
            while (resultado.next()) {
                String sigla = resultado.getString("moeda");
                BigDecimal valor = resultado.getBigDecimal("valor");
                if (sigla.equals("BTC")) {
                    moedas.add(new Bitcoin(valor));
                } else if (sigla.equals("ETH")) {
                    moedas.add(new Ethereum(valor));
                } else if (sigla.equals("XRP")) {
                    moedas.add(new Ripple(valor));
                }
            }
            return moedas;
        } catch (SQLException e) {
            throw new DaoException("Não foi possível consultar as cotações.", e);
        }
    }

    public void atualizar(String sigla, BigDecimal novoValor) {
        try (Connection conexao = ConnectionFactory.getConnection()) {
            atualizar(conexao, sigla, novoValor);
        } catch (SQLException e) {
            throw new DaoException("Não foi possível atualizar a cotação.", e);
        }
    }

    /** Salva o valor recebido, sem calcular variação ou taxas. */
    public void atualizar(Connection conexao, String sigla, BigDecimal novoValor) {
        String sql = "UPDATE cotacao SET valor = ? WHERE moeda = ?";
        try (PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setBigDecimal(1, novoValor);
            comando.setString(2, sigla);
            if (comando.executeUpdate() != 1) {
                throw new DaoException("Cotação não encontrada para a moeda informada.");
            }
        } catch (SQLException e) {
            throw new DaoException("Não foi possível atualizar a cotação.", e);
        }
    }
}
