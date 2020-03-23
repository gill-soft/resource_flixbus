package com.gillsoft;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.lang.time.DateUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RestController;

import com.gillsoft.abstract_rest_service.SimpleAbstractTripSearchService;
import com.gillsoft.cache.CacheHandler;
import com.gillsoft.client.ResponseError;
import com.gillsoft.client.RestClient;
import com.gillsoft.client.TripPackage;
import com.gillsoft.model.Currency;
import com.gillsoft.model.Document;
import com.gillsoft.model.FlixbusStop;
import com.gillsoft.model.FlixbusTrip;
import com.gillsoft.model.Locality;
import com.gillsoft.model.Organisation;
import com.gillsoft.model.Price;
import com.gillsoft.model.RequiredField;
import com.gillsoft.model.RestError;
import com.gillsoft.model.ReturnCondition;
import com.gillsoft.model.Route;
import com.gillsoft.model.RoutePoint;
import com.gillsoft.model.Seat;
import com.gillsoft.model.SeatsScheme;
import com.gillsoft.model.Segment;
import com.gillsoft.model.Tariff;
import com.gillsoft.model.Trip;
import com.gillsoft.model.TripContainer;
import com.gillsoft.model.TripItem;
import com.gillsoft.model.Vehicle;
import com.gillsoft.model.request.TripSearchRequest;
import com.gillsoft.model.response.TripSearchResponse;
import com.gillsoft.util.StringUtil;

@RestController
public class SearchServiceController extends SimpleAbstractTripSearchService<TripPackage> {
	
	@Autowired
	private RestClient client;
	
	@Autowired
	@Qualifier("RedisMemoryCache")
	private CacheHandler cache;

	@Override
	public List<ReturnCondition> getConditionsResponse(String arg0, String arg1) {
		throw RestClient.createUnavailableMethod();
	}

	@Override
	public List<Document> getDocumentsResponse(String arg0) {
		throw RestClient.createUnavailableMethod();
	}

	@Override
	public List<Tariff> getTariffsResponse(String arg0) {
		throw RestClient.createUnavailableMethod();
	}

	@Override
	public List<RequiredField> getRequiredFieldsResponse(String arg0) {
		return Arrays.asList(new RequiredField[] { RequiredField.NAME, RequiredField.SURNAME, RequiredField.BIRTHDAY,
				RequiredField.PHONE });
	}

	@Override
	public Route getRouteResponse(String arg0) {
		throw RestClient.createUnavailableMethod();
	}

	@Override
	public SeatsScheme getSeatsSchemeResponse(String arg0) {
		throw RestClient.createUnavailableMethod();
	}
	
	@Override
	public List<Seat> updateSeatsResponse(String arg0, List<Seat> arg1) {
		throw RestClient.createUnavailableMethod();
	}
	
	@Override
	public List<Seat> getSeatsResponse(String tripId) {
		throw RestClient.createUnavailableMethod();
	}

	@Override
	public TripSearchResponse initSearchResponse(TripSearchRequest request) {
		return simpleInitSearchResponse(cache, request);
	}
	
	@Override
	public void addInitSearchCallables(List<Callable<TripPackage>> callables, String[] pair, Date date) {
		callables.add(() -> {
			try {
				validateSearchParams(pair, date);
				TripPackage tripPackage = client.getCachedTrips(pair[0], pair[1], date);
				if (tripPackage == null) {
					throw new ResponseError("Empty result");
				}
				tripPackage.setRequest(TripSearchRequest.createRequest(pair, date));
				return tripPackage;
			} catch (ResponseError e) {
				TripPackage tripPackage = new TripPackage();
				tripPackage.setError(e);
				tripPackage.setRequest(TripSearchRequest.createRequest(pair, date));
				return tripPackage;
			} catch (Exception e) {
				return null;
			}
		});
	}
	
	private static void validateSearchParams(String[] pair, Date date) throws ResponseError {
		if (date == null
				|| date.getTime() < DateUtils.truncate(new Date(), Calendar.DATE).getTime()) {
			throw new ResponseError("Invalid parameter \"date\"");
		}
		if (pair == null || pair.length < 2) {
			throw new ResponseError("Invalid parameter \"pair\"");
		}
	}
	
	@Override
	public TripSearchResponse getSearchResultResponse(String searchId) {
		return simpleGetSearchResponse(cache, searchId);
	}
	
	@Override
	public void addNextGetSearchCallablesAndResult(List<Callable<TripPackage>> callables, Map<String, Vehicle> vehicles,
			Map<String, Locality> localities, Map<String, Organisation> organisations, Map<String, Segment> segments,
			List<TripContainer> containers, TripPackage tripPackage) {
		if (!tripPackage.isContinueSearch()) {
			addResult(vehicles, localities, segments, containers, tripPackage);
		} else if (tripPackage.getRequest() != null) {
			addInitSearchCallables(callables, tripPackage.getRequest().getLocalityPairs().get(0),
					tripPackage.getRequest().getDates().get(0));
		}
	}
	
	private void addResult(Map<String, Vehicle> vehicles, Map<String, Locality> localities,
			Map<String, Segment> segments, List<TripContainer> containers, TripPackage tripPackage) {
		TripContainer container = new TripContainer();
		container.setRequest(tripPackage.getRequest());
		if (tripPackage != null && tripPackage.getTripResult() != null && tripPackage.getTripResult().getTrips() != null
				&& !tripPackage.getTripResult().getTrips().isEmpty()) {
			List<Trip> trips = new ArrayList<>();
			for (FlixbusTrip resourceTrip : tripPackage.getTripResult().getTrips()) {
				for (TripItem tripItem : resourceTrip.getItems()) {
					if (!tripItem.isTransborder()
							&& !tripItem.isSaleRestriction()
							&& "available".equals(tripItem.getStatus())
							&& "direct".equals(tripItem.getType())) {
						Trip tmpTrip = new Trip();
						tmpTrip.setId(tripItem.getUid());
						trips.add(tmpTrip);
						String segmentId = tmpTrip.getId();
						Segment segment = segments.get(segmentId);
						if (segment == null) {
							segment = new Segment();
							segment.setId(tmpTrip.getId());
							segment.setNumber(String.join(" - ", resourceTrip.getFrom().getName(), resourceTrip.getTo().getName()));
							try {
								segment.setDepartureDate(new DateTime(tripItem.getDeparture().getTimestamp() * 1000).toDate());
								segment.setArrivalDate(new DateTime(tripItem.getArrival().getTimestamp() * 1000).toDate());
							} catch (Exception e) { }
							segments.put(segmentId, segment);
						}
						segment.setDeparture(addStation(localities, String.valueOf(resourceTrip.getFrom().getId())));
						segment.setArrival(addStation(localities, String.valueOf(resourceTrip.getTo().getId())));
						segment.setRoute(createRoute(segment, resourceTrip, localities));
						if (tripItem.getInfoMessage() != null && !tripItem.getInfoMessage().isEmpty()) {
							if (segment.getAdditionals() == null) {
								segment.setAdditionals(new HashMap<>());
							}
							segment.getAdditionals().put("info_message", tripItem.getInfoMessage());
						}
						addPrice(segment, tripItem.getPrice());
					}
				}
			}
			container.setTrips(trips);
		}
		if (tripPackage.getError() != null) {
			container.setError(new RestError(tripPackage.getError().getMessage()));
		}
		containers.add(container);
	}

	private Route createRoute(Segment segment, FlixbusTrip resourceTrip, Map<String, Locality> localities) {
		Route route = null;
		if (resourceTrip.getItems() != null) {
			for (TripItem tripItem : resourceTrip.getItems()) {
				if (tripItem.getUid().equals(segment.getId()) && tripItem.getStops() != null) {
					segment.setNumber(tripItem.getLineCode());
					for (FlixbusStop stop : tripItem.getStops()) {
						RoutePoint routePoint = new RoutePoint();
						routePoint.setArrivalDay(Days.daysBetween(
								new LocalDate(tripItem.getArrival().getTimestamp() * 1000), new LocalDate(tripItem.getDeparture().getTimestamp() * 1000)).getDays());
						routePoint.setLocality(addStation(localities, stop.getStation().getId()));
						routePoint.setDepartureTime(StringUtil.timeFormat.format(new DateTime(tripItem.getDeparture().getTimestamp() * 1000).toDate()));
						routePoint.setPlatform(stop.getStation().getWarnings());
						if (route == null) {
							route = new Route();
							route.setPath(new ArrayList<>());
						}
						route.getPath().add(routePoint);
					}
				}
			}
		}
		return route;
	}

	private void addPrice(Segment segment, BigDecimal price) {
		Price tripPrice = new Price();
		Tariff tariff = new Tariff();
		tariff.setValue(price);
		tripPrice.setCurrency(Currency.valueOf(RestClient.CURRENCY));
		tripPrice.setAmount(price);
		tripPrice.setTariff(tariff);
		segment.setPrice(tripPrice);
	}
	
	public static void addVehicle(Map<String, Vehicle> vehicles, Segment segment, String model) {
		String vehicleKey = StringUtil.md5(model);
		Vehicle vehicle = vehicles.get(vehicleKey);
		if (vehicle == null) {
			vehicle = new Vehicle();
			vehicle.setModel(model);
			vehicles.put(vehicleKey, vehicle);
		}
		segment.setVehicle(new Vehicle(vehicleKey));
	}

	public static Locality addStation(Map<String, Locality> localities, String id) {
		Locality locality = LocalityServiceController.getLocality(id);
		if (locality == null) {
			return null;
		}
		String localityId = locality.getId();
		try {
			locality = locality.clone();
			locality.setId(null);
		} catch (CloneNotSupportedException e) {
		}
		if (!localities.containsKey(localityId)) {
			localities.put(localityId, locality);
		}
		return new Locality(localityId);
	}

}
