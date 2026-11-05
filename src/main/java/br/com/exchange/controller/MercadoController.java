package br.com.exchange.controller;

import br.com.exchange.dao.CarteiraDAO;
import br.com.exchange.dao.ConnectionFactory;
import br.com.exchange.dao.CotacaoDAO;
import br.com.exchange.dao.DaoException;
import br.com.exchange.dao.OperacaoDAO;
import br.com.exchange.model.CalculoNegociacao;
import br.com.exchange.model.Carteira;
import br.com.exchange.model.Moedas;
import br.com.exchange.model.Operacao;
import br.com.exchange.util.Validacao;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;

/** Cotações, compras e vendas das três criptomoedas. */
public class MercadoController {

    private final Supplier<Connection> conexoes;
    private final CotacaoDAO cotacoes = new CotacaoDAO();
    private final CarteiraDAO carteiras = new CarteiraDAO();
    private final OperacaoDAO operacoes = new OperacaoDAO();

    public MercadoController() {
        this(ConnectionFactory::getConnection);
    }

    /** Permite abrir conexões com o banco de testes. */
    public MercadoController(Supplier<Connection> conexoes) {
        if (conexoes == null) {
            throw new IllegalArgumentException("Informe como abrir a conexão com o banco.");
        }
        this.conexoes = conexoes;
    }

    /** Consulta as cotações atuais de BTC, ETH e XRP. */
    public List<Moedas> consultarCotacoes() {
        try (Connection conexao = conexoes.get()) {
            List<Moedas> moedas = cotacoes.buscarTodas(conexao);
            conferirCotacoes(moedas);
            return moedas;
        } catch (SQLException | DaoException e) {
            throw new IllegalArgumentException("Não foi possível consultar as cotações. Confira a conexão com o banco.", e);
        }
    }

    /** Varia cada cotação entre -5% e +5% e salva as três juntas. */
    public List<Moedas> atualizarCotacoes() {
        try (Connection conexao = conexoes.get()) {
            conexao.setAutoCommit(false);
            try {
                List<Moedas> moedas = cotacoes.buscarParaAtualizar(conexao);
                conferirCotacoes(moedas);
                for (Moedas moeda : moedas) {
                    moeda.atualizarCotacao();
                    cotacoes.atualizar(conexao, moeda.getSigla(), moeda.getCotacao());
                }
                conexao.commit();
                return moedas;
            } catch (SQLException | RuntimeException e) {
                desfazer(conexao, e);
                throw e;
            }
        } catch (SQLException | DaoException e) {
            throw new IllegalArgumentException("Não foi possível atualizar as cotações. Confira a conexão com o banco.", e);
        }
    }

    /**
     * Compra a moeda e devolve a carteira atualizada.
     * A tela chama InvestidorController.validarSenha() e consultarCotacoes() antes.
     */
    public Carteira comprar(long investidorId, String siglaMoeda, BigDecimal valorEmReais) {
        return negociar(investidorId, siglaMoeda, valorEmReais, true);
    }

    /**
     * Vende a moeda pelo valor em reais e devolve a carteira atualizada.
     * A tela chama InvestidorController.validarSenha() e consultarCotacoes() antes.
     */
    public Carteira vender(long investidorId, String siglaMoeda, BigDecimal valorEmReais) {
        return negociar(investidorId, siglaMoeda, valorEmReais, false);
    }

    private Carteira negociar(long investidorId, String sigla, BigDecimal valorEmReais, boolean compra) {
        int id = Validacao.validarInvestidorId(investidorId);
        if (!"BTC".equals(sigla) && !"ETH".equals(sigla) && !"XRP".equals(sigla)) {
            throw new IllegalArgumentException("Moeda inválida. Use BTC, ETH ou XRP.");
        }
        BigDecimal valor = Validacao.validarValorPositivo(valorEmReais);
        try (Connection conexao = conexoes.get()) {
            conexao.setAutoCommit(false);
            try {
                Carteira carteira = carteiras.buscarParaAtualizar(conexao, id);
                if (carteira == null) {
                    throw new IllegalArgumentException("Investidor não encontrado.");
                }
                Moedas moeda = buscarMoeda(conexao, sigla);
                CalculoNegociacao calculo;
                char sinal;
                if (compra) {
                    calculo = CalculoNegociacao.compra(moeda, valor);
                    carteira.removerSaldo("REAL", calculo.getTotalEmReais());
                    carteira.adicionarSaldo(sigla, calculo.getQuantidade());
                    sinal = '-';
                } else {
                    calculo = CalculoNegociacao.venda(moeda, valor);
                    carteira.removerSaldo(sigla, calculo.getQuantidade());
                    carteira.adicionarSaldo("REAL", calculo.getTotalEmReais());
                    sinal = '+';
                }
                Operacao operacao = new Operacao(LocalDateTime.now(), sinal, calculo.getValor(),
                        sigla, moeda.getCotacao(), calculo.getAliquota(), carteira);
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
            throw new IllegalArgumentException("Não foi possível concluir a negociação. Confira a conexão com o banco.", e);
        }
    }

    private Moedas buscarMoeda(Connection conexao, String sigla) {
        List<Moedas> moedas = cotacoes.buscarTodas(conexao);
        conferirCotacoes(moedas);
        for (Moedas moeda : moedas) {
            if (moeda.getSigla().equals(sigla)) {
                return moeda;
            }
        }
        throw new IllegalArgumentException("Cotação não encontrada para a moeda informada.");
    }

    private void conferirCotacoes(List<Moedas> moedas) {
        if (moedas.size() != 3) {
            throw new IllegalArgumentException("As cotações de BTC, ETH e XRP devem estar cadastradas.");
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
