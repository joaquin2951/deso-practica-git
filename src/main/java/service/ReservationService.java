package service;

import domain.*;
import dto.reservation.CreateReservationRequest;
import dto.reservation.ReservationResponse;
import dto.reservation.ValidationResultDTO;
import dto.user.UserSummaryDTO;
import exception.DataAccessException;
import exception.DuplicateReservationException;
import exception.FlightNotFoundException;
import exception.InvalidDateRangeException;
import exception.UserNotFoundException;
import repository.dao.FlightDao;
import repository.dao.ReservationDao;
import repository.dao.ReservationExportDao;
import repository.dao.UserDao;
import util.Pair;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import java.util.Optional;

public class ReservationService {
  private final ReservationDao reservationDao;
  private final UserDao userDao;
  private final FlightDao flightDao;

  public ReservationService(ReservationDao reservationDao, UserDao userDao, FlightDao flightDao) {
    this.reservationDao = reservationDao;
    this.userDao = userDao;
    this.flightDao = flightDao;
  }

  public ReservationResponse createReservation(CreateReservationRequest request)
      throws DataAccessException, UserNotFoundException, FlightNotFoundException {
    // 1. Resolver entidades de dominio desde los datos del DTO
    User user =
        userDao
            .findByUsername(request.username())
            .orElseThrow(() -> new UserNotFoundException(request.username()));
    TouristService service =
        flightDao
            .findByNumber(request.flightNumber())
            .orElseThrow(() -> new FlightNotFoundException(request.flightNumber()));

    // 2. Construir la reserva (lógica de dominio)
    Reservation reservation =
        new Reservation.Builder()
            .id((long) (Math.random() * 1000))
            .owner(user)
            .service(service)
            .date(request.date())
            .build();

    reservationDao.save(reservation);

    // 3. Mapear a DTO de respuesta
    return new ReservationResponse(
        reservation.getId(),
        user.getUsername(),
        service.toString(),
        reservation.calculatePrice(),
        request.date());
  }

  // Ejercicio 1
  public List<Reservation> getReservationsByUser(
      List<Reservation> reservations, String username, UserDao userDao)
      throws UserNotFoundException, DataAccessException {

      Optional<User> resultado_busqueda = userDao.findByUsername(username);

      if(!resultado_busqueda.isPresent()){
        throw new UserNotFoundException("Usuario " + username + " no encontrado");
      }

      User usuario = resultado_busqueda.get();

      List<Reservation> lista_final = reservations.stream()
        .filter(r -> r.getOwner() != null && r.getOwner().getId().equals(usuario.getId()))
        .sorted((r1, r2) -> Double.compare(r2.calculatePrice(), r1.calculatePrice()))
        .toList();

      return lista_final;

    //throw new UnsupportedOperationException("Ejercicio 1: implement getReservationsByUser");
  }

  // Ejercicio 3
  public List<Reservation> getTopNMostExpensive(
      ReservationDao reservationDao, Instant from, Instant to, int n)
      throws InvalidDateRangeException, DataAccessException {

    //valido si from , to son null o si from es despues de to
    if(from == null || to == null || from.isAfter(to)) {
        throw new InvalidDateRangeException("rango de fechas invalido");
    }

    //lista vacia si n <= 0
    if(n <= 0){
      return new ArrayList<Reservation>();
    }

    //use stream y lambda
    return reservationDao.findByDateRange(from,to).stream()
            .sorted((r1,r2) -> Double.compare(r2.calculatePrice(), r1.calculatePrice()))
            .limit(n) //me quedo con Reservaciones hasta n o hasta reservations.size()
            .toList();

      //throw new UnsupportedOperationException("Ejercicio 3: implement getTopNMostExpensive");
  }

  // Ejercicio 5
  public Map<String, List<Reservation>> groupReservationsByCountry(List<Reservation> reservations) {
    return reservations.stream()
            .filter(r -> { // Filtrar Reservas sin location asignada
              TouristService service = r.getService();
              if(service instanceof Accommodation acc){
                return acc.getLocation() != null && acc.getLocation().getCountry() != null;
              } else if (service instanceof Flight fli){
                return fli.getArrivalInfo() != null && fli.getArrivalInfo().getCountry() != null;
              } else {
                return false;
              }
            })
            .sorted(Comparator.comparing(Reservation::getDate)) //Ordenar toda la lista por fecha
            .collect(Collectors.groupingBy(r -> { // Agrupar por pais
              TouristService service = r.getService();
              if (service instanceof Accommodation acc){
                return acc.getLocation().getCountry();
              } else if (service instanceof Flight fli){
                return fli.getArrivalInfo().getCountry();
              } else {
                return ""; //no deberia llegar aca pq ya se filtro que getCountry no devuelva null
              }
            }));

    //throw new UnsupportedOperationException("Ejercicio 5: implement groupReservationsByCountry");
  }

  // Ejercicio 7
  public Reservation createReservation(
      String username,
      String flightNumber,
      Instant date,
      UserDao userDao,
      FlightDao flightDao,
      ReservationDao reservationDao)
      throws UserNotFoundException,
          FlightNotFoundException,
          InvalidDateRangeException,
          DataAccessException {
    throw new UnsupportedOperationException("Ejercicio 7: implement createReservation");
  }

  // Ejercicio 8
  public Reservation addReservationIfNotDuplicate(
      List<Reservation> existingReservations,
      Reservation newReservation,
      ReservationDao reservationDao)
      throws DuplicateReservationException, DataAccessException {
    throw new UnsupportedOperationException("Ejercicio 8: implement addReservationIfNotDuplicate");
  }

  // Ejercicio 10
  public void exportReservationsToCsv(
      List<Reservation> reservations, ReservationExportDao exportDao) throws DataAccessException {
    throw new UnsupportedOperationException("Ejercicio 10: implement exportReservationsToCsv");
  }

  // Ejercicio 11
  public List<UserSummaryDTO> getUserReservationSummaries(
      List<Reservation> reservations, UserDao userDao)
      throws UserNotFoundException, DataAccessException {
    throw new UnsupportedOperationException("Ejercicio 11: implement getUserReservationSummaries");
  }

  // Ejercicio 12
  public ValidationResultDTO validateReservationCandidates(
      List<Pair<String, String>> candidates, UserDao userDao, FlightDao flightDao)
      throws DataAccessException {
    throw new UnsupportedOperationException(
        "Ejercicio 12: implement validateReservationCandidates");
  }
}
