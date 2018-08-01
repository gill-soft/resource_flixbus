package com.gillsoft.client;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.gillsoft.model.Trip;
import com.gillsoft.model.TripsResult;
import com.gillsoft.model.request.TripSearchRequest;

public class TripPackage implements Serializable {

	private static final long serialVersionUID = -1022724811422338355L;

	private TripSearchRequest request;
	
	private boolean inProgress = true;
	
	private List<ScheduleTrip> schedule;
	
	private CopyOnWriteArrayList<Trip> trips;
	
	private ResponseError error;
	
	private boolean continueSearch = false;
	
	TripsResult tripResult;
	
	public TripPackage() { }
	
	public TripPackage(TripsResult tripResult) {
		this.tripResult = tripResult;
	}

	public TripSearchRequest getRequest() {
		return request;
	}

	public void setRequest(TripSearchRequest request) {
		this.request = request;
	}

	public boolean isInProgress() {
		return inProgress;
	}

	public void setInProgress(boolean inProgress) {
		this.inProgress = inProgress;
	}

	public List<ScheduleTrip> getSchedule() {
		return schedule;
	}

	public void setSchedule(List<ScheduleTrip> schedule) {
		this.schedule = schedule;
	}

	public CopyOnWriteArrayList<Trip> getTrips() {
		return trips;
	}

	public void setTrips(CopyOnWriteArrayList<Trip> trips) {
		this.trips = trips;
	}

	public ResponseError getError() {
		return error;
	}

	public void setError(ResponseError error) {
		this.error = error;
	}

	public boolean isContinueSearch() {
		return continueSearch;
	}

	public void setContinueSearch(boolean continueSearch) {
		this.continueSearch = continueSearch;
	}

	public TripsResult getTripResult() {
		return tripResult;
	}

	public void setTripResult(TripsResult tripResult) {
		this.tripResult = tripResult;
	}

}
