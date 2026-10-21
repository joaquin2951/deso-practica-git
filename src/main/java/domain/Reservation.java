package domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Reservation implements Identifiable {
  private Long id;
  private Instant date;
  private TouristService service;
  private User owner;

  public double calculatePrice() {
    return service.calculatePrice();
  }

  @Override
  public String getId() {
    return id.toString();
  }

  public static class Builder {
    private Long id;
    private Instant date;
    private TouristService service;
    private User owner;

    public Builder id(Long id) {
      this.id = id;
      return this;
    }

    public Builder date(Instant date) {
      this.date = date;
      return this;
    }

    public Builder service(TouristService service) {
      this.service = service;
      return this;
    }

    public Builder owner(User owner) {
      this.owner = owner;
      return this;
    }

    public Reservation build() {
      // 1. Validaciones de consistencia
      if (service == null) {
        throw new IllegalStateException("La reserva debe tener un servicio asociado.");
      }
      if (owner == null) {
        throw new IllegalStateException("La reserva debe tener un usuario dueño.");
      }
      if (date == null) {
        throw new IllegalStateException("La reserva debe tener una fecha.");
      }
      // 2. Creación y asignación
      Reservation reservation = new Reservation();
      reservation.id = this.id;
      reservation.date = this.date;
      reservation.service = this.service;
      reservation.owner = this.owner;

      return reservation;
    }
  }
}
