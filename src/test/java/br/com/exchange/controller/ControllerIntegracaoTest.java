package br.com.exchange.controller;

import br.com.exchange.dao.BancoTeste;
import br.com.exchange.dao.CotacaoDAO;
import br.com.exchange.model.Carteira;
import br.com.exchange.model.Investidor;
import br.com.exchange.model.Moedas;
import br.com.exchange.model.Operacao;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ControllerIntegracaoTest {

    private Connection conexao;
    private int id;
    private final InvestidorController investidor = new InvestidorController(BancoTeste::abrirConexao);
    private final MercadoController mercado = new MercadoController(BancoTeste::abrirConexao);

    @BeforeEach
    void prepararBanco() throws Exception {
        conexao = BancoTeste.abrirConexao();
        BancoTeste.preparar(conexao);
        id = investidor.autenticar("11111111111", "123456").getId();
    }

    @AfterEach
    void fecharConexao() throws SQLException {
        if (conexao != null) {
            conexao.close();
        }
    }

    @Test
    void deveAutenticarComOsDadosPersistidos() {
        Investidor encontrado = investidor.autenticar("11111111111", "123456");

        assertEquals(id, encontrado.getId());
        assertEquals("Investidor de Teste 1", encontrado.getNome());
        assertEquals("11111111111", encontrado.getCpf());
        assertEquals(new BigDecimal("0.00"), encontrado.getCarteira().getSaldoReais());
    }

    @Test
    void deveRecusarLoginComCpfDesconhecidoOuSenhaErrada() {
        IllegalArgumentException cpf = assertThrows(IllegalArgumentException.class,
                () -> investidor.autenticar("99999999999", "123456"));
        IllegalArgumentException senha = assertThrows(IllegalArgumentException.class,
                () -> investidor.autenticar("11111111111", "000000"));

        assertEquals("CPF ou senha inválidos.", cpf.getMessage());
        assertEquals("CPF ou senha inválidos.", senha.getMessage());
    }

    @Test
    void deveExigirSenhaCorretaParaSaldoEExtrato() {
        assertDoesNotThrow(() -> investidor.validarSenha(id, "123456"));
        IllegalArgumentException erro = assertThrows(IllegalArgumentException.class,
                () -> investidor.validarSenha(id, "000000"));
        assertEquals("Senha inválida", erro.getMessage());
        assertThrows(IllegalArgumentException.class, () -> investidor.consultarSaldo(id, "000000"));
        assertThrows(IllegalArgumentException.class, () -> investidor.consultarExtrato(id, "000000"));
        assertTrue(investidor.consultarExtrato(id, "123456").isEmpty());
    }

    @Test
    void deveConsultarOSaldoAtualizadoSemReutilizarOCarregadoNoLogin() {
        Investidor antes = investidor.autenticar("11111111111", "123456");
        investidor.depositar(id, new BigDecimal("100.00"));
        Investidor depois = investidor.consultarSaldo(id, "123456");

        assertEquals(new BigDecimal("0.00"), antes.getCarteira().getSaldoReais());
        assertEquals(new BigDecimal("100.00"), depois.getCarteira().getSaldoReais());
        assertEquals("11111111111", depois.getCpf());
        assertEquals("Investidor de Teste 1", depois.getNome());
    }

    @Test
    void depositoESaqueDevemGravarSaldosEExtrato() {
        Carteira deposito = investidor.depositar(id, new BigDecimal("100.50"));
        Carteira saque = investidor.sacar(id, new BigDecimal("30.25"));
        List<Operacao> extrato = investidor.consultarExtrato(id, "123456");

        assertEquals(new BigDecimal("100.50"), deposito.getSaldoReais());
        assertEquals(new BigDecimal("70.25"), saque.getSaldoReais());
        assertEquals(2, extrato.size());
        assertEquals(2, saque.getExtrato().size());
        assertEquals('+', extrato.get(0).getSinal());
        assertEquals('-', extrato.get(1).getSinal());
        assertEquals("REAL", extrato.get(1).getSiglaMoeda());
        assertEquals(new BigDecimal("30.25"), extrato.get(1).getValor());
        assertEquals(new BigDecimal("0.00"), extrato.get(1).getCotacao());
        assertEquals(new BigDecimal("0.00"), extrato.get(1).getTaxa());
        assertEquals(new BigDecimal("100.50"), extrato.get(0).getSaldoReais());
        assertEquals(new BigDecimal("70.25"), extrato.get(1).getSaldoReais());
        assertEquals(new BigDecimal("0.00000000"), extrato.get(1).getSaldoBitcoin());
        assertNotNull(extrato.get(1).getDataHora());
    }

    @Test
    void saqueAcimaDoSaldoNaoDeveAlterarCarteiraNemExtrato() {
        investidor.depositar(id, new BigDecimal("50.00"));

        IllegalArgumentException erro = assertThrows(IllegalArgumentException.class,
                () -> investidor.sacar(id, new BigDecimal("50.01")));

        assertEquals("Saldo insuficiente em REAL.", erro.getMessage());
        assertEquals(new BigDecimal("50.00"), saldo().getSaldoReais());
        assertEquals(1, investidor.consultarExtrato(id, "123456").size());
    }

    @Test
    void devePermitirSacarTodoOSaldo() {
        investidor.depositar(id, new BigDecimal("50.00"));

        assertEquals(new BigDecimal("0.00"), investidor.sacar(id, new BigDecimal("50.00")).getSaldoReais());
    }

    @Test
    void compraDeRippleDeveSeguirOExemploDoEnunciado() {
        investidor.depositar(id, new BigDecimal("500.00"));
        Carteira carteira = mercado.comprar(id, "XRP", new BigDecimal("100.00"));
        Operacao compra = investidor.consultarExtrato(id, "123456").get(1);

        assertEquals(new BigDecimal("399.00"), carteira.getSaldoReais());
        assertEquals(new BigDecimal("40.00000000"), carteira.getSaldoRipple());
        assertEquals('-', compra.getSinal());
        assertEquals(new BigDecimal("100.00"), compra.getValor());
        assertEquals("XRP", compra.getSiglaMoeda());
        assertEquals(new BigDecimal("2.50"), compra.getCotacao());
        assertEquals(new BigDecimal("0.01"), compra.getTaxa());
        assertEquals(carteira.getSaldoReais(), compra.getSaldoReais());
        assertEquals(carteira.getSaldoRipple(), compra.getSaldoRipple());
    }

    @ParameterizedTest
    @CsvSource({"BTC,398.00,495.00,0.02,0.03", "ETH,399.00,497.00,0.01,0.02", "XRP,399.00,498.00,0.01,0.01"})
    void compraEVendaDevemUsarAsTaxasDeCadaMoeda(String sigla, String saldoCompra,
            String saldoVenda, String taxaCompra, String taxaVenda) {
        investidor.depositar(id, new BigDecimal("500.00"));
        Carteira compra = mercado.comprar(id, sigla, new BigDecimal("100.00"));
        Carteira venda = mercado.vender(id, sigla, new BigDecimal("100.00"));
        List<Operacao> extrato = investidor.consultarExtrato(id, "123456");

        assertEquals(new BigDecimal(saldoCompra), compra.getSaldoReais());
        assertTrue(compra.obterSaldo(sigla).signum() > 0);
        assertEquals(new BigDecimal(saldoVenda), venda.getSaldoReais());
        assertEquals(new BigDecimal("0.00000000"), venda.obterSaldo(sigla));
        assertEquals(3, extrato.size());
        assertEquals(new BigDecimal(taxaCompra), extrato.get(1).getTaxa());
        assertEquals(new BigDecimal(taxaVenda), extrato.get(2).getTaxa());
        assertEquals('+', extrato.get(2).getSinal());
        assertEquals(new BigDecimal("100.00"), extrato.get(2).getValor());
        assertEquals(venda.getSaldoReais(), extrato.get(2).getSaldoReais());
    }

    @Test
    void vendaParcialDeveDescontarACriptomoedaECreditarOValorLiquido() {
        investidor.depositar(id, new BigDecimal("500.00"));
        mercado.comprar(id, "XRP", new BigDecimal("100.00"));

        Carteira venda = mercado.vender(id, "XRP", new BigDecimal("50.00"));

        assertEquals(new BigDecimal("448.50"), venda.getSaldoReais());
        assertEquals(new BigDecimal("20.00000000"), venda.getSaldoRipple());
    }

    @Test
    void compraDeveRecusarSaldoQueNaoCobreATaxa() {
        investidor.depositar(id, new BigDecimal("100.00"));

        assertThrows(IllegalArgumentException.class,
                () -> mercado.comprar(id, "BTC", new BigDecimal("100.00")));

        assertEquals(new BigDecimal("100.00"), saldo().getSaldoReais());
        assertEquals(new BigDecimal("0.00000000"), saldo().getSaldoBitcoin());
        assertEquals(1, investidor.consultarExtrato(id, "123456").size());
    }

    @Test
    void vendaSemCriptomoedaNaoDeveCreditarReaisNemRegistrarOperacao() {
        IllegalArgumentException erro = assertThrows(IllegalArgumentException.class,
                () -> mercado.vender(id, "XRP", new BigDecimal("100.00")));

        assertEquals("Saldo insuficiente em XRP.", erro.getMessage());
        assertEquals(new BigDecimal("0.00"), saldo().getSaldoReais());
        assertTrue(investidor.consultarExtrato(id, "123456").isEmpty());
    }

    @Test
    void deveUsarACotacaoAtualDoBancoNaNegociacao() {
        investidor.depositar(id, new BigDecimal("500.00"));
        new CotacaoDAO().atualizar(conexao, "XRP", new BigDecimal("5.00"));

        Carteira carteira = mercado.comprar(id, "XRP", new BigDecimal("100.00"));

        assertEquals(new BigDecimal("20.00000000"), carteira.getSaldoRipple());
        assertEquals(new BigDecimal("5.00"), investidor.consultarExtrato(id, "123456").get(1).getCotacao());
    }

    @Test
    void deveRecusarInvestidorQueNaoExiste() {
        assertThrows(IllegalArgumentException.class, () -> investidor.validarSenha(Integer.MAX_VALUE, "123456"));
        assertThrows(IllegalArgumentException.class, () -> investidor.depositar(Integer.MAX_VALUE, BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class, () -> investidor.sacar(Integer.MAX_VALUE, BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class, () -> mercado.comprar(Integer.MAX_VALUE, "XRP", BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class, () -> mercado.vender(Integer.MAX_VALUE, "XRP", BigDecimal.ONE));
    }

    @Test
    void deveConsultarEAtualizarAsTresCotacoesDentroDosLimites() {
        List<Moedas> anteriores = mercado.consultarCotacoes();
        List<Moedas> novas = mercado.atualizarCotacoes();
        List<Moedas> salvas = mercado.consultarCotacoes();

        assertEquals(3, anteriores.size());
        assertEquals(3, novas.size());
        for (int i = 0; i < novas.size(); i++) {
            BigDecimal anterior = anteriores.get(i).getCotacao();
            BigDecimal atual = novas.get(i).getCotacao();
            assertTrue(atual.compareTo(anterior.multiply(new BigDecimal("0.95"))) >= 0);
            assertTrue(atual.compareTo(anterior.multiply(new BigDecimal("1.05"))) <= 0);
            assertEquals(novas.get(i).getSigla(), salvas.get(i).getSigla());
            assertEquals(atual, salvas.get(i).getCotacao());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"deposito", "saque", "compra", "venda"})
    void falhaNoExtratoDeveDesfazerASuaMovimentacao(String tipo) throws SQLException {
        investidor.depositar(id, new BigDecimal("500.00"));
        mercado.comprar(id, "XRP", new BigDecimal("100.00"));
        Carteira antes = saldo();
        executar("ALTER TABLE operacao ADD CONSTRAINT falha_extrato_teste CHECK (valor <> 50.00) NOT VALID");
        try {
            assertThrows(IllegalArgumentException.class, () -> movimentar(tipo));
            Carteira depois = saldo();
            assertEquals(antes.getSaldoReais(), depois.getSaldoReais());
            assertEquals(antes.getSaldoRipple(), depois.getSaldoRipple());
            assertEquals(2, investidor.consultarExtrato(id, "123456").size());
        } finally {
            executar("ALTER TABLE operacao DROP CONSTRAINT falha_extrato_teste");
        }
    }

    @Test
    void falhaNaSegundaCotacaoDeveDesfazerAAtualizacaoDasTres() throws SQLException {
        List<Moedas> antes = mercado.consultarCotacoes();
        executar("ALTER TABLE cotacao ADD CONSTRAINT falha_cotacao_teste CHECK (moeda <> 'ETH') NOT VALID");
        try {
            assertThrows(IllegalArgumentException.class, mercado::atualizarCotacoes);
            List<Moedas> depois = mercado.consultarCotacoes();
            for (int i = 0; i < antes.size(); i++) {
                assertEquals(antes.get(i).getCotacao(), depois.get(i).getCotacao());
            }
        } finally {
            executar("ALTER TABLE cotacao DROP CONSTRAINT falha_cotacao_teste");
        }
    }

    private Carteira saldo() {
        return investidor.consultarSaldo(id, "123456").getCarteira();
    }

    private void executar(String sql) throws SQLException {
        try (PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.executeUpdate();
        }
    }

    private void movimentar(String tipo) {
        BigDecimal valor = new BigDecimal("50.00");
        if (tipo.equals("deposito")) {
            investidor.depositar(id, valor);
        } else if (tipo.equals("saque")) {
            investidor.sacar(id, valor);
        } else if (tipo.equals("compra")) {
            mercado.comprar(id, "XRP", valor);
        } else {
            mercado.vender(id, "XRP", valor);
        }
    }
}
