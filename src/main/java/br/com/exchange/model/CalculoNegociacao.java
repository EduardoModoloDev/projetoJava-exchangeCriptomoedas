package br.com.exchange.model;

import br.com.exchange.util.Validacao;
import java.math.BigDecimal;
import java.math.RoundingMode;

/** Calcula uma compra ou venda sem acessar o banco. */
public final class CalculoNegociacao {

    private final BigDecimal valor;
    private final BigDecimal valorTaxa;
    private final BigDecimal totalEmReais;
    private final BigDecimal quantidade;
    private final BigDecimal aliquota;

    private CalculoNegociacao(Moedas moeda, BigDecimal valorEmReais, boolean compra) {
        if (!(moeda instanceof Tarifacao)) {
            throw new IllegalArgumentException("Moeda inválida. Use BTC, ETH ou XRP.");
        }
        valor = Validacao.validarValorPositivo(valorEmReais);
        Tarifacao tarifas = (Tarifacao) moeda;
        if (compra) {
            aliquota = tarifas.getAliquotaCompra();
            valorTaxa = tarifas.calcularTaxaCompra(valor);
            totalEmReais = valor.add(valorTaxa);
        } else {
            aliquota = tarifas.getAliquotaVenda();
            valorTaxa = tarifas.calcularTaxaVenda(valor);
            totalEmReais = valor.subtract(valorTaxa);
        }
        quantidade = valor.divide(moeda.getCotacao(), 8, RoundingMode.HALF_UP);
        if (quantidade.signum() == 0) {
            throw new IllegalArgumentException("O valor é muito pequeno para a cotação desta moeda.");
        }
    }

    /** Soma a taxa ao valor pago em reais. */
    public static CalculoNegociacao compra(Moedas moeda, BigDecimal valorEmReais) {
        return new CalculoNegociacao(moeda, valorEmReais, true);
    }

    /** Desconta a taxa do valor recebido em reais. */
    public static CalculoNegociacao venda(Moedas moeda, BigDecimal valorEmReais) {
        return new CalculoNegociacao(moeda, valorEmReais, false);
    }

    public BigDecimal getValor() {
        return valor;
    }

    public BigDecimal getValorTaxa() {
        return valorTaxa;
    }

    public BigDecimal getTotalEmReais() {
        return totalEmReais;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public BigDecimal getAliquota() {
        return aliquota;
    }
}
