package br.com.exchange.dao;

import br.com.exchange.model.Carteira;
import br.com.exchange.model.Investidor;
import br.com.exchange.model.Moedas;
import br.com.exchange.model.Operacao;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DaoIntegracaoTest {

    private Connection conexao;
    private final InvestidorDAO investidores = new InvestidorDAO();
    private final CarteiraDAO carteiras = new CarteiraDAO();
    private final OperacaoDAO operacoes = new OperacaoDAO();
    private final CotacaoDAO cotacoes = new CotacaoDAO();

    @BeforeEach
    void prepararBanco() throws Exception {
        conexao = BancoTeste.abrirConexao();
        BancoTeste.preparar(conexao);
    }

    @AfterEach
    void fecharConexao() throws SQLException {
        if (conexao != null) {
            try (Connection encerrar = conexao) {
                if (!conexao.getAutoCommit()) {
                    conexao.rollback();
                }
            }
        }
    }

    @Test
    void deveInserirEBuscarInvestidorComCarteiraEExtrato() throws SQLException {
        Investidor novo = new Investidor("Ana", "01234567890", "012345");
        novo.getCarteira().adicionarSaldo("REAL", new BigDecimal("100.00"));
        int id = investidores.inserir(conexao, novo);
        Operacao operacao = novaOperacao(novo.getCarteira(), 12);
        operacoes.inserir(conexao, operacao, id);

        Investidor salvo = investidores.buscarPorCpf(conexao, "01234567890");

        assertEquals(id, salvo.getId());
        assertEquals("Ana", salvo.getNome());
        assertTrue(salvo.verificarSenha("012345"));
        assertEquals(new BigDecimal("100.00"), salvo.getCarteira().getSaldoReais());
        assertEquals(1, salvo.getCarteira().getExtrato().size());
        assertEquals(operacao.toString(), salvo.getCarteira().getExtrato().get(0).toString());
        assertFalse(conexao.isClosed());
    }

    @Test
    void deveRetornarNullQuandoCpfNaoExiste() {
        assertNull(investidores.buscarPorCpf(conexao, "99999999999"));
        assertNull(carteiras.buscarPorInvestidor(conexao, -1));
        assertTrue(operacoes.listarPorInvestidor(conexao, -1).isEmpty());
    }

    @Test
    void deveAtualizarOsQuatroSaldos() {
        int id = inserirInvestidor();
        Carteira carteira = new Carteira(new BigDecimal("250.25"), new BigDecimal("0.12345678"),
                new BigDecimal("2.34567891"), new BigDecimal("38.90000001"));

        carteiras.atualizarSaldos(conexao, id, carteira);
        Carteira salva = carteiras.buscarPorInvestidor(conexao, id);

        assertEquals(carteira.getSaldoReais(), salva.getSaldoReais());
        assertEquals(carteira.getSaldoBitcoin(), salva.getSaldoBitcoin());
        assertEquals(carteira.getSaldoEthereum(), salva.getSaldoEthereum());
        assertEquals(carteira.getSaldoRipple(), salva.getSaldoRipple());
    }

    @Test
    void deveListarOperacoesEmOrdemEPreservarOsSaldosHistoricos() {
        int id = inserirInvestidor();
        Carteira primeiraCarteira = new Carteira(new BigDecimal("100.00"),
                new BigDecimal("0.12345678"), BigDecimal.ZERO, BigDecimal.ZERO);
        Carteira segundaCarteira = new Carteira(new BigDecimal("200.00"),
                BigDecimal.ZERO, new BigDecimal("1.23456789"), new BigDecimal("38.9"));
        Operacao primeira = novaOperacao(primeiraCarteira, 12);
        Operacao segunda = novaOperacao(segundaCarteira, 13);
        operacoes.inserir(conexao, segunda, id);
        operacoes.inserir(conexao, primeira, id);
        operacoes.inserir(conexao, primeira, id);
        carteiras.atualizarSaldos(conexao, id, new Carteira());

        List<Operacao> extrato = operacoes.listarPorInvestidor(conexao, id);

        assertEquals(3, extrato.size());
        assertEquals(primeira.toString(), extrato.get(0).toString());
        assertEquals(primeira.toString(), extrato.get(1).toString());
        assertEquals(segunda.toString(), extrato.get(2).toString());
        assertEquals(new BigDecimal("0.12345678"), extrato.get(0).getSaldoBitcoin());
        assertEquals(new BigDecimal("1.23456789"), extrato.get(2).getSaldoEthereum());
        assertEquals(new BigDecimal("38.90000000"), extrato.get(2).getSaldoRipple());
    }

    @Test
    void deveAtualizarELerCotacoes() {
        cotacoes.atualizar(conexao, "BTC", new BigDecimal("360000.25"));
        List<Moedas> moedas = cotacoes.buscarTodas(conexao);

        assertEquals(3, moedas.size());
        assertEquals("BTC", moedas.get(0).getSigla());
        assertEquals(new BigDecimal("360000.25"), moedas.get(0).getCotacao());
        assertEquals(new BigDecimal("16000.00"), moedas.get(1).getCotacao());
        assertEquals(new BigDecimal("2.50"), moedas.get(2).getCotacao());
    }

    @Test
    void deveReverterCarteiraEExtratoQuandoUmaGravacaoFalha() throws SQLException {
        int id = inserirInvestidor();
        Carteira carteira = new Carteira(new BigDecimal("100.00"), BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO);
        conexao.setAutoCommit(false);
        carteiras.atualizarSaldos(conexao, id, carteira);
        DaoException erro = assertThrows(DaoException.class,
                () -> operacoes.inserir(conexao, novaOperacao(carteira, 12), -1));
        conexao.rollback();

        assertInstanceOf(SQLException.class, erro.getCause());
        assertEquals(new BigDecimal("0.00"), carteiras.buscarPorInvestidor(conexao, id).getSaldoReais());
        assertTrue(operacoes.listarPorInvestidor(conexao, id).isEmpty());
        assertFalse(conexao.isClosed());
    }

    @Test
    void deveConfirmarCarteiraEExtratoNaMesmaTransacao() throws SQLException {
        int id = inserirInvestidor();
        Carteira carteira = new Carteira(new BigDecimal("100.00"), BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO);
        conexao.setAutoCommit(false);
        carteiras.atualizarSaldos(conexao, id, carteira);
        operacoes.inserir(conexao, novaOperacao(carteira, 12), id);
        conexao.commit();

        try (Connection outra = BancoTeste.abrirConexao()) {
            assertEquals(new BigDecimal("100.00"), carteiras.buscarPorInvestidor(outra, id).getSaldoReais());
            assertEquals(1, operacoes.listarPorInvestidor(outra, id).size());
        }
    }

    @Test
    void deveReverterInvestidorCarteiraECotacaoSemFecharConexao() throws SQLException {
        conexao.setAutoCommit(false);
        int id = inserirInvestidor();
        cotacoes.atualizar(conexao, "XRP", new BigDecimal("3.00"));
        conexao.rollback();

        assertNull(investidores.buscarPorCpf(conexao, "01234567890"));
        assertNull(carteiras.buscarPorInvestidor(conexao, id));
        assertEquals(new BigDecimal("2.50"), cotacoes.buscarTodas(conexao).get(2).getCotacao());
        assertFalse(conexao.isClosed());
    }

    @Test
    void bancoTambemDeveRecusarSaldoNegativo() throws SQLException {
        int id = inserirInvestidor();
        String sql = "UPDATE carteira SET saldo_real = ? WHERE investidor_id = ?";
        try (PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setBigDecimal(1, new BigDecimal("-1.00"));
            comando.setInt(2, id);
            assertThrows(SQLException.class, comando::executeUpdate);
        }
        assertEquals(new BigDecimal("0.00"), carteiras.buscarPorInvestidor(conexao, id).getSaldoReais());
    }

    @Test
    void scriptsDevemSerReexecutadosSemDuplicarNemApagarDados() throws Exception {
        Investidor investidor = investidores.buscarPorCpf(conexao, "11111111111");
        Carteira carteira = new Carteira(new BigDecimal("50.00"), BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO);
        carteiras.atualizarSaldos(conexao, investidor.getId(), carteira);
        cotacoes.atualizar(conexao, "XRP", new BigDecimal("3.00"));

        BancoTeste.executarScript(conexao, "01_schema.sql");
        BancoTeste.executarScript(conexao, "02_dados_iniciais.sql");
        BancoTeste.executarScript(conexao, "02_dados_iniciais.sql");

        assertEquals(new BigDecimal("50.00"), carteiras.buscarPorInvestidor(conexao,
                investidor.getId()).getSaldoReais());
        assertEquals(new BigDecimal("3.00"), cotacoes.buscarTodas(conexao).get(2).getCotacao());
        try (PreparedStatement comando = conexao.prepareStatement("SELECT COUNT(*) FROM investidor");
             ResultSet resultado = comando.executeQuery()) {
            resultado.next();
            assertEquals(2, resultado.getInt(1));
        }
    }

    private int inserirInvestidor() {
        return investidores.inserir(conexao, new Investidor("Ana", "01234567890", "012345"));
    }

    private Operacao novaOperacao(Carteira carteira, int hora) {
        return new Operacao(LocalDateTime.of(2026, 10, 1, hora, 0), '+',
                new BigDecimal("100.00"), "REAL", BigDecimal.ZERO, BigDecimal.ZERO, carteira);
    }

}
