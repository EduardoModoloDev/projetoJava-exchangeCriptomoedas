package br.com.exchange.controller;

import br.com.exchange.dao.CarteiraDAO;
import br.com.exchange.dao.ConnectionFactory;
import br.com.exchange.dao.DaoException;
import br.com.exchange.dao.InvestidorDAO;
import br.com.exchange.dao.OperacaoDAO;
import br.com.exchange.model.Carteira;
import br.com.exchange.model.Investidor;
import br.com.exchange.model.Operacao;
import br.com.exchange.util.Validacao;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;

/** Login, consultas e movimentações em reais. */
public class InvestidorController {

    private final Supplier<Connection> conexoes;
    private final InvestidorDAO investidores = new InvestidorDAO();
    private final CarteiraDAO carteiras = new CarteiraDAO();
    private final OperacaoDAO operacoes = new OperacaoDAO();

    public InvestidorController() {
        this(ConnectionFactory::getConnection);
    }

    /** Permite abrir conexões com o banco de testes. */
    public InvestidorController(Supplier<Connection> conexoes) {
        if (conexoes == null) {
            throw new IllegalArgumentException("Informe como abrir a conexão com o banco.");
        }
        this.conexoes = conexoes;
    }

    /** Devolve o investidor quando o CPF e a senha conferem. */
    public Investidor autenticar(String cpf, String senha) {
        Validacao.validarCpf(cpf);
        Validacao.validarSenha(senha);
        try (Connection conexao = conexoes.get()) {
            Investidor investidor = investidores.buscarPorCpf(conexao, cpf);
            if (investidor == null || !investidor.verificarSenha(senha)) {
                throw new IllegalArgumentException("CPF ou senha inválidos.");
            }
            return investidor;
        } catch (SQLException | DaoException e) {
            throw new IllegalArgumentException("Não foi possível fazer o login. Confira a conexão com o banco.", e);
        }
    }

    /** Lança Senha inválida quando a confirmação não confere. */
    public void validarSenha(long investidorId, String senha) {
        buscarComSenha(investidorId, senha);
    }

    /** Devolve nome, CPF e carteira com os saldos atuais do banco. */
    public Investidor consultarSaldo(long investidorId, String senha) {
        return buscarComSenha(investidorId, senha);
    }

    /** Confirma a senha e devolve as operações em ordem de data. */
    public List<Operacao> consultarExtrato(long investidorId, String senha) {
        return buscarComSenha(investidorId, senha).getCarteira().getExtrato();
    }

    /** Deposita reais e devolve a carteira atualizada. */
    public Carteira depositar(long investidorId, BigDecimal valor) {
        return movimentarReais(investidorId, valor, true);
    }

    /** Saca reais se houver saldo e devolve a carteira atualizada. */
    public Carteira sacar(long investidorId, BigDecimal valor) {
        return movimentarReais(investidorId, valor, false);
    }

    private Investidor buscarComSenha(long investidorId, String senha) {
        int id = Validacao.validarInvestidorId(investidorId);
        try {
            Validacao.validarSenha(senha);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Senha inválida");
        }
        try (Connection conexao = conexoes.get()) {
            Investidor investidor = investidores.buscarPorId(conexao, id);
            if (investidor == null) {
                throw new IllegalArgumentException("Investidor não encontrado.");
            }
            if (!investidor.verificarSenha(senha)) {
                throw new IllegalArgumentException("Senha inválida");
            }
            return investidor;
        } catch (SQLException | DaoException e) {
            throw new IllegalArgumentException("Não foi possível consultar o investidor. Confira a conexão com o banco.", e);
        }
    }

    private Carteira movimentarReais(long investidorId, BigDecimal valor, boolean deposito) {
        int id = Validacao.validarInvestidorId(investidorId);
        BigDecimal valorAjustado = Validacao.validarValorPositivo(valor);
        try (Connection conexao = conexoes.get()) {
            conexao.setAutoCommit(false);
            try {
                Carteira carteira = carteiras.buscarParaAtualizar(conexao, id);
                if (carteira == null) {
                    throw new IllegalArgumentException("Investidor não encontrado.");
                }
                char sinal;
                if (deposito) {
                    carteira.adicionarSaldo("REAL", valorAjustado);
                    sinal = '+';
                } else {
                    carteira.removerSaldo("REAL", valorAjustado);
                    sinal = '-';
                }
                Operacao operacao = new Operacao(LocalDateTime.now(), sinal, valorAjustado,
                        "REAL", BigDecimal.ZERO, BigDecimal.ZERO, carteira);
                carteiras.atualizarSaldos(conexao, id, carteira);
                operacoes.inserir(conexao, operacao, id);
                for (Operacao anterior : operacoes.listarPorInvestidor(conexao, id)) {
                    carteira.adicionarOperacao(anterior);
                }
                conexao.commit();
                return carteira;
            } catch (SQLException | RuntimeException e) {
                desfazer(conexao, e);
                throw e;
            }
        } catch (SQLException | DaoException e) {
            throw new IllegalArgumentException("Não foi possível concluir a movimentação em reais. Confira a conexão com o banco.", e);
        }
    }

    private void desfazer(Connection conexao, Exception erro) {
        try {
            conexao.rollback();
        } catch (SQLException falha) {
            erro.addSuppressed(falha);
        }
    }
}
