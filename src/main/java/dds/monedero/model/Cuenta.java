package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cuenta {

  private double saldo = 0;
  private List<Movimiento> movimientos = new ArrayList<>();

  public Cuenta() {
    saldo = 0;
  }

  public Cuenta(double montoInicial) {
    saldo = montoInicial;
  }

  public void setMovimientos(List<Movimiento> movimientos) {
    this.movimientos = movimientos;
  }

  public void poner(double cuanto) {
    this.validarMontoNoNegativo(cuanto);
    this.validarCantidadDeDepositosDiariosMenorA(3);
    new Movimiento(LocalDate.now(), cuanto, true).agregateA(this);
  }

  public void validarMontoNoNegativo(double monto) {
    if (monto <= 0) {
      throw new MontoNegativoException(monto + ": el monto a ingresar debe ser un valor positivo");
    }
  }

  public void validarCantidadDeDepositosDiariosMenorA(int cantidadDepositosDiarios) {
    if (this.movimientos.stream().filter(movimiento -> movimiento.isDeposito()).count() >= cantidadDepositosDiarios) {
      throw new MaximaCantidadDepositosException("Ya excedio los " + cantidadDepositosDiarios + " depositos diarios");
    }
  }

  public void sacar(double cuanto) {
    this.validarMontoNoNegativo(cuanto);
    this.validarSaldoSuperiorAMontoAExtraer(cuanto);
    this.validarMontoExtraccionDiaria(cuanto);
    new Movimiento(LocalDate.now(), cuanto, false).agregateA(this);
  }

  public void validarSaldoSuperiorAMontoAExtraer(double monto) {
    if (this.getSaldo() - monto < 0) {
      throw new SaldoMenorException("No puede sacar mas de " + this.getSaldo() + " $");
    }
  }

  public void validarMontoExtraccionDiaria(double monto) {
    double limite = this.limiteExtraccionDelDia();
    if (monto > limite) {
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + 1000
          + " diarios, límite: " + limite);
    }
  }

  public double limiteExtraccionDelDia() {
    double montoExtraidoHoy = this.getMontoExtraidoA(LocalDate.now());
    return 1000 - montoExtraidoHoy;
  }

  public void agregarMovimiento(LocalDate fecha, double cuanto, boolean esDeposito) {
    Movimiento movimiento = new Movimiento(fecha, cuanto, esDeposito);
    movimientos.add(movimiento);
  }

  public double getMontoExtraidoA(LocalDate fecha) {
    return getMovimientos().stream()
        .filter(movimiento -> movimiento.esExtraccionConFecha(fecha))
        .mapToDouble(Movimiento::getMonto)
        .sum();
  }

  public List<Movimiento> getMovimientos() {
    return movimientos;
  }

  public double getSaldo() {
    return saldo;
  }

  public void setSaldo(double saldo) {
    this.saldo = saldo;
  }

}
