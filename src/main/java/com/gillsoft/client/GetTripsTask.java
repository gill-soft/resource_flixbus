package com.gillsoft.client;

import java.io.Serializable;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;

import com.gillsoft.cache.IOCacheException;
import com.gillsoft.cache.RedisMemoryCache;
import com.gillsoft.model.FlixbusTrip;
import com.gillsoft.model.TripItem;
import com.gillsoft.util.ContextProvider;

public class GetTripsTask implements Runnable, Serializable {
	
	private static final long serialVersionUID = -612450869121241871L;
	
	private URI uri;
	
	public GetTripsTask() {

	}

	public GetTripsTask(URI uri) {
		this.uri = uri;
	}

	@Override
	public void run() {
		Map<String, Object> params = new HashMap<>();
		params.put(RedisMemoryCache.OBJECT_NAME, uri.getQuery());
		params.put(RedisMemoryCache.UPDATE_TASK, this);
		params.put(RedisMemoryCache.UPDATE_DELAY, Config.getCacheTripUpdateDelay());
		TripPackage tripPackage = null;
		
		// получаем рейсы для создания кэша
		RestClient client = ContextProvider.getBean(RestClient.class);
		try {
			tripPackage = client.getTrips(uri);
			params.put(RedisMemoryCache.TIME_TO_LIVE, getTimeToLive(tripPackage));
		} catch (ResponseError e) {
			// ошибку поиска тоже кладем в кэш но с другим временем жизни
			params.put(RedisMemoryCache.TIME_TO_LIVE, Config.getCacheErrorTimeToLive());
			params.put(RedisMemoryCache.UPDATE_DELAY, Config.getCacheErrorUpdateDelay());
			tripPackage = new TripPackage();
			tripPackage.setError(e);
		}
		try {
			client.getCache().write(tripPackage, params);
		} catch (IOCacheException e) {
			e.printStackTrace();
		}
	}
	
	// время жизни до момента самого позднего отправления
	private long getTimeToLive(TripPackage tripPackage) {
		if (Config.getCacheTripTimeToLive() != 0) {
			return Config.getCacheTripTimeToLive();
		}
		if (tripPackage != null && tripPackage.getTripResult() != null && tripPackage.getTripResult().getTrips() != null
				&& !tripPackage.getTripResult().getTrips().isEmpty()) {
			long max = 0;
			for (FlixbusTrip trip : tripPackage.getTripResult().getTrips()) {
				if (trip.getItems() != null && !trip.getItems().isEmpty()) {
					for (TripItem tripItem : trip.getItems()) {
						Date date = new DateTime(tripItem.getDeparture().getTimestamp() * 1000).toDate();
						if (date != null && date.getTime() > max) {
							max = date.getTime();
						}
					}
				}
			}
			return max - System.currentTimeMillis();
		}
		return 0;
	}

}
