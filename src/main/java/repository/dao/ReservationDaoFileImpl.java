package repository.dao;

import domain.Flight;
import domain.Reservation;
import domain.TouristService;
import domain.User;
import exception.DataAccessException;

import java.io.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReservationDaoFileImpl implements ReservationDao {
  private static ReservationDaoFileImpl instance;
  private final File file;

  private ReservationDaoFileImpl() {
    this.file = new File("reservations.csv");
  }

  // visible for testing, so tests can point at a temp file instead of the real one
  ReservationDaoFileImpl(File file) {
    this.file = file;
  }

  // singleton
  public static synchronized ReservationDaoFileImpl getInstance() {
    if (instance == null) {
      instance = new ReservationDaoFileImpl();
    }

    return instance;
  }

  @Override
  public void save(Reservation reservation) throws DataAccessException {
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
      String line =
          String.format(
              "%s,%s,%s,%s\n",
              reservation.getId(),
              reservation.getService().getId(),
              reservation.getOwner().getId(),
              reservation.getDate());
      bw.write(line);
    } catch (IOException e) {
      throw new DataAccessException("Error al guardar la reserva", e);
    }
  }

  // Ejercicio 3 (Parte A)
  @Override
  public List<Reservation> findByDateRange(Instant from, Instant to) throws DataAccessException {

    List<Reservation> reservaciones = new ArrayList<Reservation>();

    if(!file.exists()){
      return reservaciones;
    }

    try {
      BufferedReader lector = new BufferedReader(new FileReader(file));
      String lineaDeArchivo = lector.readLine();

      while(lineaDeArchivo != null){
        String[] valores = lineaDeArchivo.split(",");

        Long id = Long.parseLong(valores[0]);
        String id_service = valores[1];
        String id_owner = valores[2];
        Instant date = Instant.parse(valores[3]);

        //verifico si la fecha esta en el rango from-to
        if(!date.isBefore(from) && !date.isAfter(to)){
          User owner = User.builder()
                          .id(UUID.fromString(id_owner))
                          .build();
          TouristService service = new TouristService(id_service) {
            @Override
            public double calculatePrice() {
              return 0;
            }
          };

          Reservation reservacion = new Reservation.Builder()
                                      .id(id)
                                      .date(date)
                                      .owner(owner)
                                      .service(service)
                                      .build();

          //añado la reservacion a la lista
          reservaciones.add(reservacion);

        }
        //leo proxima linea
        lineaDeArchivo = lector.readLine();
      }//termino de leer (fin while)

      lector.close();//Cierro el archivo

    }
    catch (FileNotFoundException e){
      throw new DataAccessException("Error al abrir el archivo");
    } catch (IOException e) {
        throw new DataAccessException("Error al leer el archivo");
    }

      return reservaciones;
    //throw new UnsupportedOperationException("Ejercicio 3: implement findByDateRange");
  }
}
