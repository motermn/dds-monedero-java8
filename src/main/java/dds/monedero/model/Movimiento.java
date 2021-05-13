package dds.monedero.model;

import java.time.LocalDate;

public abstract class Movimiento {
  private LocalDate fecha;
  //En ningún lenguaje de programación usen jamás doubles para modelar dinero en el mundo real
  //siempre usen numeros de precision arbitraria, como BigDecimal en Java y similares
  private double monto;

  public Movimiento(LocalDate fecha, double monto) {
    this.fecha = fecha;
    this.monto = monto;
  }

  public double getMonto() {
    return monto;
  }

  public LocalDate getFecha() {
    return fecha;
  }

  public boolean fueDepositado(LocalDate fecha) {
    return this.isDeposito() && this.esDeLaFecha(fecha);
  }

  public boolean fueExtraido(LocalDate fecha) {
    return this.isExtraccion() && this.esDeLaFecha(fecha);
  }

  public boolean esDeLaFecha(LocalDate fecha) {
    return this.fecha.equals(fecha);
  }

  /* Considerando que los dos tipos de movimientos posibles para una cuenta pueden ser depósito y extracción, decidí hacer este refactor agregando los métodos
  isDeposito() e isExtraccion(), e implementarlos en las dos clases (Depósito y Extracción). Tomé esta decisión de diseño porque no creo que agregue demasiada
  complejidad, pues sólo habrá 4 métodos para el propósito de determinar de qué tipo de movimiento se trata uno en particular.
  Por otra parte, decidí modelarlo con herencia, pues hay lógica y atributos comunes para ambos tipos de movimientos, y además, tanto Extracción como Depósito
  SON Movimientos.
 */

  public abstract boolean isDeposito();

  public abstract boolean isExtraccion();

  public void agregateA(Cuenta cuenta) { // Mispalced method
    cuenta.setSaldo(calcularValor(cuenta));
    cuenta.agregarMovimiento(this);
  }

  public double calcularValor(Cuenta cuenta) { // Mispalced method
    if (this.isDeposito()) {
      return cuenta.getSaldo() + getMonto();
    } else {
      return cuenta.getSaldo() - getMonto();
    }
  }
}
