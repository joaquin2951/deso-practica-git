package service;

import domain.Flight;
import domain.TouristService;
import exception.FlightNotFoundException;
import exception.ServiceNotFoundException;
import repository.dao.FlightDao;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class TouristServiceService {

  // Ejercicio 2
  public double getAveragePriceByCompany(List<TouristService> services, String company)
      throws ServiceNotFoundException {

    if(services.isEmpty()){
      throw new ServiceNotFoundException("Lista de servicios vacia");
    }

    int cantidadServicios = 0;
    double precio = 0.0;


    for (TouristService service : services) {
      if(service.getCompany().equalsIgnoreCase(company)){
        cantidadServicios++;
        precio += service.calculatePrice();
      }
    }

    if(cantidadServicios == 0){
      throw new ServiceNotFoundException("servicio no encontrado");
    }

    return precio / (double) cantidadServicios;
  }

  // Ejercicio 4
  public List<Flight> getFlightsSortedByPriceThenAirline(
      List<String> flightNumbers, FlightDao flightDao) throws FlightNotFoundException {

    List<Flight> vuelos = new ArrayList<Flight>();

    for(String number : flightNumbers){

      vuelos.add(flightDao.findByNumber(number).orElseThrow(
              () -> new FlightNotFoundException("numero de vuelo no encontrado")
      ));

    }

    vuelos.sort(
            Comparator.comparingDouble(Flight::calculatePrice)
                    .thenComparing(Flight::getAirline)
    );

    return vuelos;

  }
}
