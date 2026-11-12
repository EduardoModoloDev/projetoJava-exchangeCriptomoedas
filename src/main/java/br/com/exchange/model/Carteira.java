package br.com.exchange.model;

import br.com.exchange.util.Validacao;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Saldos e extrato do investidor.
 */
public class Carteira {

    private BigDecimal saldoReais;
    private BigDecimal saldoBitcoin;
    private BigDecimal saldoEthereum;
    private BigDecimal saldoRipple;
    private final List<Operacao> extrato;

    /**
     * Cria uma carteira com os saldos zerados.
     */
    public Carteira() {
        this(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    /**
     * Carrega os quatro saldos, inclusive quando estão zerados.
     *
     * param saldoReais saldo em reais
     * param saldoBitcoin saldo em Bitcoin
     * param saldoEthereum saldo em Ethereum
     * param saldoRipple saldo em Ripple
     */
    public Carteira(BigDecimal saldoReais, BigDecimal saldoBitcoin,
                    BigDecimal saldoEthereum, BigDecimal saldoRipple) {
        Validacao.validarValorNaoNegativo(saldoReais, "O saldo em reais");
        Validacao.validarValorNaoNegativo(saldoBitcoin, "O saldo em Bitcoin");
        Validacao.validarValorNaoNegativo(saldoEthereum, "O saldo em Ethereum");
        Validacao.validarValorNaoNegativo(saldoRipple, "O saldo em Ripple");
        this.saldoReais = saldoReais.setScale(2, RoundingMode.HALF_UP);
        this.saldoBitcoin = saldoBitcoin.setScale(8, RoundingMode.HALF_UP);
        this.saldoEthereum = saldoEthereum.setScale(8, RoundingMode.HALF_UP);
        this.saldoRipple = saldoRipple.setScale(8, RoundingMode.HALF_UP);
        this.extrato = new ArrayList<>();
    }

    /**
     * Consulta o saldo em reais.
     *
     * return saldo em reais com duas casas decimais
     */
    public BigDecimal getSaldoReais() {
        return saldoReais;
    }

    /**
     * Consulta o saldo em Bitcoin.
     *
     * return saldo em Bitcoin com oito casas decimais
     */
    public BigDecimal getSaldoBitcoin() {
        return saldoBitcoin;
    }

    /**
     * Consulta o saldo em Ethereum.
     *
     * return saldo em Ethereum com oito casas decimais
     */
    public BigDecimal getSaldoEthereum() {
        return saldoEthereum;
    }

    /**
     * Consulta o saldo em Ripple.
     *
     * return saldo em Ripple com oito casas decimais
     */
    public BigDecimal getSaldoRipple() {
        return saldoRipple;
    }

    /**
     * Aceita as siglas REAL, BTC, ETH e XRP.
     *
     * param sigla sigla da moeda
     * return saldo da moeda informada
     */
    public BigDecimal obterSaldo(String sigla) {
        if (sigla == null) {
            throw new IllegalArgumentException("A sigla da moeda deve ser informada.");
        }
        if (sigla.equals("REAL")) {
            return saldoReais;
        } else if (sigla.equals("BTC")) {
            return saldoBitcoin;
        } else if (sigla.equals("ETH")) {
            return saldoEthereum;
        } else if (sigla.equals("XRP")) {
            return saldoRipple;
        } else {
            throw new IllegalArgumentException("Moeda inválida. Use REAL, BTC, ETH ou XRP.");
        }
    }

    /**
     * Adiciona saldo. O registro no extrato é feito separadamente.
     *
     * param sigla sigla da moeda
     * param valor valor em reais ou unidades da moeda indicada
     */
    public void adicionarSaldo(String sigla, BigDecimal valor) {
        BigDecimal saldoAtual = obterSaldo(sigla);
        BigDecimal valorAjustado = ajustarValor(sigla, valor);
        alterarSaldo(sigla, saldoAtual.add(valorAjustado));
    }

    /**
     * Retira saldo se houver valor suficiente.
     *
     * param sigla sigla da moeda
     * param valor valor em reais ou unidades da moeda indicada
     */
    public void removerSaldo(String sigla, BigDecimal valor) {
        BigDecimal saldoAtual = obterSaldo(sigla);
        BigDecimal valorAjustado = ajustarValor(sigla, valor);
        if (valorAjustado.compareTo(saldoAtual) > 0) {
            throw new IllegalArgumentException("Saldo insuficiente em " + sigla + ".");
        }
        alterarSaldo(sigla, saldoAtual.subtract(valorAjustado));
    }

    /**
     * Acrescenta uma operação ao extrato em memória.
     *
     * param operacao operação a registrar no extrato
     */
    public void adicionarOperacao(Operacao operacao) {
        if (operacao == null) {
            throw new IllegalArgumentException("A operação deve ser informada.");
        }
        extrato.add(operacao);
    }

    /**
     * Devolve uma cópia do extrato que não pode ser alterada.
     *
     * return cópia do extrato que não pode ser alterada
     */
    public List<Operacao> getExtrato() {
        List<Operacao> copia = new ArrayList<>(extrato);
        return Collections.unmodifiableList(copia);
    }

    private BigDecimal ajustarValor(String sigla, BigDecimal valor) {
        Validacao.validarValorNaoNegativo(valor, "O valor");
        int casasDecimais;
        if (sigla.equals("REAL")) {
            casasDecimais = 2;
        } else {
            casasDecimais = 8;
        }
        BigDecimal valorAjustado = valor.setScale(casasDecimais, RoundingMode.HALF_UP);
        if (valorAjustado.signum() == 0) {
            throw new IllegalArgumentException("O valor deve ser maior que zero após o arredondamento.");
        }
        return valorAjustado;
    }

    private void alterarSaldo(String sigla, BigDecimal novoSaldo) {
        if (sigla.equals("REAL")) {
            saldoReais = novoSaldo;
        } else if (sigla.equals("BTC")) {
            saldoBitcoin = novoSaldo;
        } else if (sigla.equals("ETH")) {
            saldoEthereum = novoSaldo;
        } else if (sigla.equals("XRP")) {
            saldoRipple = novoSaldo;
        } else {
            throw new IllegalArgumentException("Moeda inválida. Use REAL, BTC, ETH ou XRP.");
        }
    }
}
