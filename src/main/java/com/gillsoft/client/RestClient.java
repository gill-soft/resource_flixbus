package com.gillsoft.client;

import java.net.URI;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.logging.log4j.core.util.datetime.FastDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.gillsoft.cache.CacheHandler;
import com.gillsoft.cache.IOCacheException;
import com.gillsoft.cache.RedisMemoryCache;
import com.gillsoft.logging.SimpleRequestResponseLoggingInterceptor;
import com.gillsoft.model.AuthenticateResult;
import com.gillsoft.model.Customer;
import com.gillsoft.model.FlixbusTrip;
import com.gillsoft.model.ReservationResult;
import com.gillsoft.model.Station;
import com.gillsoft.model.StationsResult;
import com.gillsoft.model.TripItem;
import com.gillsoft.model.TripsResult;
import com.gillsoft.model.request.Request;
import com.gillsoft.util.RestTemplateUtil;
import com.gillsoft.util.StringUtil;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class RestClient {

	public static final String STATIONS_CACHE_KEY = "flixbus.stations.";
	public static final String TRIPS_CACHE_KEY = "flixbus.trips.";

	public static final String CURRENCY = "EUR";

	private static final String STATIONS = "public/v1/network.json";
	private static final String AUTHENTICATION = "public/v1/partner/authenticate.json";
	private static final String TRIPS = "public/v1/trip/search.json";
	private static final String TRIP_INFO = "public/v1/trips/%s/info.json";
	private static final String CREATE_RESERVATION = "public/v1/reservation/items.json";
	private static final String PASSENGERS = "public/v1/reservations/%s/passengers.json";
	private static final String PAYMENT_START = "public/v1/payment/start.json";
	private static final String PAYMENT_COMMIT = "public/v1/payment/commit.json";
	private static final String TICKET_FOR_ORDER = "public/v2/orders/%s/info.json";
	
	public static final String DATE_FORMAT = "dd.MM.yyyy";
	public static final String TIME_FORMAT = "HH:mm";
	public static final String FULL_DATE_FORMAT = "dd.MM.yyyy HH:mm";
	public final static FastDateFormat dateFormat =  FastDateFormat.getInstance(DATE_FORMAT);
	public final static FastDateFormat dateFormatFull = FastDateFormat.getInstance(FULL_DATE_FORMAT);

	private static HttpHeaders headers = new HttpHeaders();
	private static HttpHeaders sessionHeaders = new HttpHeaders();

    @Autowired
    @Qualifier("RedisMemoryCache")
	private CacheHandler cache;
   
    static {
    	headers.add("X-API-Authentication", Config.getKey());
    	sessionHeaders.addAll(headers);
    }

	private RestTemplate template;
	// для запросов поиска с меньшим таймаутом
	private RestTemplate searchTemplate;

	public RestClient() {
		template = createNewPoolingTemplate(Config.getRequestTimeout());
		searchTemplate = createNewPoolingTemplate(Config.getSearchRequestTimeout());
	}

	public RestTemplate createNewPoolingTemplate(int requestTimeout) {
		HttpComponentsClientHttpRequestFactory factory = (HttpComponentsClientHttpRequestFactory) RestTemplateUtil
				.createPoolingFactory(Config.getUrl(), 300, requestTimeout, true, true);
		factory.setReadTimeout(Config.getReadTimeout());
		RestTemplate template = new RestTemplate(new BufferingClientHttpRequestFactory(factory));
		template.setInterceptors(Collections.singletonList(new SimpleRequestResponseLoggingInterceptor()));
		return template;
	}
	
	/****************** AUTH ********************/
	public String getAuthToken() throws ResponseError {
		try {
			AuthenticateResult result = getResult(template, null, HttpMethod.POST,
					AuthenticateResult.getTypeReference(), getAuthUri(), null);
			if (result != null) {
				return result.getToken();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ResponseError("getAuthToken error!", e);
		}
		throw new ResponseError("getAuthToken error!");
	}
	
	private URI getAuthUri() {
		return UriComponentsBuilder.fromUriString(Config.getUrl().concat(AUTHENTICATION))
				.queryParam("email", Config.getEmail())
				.queryParam("password", Config.getPassword())
				.build().toUri();
	}

	/****************** STATIONS ********************/
	@SuppressWarnings("unchecked")
	public List<Station> getCachedStations() throws IOCacheException {
		Map<String, Object> params = new HashMap<>();
		params.put(RedisMemoryCache.OBJECT_NAME, RestClient.STATIONS_CACHE_KEY);
		params.put(RedisMemoryCache.IGNORE_AGE, true);
		params.put(RedisMemoryCache.UPDATE_DELAY, Config.getCacheStationsUpdateDelay());
		params.put(RedisMemoryCache.UPDATE_TASK, new StationsUpdateTask());
		return (List<Station>) cache.read(params);
	}

	public List<Station> getAllStations() throws ResponseError {
		try {
			StationsResult result = getResult(template, null, HttpMethod.GET, StationsResult.getTypeReference(),
					UriComponentsBuilder.fromUriString(Config.getUrl().concat(STATIONS)).build().toUri(), null);
			if (result != null) {
				return result.getStations();
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/****************** TRIPS ********************/
	public TripPackage getCachedTrips(String from, String to, Date dispatch) throws ResponseError {
		URI uri = getTripsUri(from, to, dispatch);
		Map<String, Object> params = new HashMap<>();
		params.put(RedisMemoryCache.OBJECT_NAME, uri.getQuery());
		params.put(RedisMemoryCache.UPDATE_TASK, new GetTripsTask(uri));
		try {
			return (TripPackage) cache.read(params);
		} catch (IOCacheException e) {
			e.printStackTrace();
			// ставим пометку, что кэш еще формируется
			TripPackage tripPackage = new TripPackage();
			tripPackage.setContinueSearch(true);
			return tripPackage;
		} catch (Exception e) {
			throw new ResponseError(e.getMessage());
		}
	}

	public TripPackage getTrips(URI uri) throws ResponseError {
		TripPackage tripPackage = new TripPackage(getResult(searchTemplate, null, HttpMethod.GET, TripsResult.getTypeReference(), uri, null));
		// получаем маршрут следования по каждому рейсу
		if (tripPackage != null && tripPackage.getTripResult() != null && tripPackage.getTripResult().getTrips() != null) {
			for (FlixbusTrip trip : tripPackage.getTripResult().getTrips()) {
				if (trip.getItems() != null) {
					for (TripItem tripItem : trip.getItems()) {
						try {
							TripsResult tripInfo = getTripInfo(tripItem.getUid());
							if (tripInfo != null && tripInfo.getTrips() != null) {
								for (FlixbusTrip tripInfoTrip : tripInfo.getTrips()) {
									if (tripInfoTrip.getStops() != null && Objects.equals(tripItem.getUid(), tripInfoTrip.getUid())) {
										// номер рейса
										tripItem.setLineCode(tripInfoTrip.getLineCode());
										// маршрут следования (список остановок на маршруте)
										tripItem.setStops(tripInfoTrip.getStops());
									}
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		return tripPackage;
	}
	
	public URI getTripsUri(String from, String to, Date dispatch) {
		//http://api.sandbox.mfb.io/public/v1/trip/search.json?search_by=stations&from=13477&to=2186&adult=2&currency=EUR&departure_date=30.07.2018
		return UriComponentsBuilder.fromUriString(Config.getUrl().concat(TRIPS))
				.queryParam("search_by", "stations")
				.queryParam("from", from)
				.queryParam("to", to)
				.queryParam("adult", 1)
				.queryParam("currency", RestClient.CURRENCY)
				.queryParam("departure_date", dateFormat.format(dispatch))
				.build().toUri();
	}

	private TripsResult getTripInfo(String uid) throws ResponseError {
		return getResult(searchTemplate, null, HttpMethod.GET, TripsResult.getTypeReference(), UriComponentsBuilder
				.fromUriString(Config.getUrl().concat(String.format(TRIP_INFO, uid))).build().toUri(), null);
	}
	
	/****************** RESERVATION/PAY/CANCEL ********************/
	public void createReservation(Map<String, List<Customer>> serviceMap, OrderIdModel orderIdModel) throws ResponseError {
		String sessionId = getAuthToken();
		for (Entry<String, List<Customer>> mapEntry : serviceMap.entrySet()) {
			ReservationResult reservationResult = getResult(template,
					getReservationRequest(sessionId, mapEntry.getKey(), mapEntry.getValue().size()),
					ReservationResult.class, HttpMethod.PUT, getReservationUri(mapEntry.getKey(), mapEntry.getValue().size()));
			if (reservationResult == null) {
				throw new ResponseError("Не удалось получить ответ от ресурса при создании заказа.");
			} else if (reservationResult.getCode() != -1) {
				throw new ResponseError(String.join("\r\n", "В ресурсе не удалось создать заказ.", 
						String.valueOf(reservationResult.getCode()), reservationResult.getMessage()));
			} else {
				TripsResult passengerResult = getResult(template, getPassengersGetRequest(), TripsResult.class,
						HttpMethod.GET, getPassangersUri(reservationResult.getReservation().getId(),
								reservationResult.getReservation().getToken()));
				passengerResult = getResult(template,
						getPassengersPutRequest(reservationResult.getReservation().getToken(), mapEntry.getValue(), passengerResult),
						TripsResult.class, HttpMethod.PUT, getPassangersUri(reservationResult.getReservation().getId(), null));
				if (passengerResult == null) {
					throw new ResponseError("Не удалось получить ответ от ресурса при добавлении пассажиров в заказ.");
				} else if (passengerResult.getCode() != -1) {
					throw new ResponseError(String.join("\r\n", "В ресурсе не удалось добавить пассажиров в заказ.",
							String.valueOf(passengerResult.getCode()), passengerResult.getMessage()));
				}
				orderIdModel.getServices().add(reservationResult);
			}
		}
	}
	
	private URI getReservationUri(String tripUid, int adultCount) {
		return UriComponentsBuilder.fromUriString(Config.getUrl().concat(CREATE_RESERVATION))
				.build().toUri();
	}
	
	private HttpHeaders getBaseHeaders() {
	    /*-H "Accept-Language: en" \
	    -H "Accept: application/json" \
	    -H "Content-Type: application/x-www-form-urlencoded" \
	    -H "X-API-Authentication: <X-API-Authentication>" \*/
		HttpHeaders headers = new HttpHeaders();
		headers.add("X-API-Authentication", Config.getKey());
		headers.add("Accept", "application/json");
		headers.add("Accept-Language", "en;q=0.5");
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		return headers;
	}
	
	private HttpHeaders getHeaders(String sessionId) {
		headers = getBaseHeaders();
		headers.add("X-API-Session", sessionId);
		headers.add("Accept-Encoding", "gzip, identity");
		headers.add("User-Agent", "GILLSOFT");
		return headers;
	}
	
	private HttpHeaders getPaymentHeaders(String sessionId) {
		headers = getBaseHeaders();
		headers.add("X-API-Session", sessionId);
		return headers;
	}
	
	private HttpEntity<MultiValueMap<String, String>> getReservationRequest(String sessionId, String tripUid, int ticketCount) {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("trip_uid", tripUid);
		map.add("adult", String.valueOf(ticketCount));
		map.add("currency", RestClient.CURRENCY);
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, getHeaders(sessionId));
		return request;
	}
	
	private URI getPassangersUri(long reservationId, String reservationToken) {
		UriComponentsBuilder uri = UriComponentsBuilder.fromUriString(Config.getUrl().concat(String.format(PASSENGERS, reservationId)));
		if (reservationToken != null) {
			uri = uri.queryParam("reservation_token", reservationToken);
		}
		return uri.build().toUri();
	}
	
	private URI getUri(String method) {
		return UriComponentsBuilder.fromUriString(Config.getUrl() + method).build().toUri();
	}
	
	private HttpEntity<MultiValueMap<String, String>> getPassengersGetRequest() {
		return new HttpEntity<MultiValueMap<String, String>>(null, getBaseHeaders());
	}
	
	private HttpEntity<MultiValueMap<String, String>> getPassengersPutRequest(String reservationToken, List<Customer> customers, TripsResult passengerResult) {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("reservation_token", reservationToken);
		map.add("with_donation", "false");
		int i = 0;
		for (Customer customer : customers) {
			String passenger = new StringBuilder("passengers[").append(i).append(']').toString();
			map.add(passenger + "[firstname]", customer.getName());
			map.add(passenger + "[lastname]", customer.getSurname());
			map.add(passenger + "[phone]", customer.getPhone());
			map.add(passenger + "[birthdate]", StringUtil.dateFormat.format(customer.getBirthday()));
			map.add(passenger + "[type]", passengerResult.getTrips().get(0).getPassengers().get(i).getType());
			map.add(passenger + "[reference_id]", passengerResult.getTrips().get(0).getPassengers().get(i).getReferenceId());
			map.add(passenger + "[parental_permission]", "false");
			i++;
		}
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, getBaseHeaders());
		return request;
	}
	
	public void payment(List<ReservationResult> reservationResultList){
		String sessionId = null;
		try {
			sessionId = getAuthToken();
		} catch (Exception e) {
			reservationResultList.stream().forEach(c -> {
				c.setCode(0);
				c.setMessage(e.getMessage());
			});
		}
		for (ReservationResult reservationResult : reservationResultList) {
			try {
				ReservationResult paymentStartResult = getResult(template,
						getPaymentStartRequest(String.valueOf(reservationResult.getReservation().getId()),
								reservationResult.getReservation().getToken(), sessionId),
						ReservationResult.class, HttpMethod.POST, getUri(PAYMENT_START));
				if (paymentStartResult == null) {
					throw new ResponseError("Не удалось получить ответ от ресурса при создании платежа.");
				} else if (paymentStartResult.getCode() != -1) {
					throw new ResponseError(String.join("\r\n", "В ресурсе не удалось создать платеж.",
							String.valueOf(paymentStartResult.getCode()), paymentStartResult.getMessage()));
				} else {
					ReservationResult paymentCommitResult = getResult(template,
							getPaymentCommitRequest(String.valueOf(reservationResult.getReservation().getId()),
									reservationResult.getReservation().getToken(), paymentStartResult.getPaymentId(), sessionId),
							ReservationResult.class, HttpMethod.PUT, getUri(PAYMENT_COMMIT));
					if (paymentCommitResult == null) {
						throw new ResponseError("Не удалось получить ответ от ресурса при подтверждении платежа "
								+ paymentStartResult.getPaymentId());
					} else if (paymentCommitResult.getCode() != -1) {
						throw new ResponseError(String.join("\r\n", "В ресурсе не удалось подтвердить платеж.",
								String.valueOf(paymentCommitResult.getCode()), paymentCommitResult.getMessage()));
					} else {
						reservationResult.setPaymentId(paymentStartResult.getPaymentId());
						reservationResult.setOrderId(paymentCommitResult.getOrderId());
						reservationResult.setHash(paymentCommitResult.getHash());
						reservationResult.setCode(1);
					}
				}
			} catch (Exception e) {
				reservationResult.setCode(0);
				reservationResult.setMessage(e.getMessage());
			}
		}
	}
	
	private HttpEntity<MultiValueMap<String, String>> getPaymentStartRequest(String reservationId,
			String reservationToken, String sessionId) {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("reservation", reservationId);
		map.add("reservation_token", String.valueOf(reservationToken));
		map.add("email", Config.getPaymentEmail());
		map.add("payment[psp]", "offline");
		map.add("payment[method]", "cash");
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map,
				getPaymentHeaders(sessionId));
		return request;
	}
	
	private HttpEntity<MultiValueMap<String, String>> getPaymentCommitRequest(String reservationId,
			String reservationToken, Long paymentId, String sessionId) {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("reservation", reservationId);
		map.add("reservation_token", String.valueOf(reservationToken));
		map.add("payment_id", String.valueOf(paymentId));
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map,
				getPaymentHeaders(sessionId));
		return request;
	}

	/*************************************************/
	private <T> T getResult(RestTemplate template, Request request, HttpMethod httpMethod,
			ParameterizedTypeReference<T> type, URI uri, String sessionId) throws ResponseError {
		RequestEntity<Request> requestEntity = new RequestEntity<Request>(request,
				sessionId == null ? headers : getSessionHeaders(sessionId), httpMethod, uri);
		ResponseEntity<T> response = template.exchange(requestEntity, type);
		return response.getBody();
	}
	
	private <T> T getResult(RestTemplate template, HttpEntity<MultiValueMap<String, String>> request, Class<T> type, HttpMethod method, URI uri)
			throws ResponseError {
		ResponseEntity<T> response = template.exchange(uri, method, request, type);
		return response.getBody();
	}
	
	private HttpHeaders getSessionHeaders(String sessionId) {
		sessionHeaders.remove("X-API-Session");
		sessionHeaders.add("X-API-Session", sessionId);
		return sessionHeaders;
	}

	public CacheHandler getCache() {
		return cache;
	}

	public static RestClientException createUnavailableMethod() {
		return new RestClientException("Method is unavailable");
	}

}
