package br.com.exchange.dao;

import br.com.exchange.model.Carteira;
import br.com.exchange.model.Investidor;
import br.com.exchange.model.Operacao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/** Salva e consulta os dados do investidor. */
public class InvestidorDAO {

    /**
     * Cria o acesso aos dados.
     */
    public InvestidorDAO() {
    }

    /**
     * Salva o investidor e sua carteira juntos e retorna o ID criado.
     *
     * param investidor investidor a salvar
     * return ID criado pelo banco
     */
    public int inserir(Investidor investidor) {
        try (Connection conexao = ConnectionFactory.getConnection()) {
            conexao.setAutoCommit(false);
            try {
                int id = inserir(conexao, investidor);
                conexao.commit();
                return id;
            } catch (SQLException | RuntimeException e) {
                try {
                    conexao.rollback();
                } catch (SQLException erroRollback) {
                    e.addSuppressed(erroRollback);
                }
                throw new DaoException("Não foi possível salvar o investidor e sua carteira.", e);
            }
        } catch (SQLException e) {
            throw new DaoException("Não foi possível salvar o investidor.", e);
        }
    }

    /**
     * Quem fornece a conexão controla o commit e o rollback.
     *
     * param conexao conexão controlada por quem chamou o método
     * param investidor investidor a salvar
     * return ID criado pelo banco
     */
    public int inserir(Connection conexao, Investidor investidor) {
        String sql = "INSERT INTO investidor (nome, cpf, senha) VALUES (?, ?, ?)";
        try (PreparedStatement comando = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            comando.setString(1, investidor.getNome());
            comando.setString(2, investidor.getCpf());
            // Senha em texto só no trabalho acadêmico. Um sistema real usaria hash.
            comando.setString(3, investidor.getSenha());
            comando.executeUpdate();

            int id;
            try (ResultSet resultado = comando.getGeneratedKeys()) {
                if (!resultado.next()) {
                    throw new DaoException("O banco não retornou o ID do investidor.");
                }
                id = resultado.getInt(1);
            }

            String sqlCarteira = "INSERT INTO carteira (investidor_id, saldo_real, saldo_bitcoin, "
                    + "saldo_ethereum, saldo_ripple) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement comandoCarteira = conexao.prepareStatement(sqlCarteira)) {
                Carteira carteira = investidor.getCarteira();
                comandoCarteira.setInt(1, id);
                comandoCarteira.setBigDecimal(2, carteira.getSaldoReais());
                comandoCarteira.setBigDecimal(3, carteira.getSaldoBitcoin());
                comandoCarteira.setBigDecimal(4, carteira.getSaldoEthereum());
                comandoCarteira.setBigDecimal(5, carteira.getSaldoRipple());
                comandoCarteira.executeUpdate();
            }
            return id;
        } catch (SQLException e) {
            throw new DaoException("Não foi possível salvar o investidor.", e);
        }
    }

    /**
     * Consulta o investidor pelo CPF.
     *
     * param cpf CPF com 11 dígitos, sem pontuação
     * return investidor completo, ou null se não existir
     */
    public Investidor buscarPorCpf(String cpf) {
        try (Connection conexao = ConnectionFactory.getConnection()) {
            return buscarPorCpf(conexao, cpf);
        } catch (SQLException e) {
            throw new DaoException("Não foi possível consultar o investidor.", e);
        }
    }

    /**
     * Carrega também os saldos e o extrato. Retorna null se o CPF não existir.
     *
     * param conexao conexão controlada por quem chamou o método
     * param cpf CPF com 11 dígitos, sem pontuação
     * return investidor completo, ou null se não existir
     */
    public Investidor buscarPorCpf(Connection conexao, String cpf) {
        String sql = "SELECT id, nome, cpf, senha FROM investidor WHERE cpf = ?";
        try (PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, cpf);
            try (ResultSet resultado = comando.executeQuery()) {
                return carregarInvestidor(conexao, resultado);
            }
        } catch (SQLException e) {
            throw new DaoException("Não foi possível consultar o investidor.", e);
        }
    }

    /**
     * Consulta pelo ID usando a conexão de quem chamou.
     *
     * param conexao conexão controlada por quem chamou o método
     * param id ID do investidor
     * return investidor completo, ou null se não existir
     */
    public Investidor buscarPorId(Connection conexao, int id) {
        String sql = "SELECT id, nome, cpf, senha FROM investidor WHERE id = ?";
        try (PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, id);
            try (ResultSet resultado = comando.executeQuery()) {
                return carregarInvestidor(conexao, resultado);
            }
        } catch (SQLException e) {
            throw new DaoException("Não foi possível consultar o investidor.", e);
        }
    }

    private Investidor carregarInvestidor(Connection conexao, ResultSet resultado) throws SQLException {
        if (!resultado.next()) {
            return null;
        }
        int id = resultado.getInt("id");
        Carteira carteira = new CarteiraDAO().buscarPorInvestidor(conexao, id);
        if (carteira == null) {
            throw new DaoException("A carteira deste investidor não foi encontrada.");
        }
        for (Operacao operacao : new OperacaoDAO().listarPorInvestidor(conexao, id)) {
            carteira.adicionarOperacao(operacao);
        }
        return new Investidor(id, resultado.getString("nome"), resultado.getString("cpf"),
                resultado.getString("senha"), carteira);
    }
}
