package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.*;

public class MonederoTest {
  private Cuenta cuenta;

  @BeforeEach
  void init() {
    cuenta = new Cuenta();
  }

  @Test
  public void UnDepositoSeRealizaCorrectamente() {
    cuenta.poner(1500);
    assertEquals(1500, cuenta.getSaldo());
  }

  @Test
  public void NoSePuedeDepositarUnMontoNegativo() {
    assertThrows(MontoNegativoException.class, () -> cuenta.poner(-1500));
  }

  @Test
  public void TresDepositosSeRealizanCorrectamente() {
    cuenta.poner(1500);
    cuenta.poner(456);
    cuenta.poner(1900);
    assertEquals(3856, cuenta.getSaldo());
  }

  @Test
  public void NoSePuedenRealizarMasDeTresDepositos() {
    assertThrows(MaximaCantidadDepositosException.class, () -> {
          cuenta.poner(1500);
          cuenta.poner(456);
          cuenta.poner(1900);
          cuenta.poner(245);
    });
  }

  @Test
  public void NoSePuedeExtraerMasQueElSaldo() {
    assertThrows(SaldoMenorException.class, () -> {
          cuenta.setSaldo(90);
          cuenta.sacar(1001);
    });
  }

  @Test
  public void NoSePuedeExtraerMasDelMontoDiarioPermitido() {
    assertThrows(MaximoExtraccionDiarioException.class, () -> {
      cuenta.setSaldo(5000);
      cuenta.sacar(1001);
    });
  }

  @Test
  public void NoSePuedeExtraerUnMontoNegativo() {
    assertThrows(MontoNegativoException.class, () -> cuenta.sacar(-500));
  }

  @Test
  public void UnaExtraccionSeRealizaConExito() {
    cuenta.setSaldo(5000);
    cuenta.sacar(1000);
    assertEquals(4000, cuenta.getSaldo());
  }

  @Test
  public void ElMontoExtraidoEnUnaDeterminadaFechaSeCalculaBien() {
    this.depositosDeHoy();
    this.extraccionesDeHoy();
    assertEquals(800, cuenta.getMontoExtraidoEn(LocalDate.now()));
  }

  private void depositosDeHoy() {
    cuenta.poner(5000);
    cuenta.poner(4000);
  }

  private void extraccionesDeHoy() {
    cuenta.sacar(200);
    cuenta.sacar(200);
    cuenta.sacar(200);
    cuenta.sacar(200);
  }
}