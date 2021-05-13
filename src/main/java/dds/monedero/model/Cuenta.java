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
    this.sumarSaldo(cuanto);
    this.agregarMovimiento(new Deposito(LocalDate.now(), cuanto));
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

  /* Podría haber hecho un método común tanto para sumar como para restar una cantidad al saldo, que sea modificarSaldo(double cantidad), pero al momento de restar
   el saldo cuando se realiza una extracción, debería pasarle "- cuanto" por parámetro, y eso me pareció un poco "feo", aunque es la única forma que se me ocurió para
   hacerlo.*/

  public void sumarSaldo(double cantidad) {
    this.saldo += cantidad;
  }

  public void restarSaldo(double cantidad) {
    this.saldo -= cantidad;
  }

  public void sacar(double cuanto) {
    this.validarMontoNoNegativo(cuanto);
    this.validarSaldoSuperiorAMontoAExtraer(cuanto);
    this.validarMontoExtraccionDiaria(cuanto);
    this.restarSaldo(cuanto);
    this.agregarMovimiento(new Extraccion(LocalDate.now(), cuanto));
  }

  public void validarSaldoSuperiorAMontoAExtraer(double monto) {
    if (this.getSaldo() - monto < 0) {
      throw new SaldoMenorException("No puede sacar mas de " + this.getSaldo() + " $");
    }
  }

  public void validarMontoExtraccionDiaria(double monto) {
    double limite = this.limiteExtraccionRestanteDelDia();
    if (monto > limite) {
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + 1000
          + " diarios, límite: " + limite);
    }
  }

  public double limiteExtraccionRestanteDelDia() {
    double montoExtraidoHoy = this.getMontoExtraidoEn(LocalDate.now());
    return this.limiteExtraccionDiaria() - montoExtraidoHoy;
  }

  private double limiteExtraccionDiaria() {
    return 1000;
  }

  public void agregarMovimiento(Movimiento movimiento) {
    movimientos.add(movimiento);
  }

  public double getMontoExtraidoEn(LocalDate fecha) {
    return getMovimientos().stream()
        .filter(movimiento -> movimiento.fueExtraido(fecha))
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
