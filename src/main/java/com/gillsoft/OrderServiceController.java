package com.gillsoft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.gillsoft.abstract_rest_service.AbstractOrderService;
import com.gillsoft.client.OrderIdModel;
import com.gillsoft.client.ResponseError;
import com.gillsoft.client.RestClient;
import com.gillsoft.model.Customer;
import com.gillsoft.model.Price;
import com.gillsoft.model.RestError;
import com.gillsoft.model.Segment;
import com.gillsoft.model.ServiceItem;
import com.gillsoft.model.request.OrderRequest;
import com.gillsoft.model.response.OrderResponse;

@RestController
public class OrderServiceController extends AbstractOrderService {
	
	@Autowired
	private RestClient client;

	@Override
	public OrderResponse addServicesResponse(OrderRequest request) {
		throw RestClient.createUnavailableMethod();
	}

	@Override
	public OrderResponse bookingResponse(String orderId) {
		throw RestClient.createUnavailableMethod();
	}

	@Override
	public OrderResponse cancelResponse(String orderId) {
		throw RestClient.createUnavailableMethod();
	}

	@Override
	public OrderResponse createResponse(OrderRequest request) {
		// формируем ответ
		OrderResponse response = new OrderResponse();
		response.setCustomers(request.getCustomers());
		
		// копия для определения пассажиров
		List<ServiceItem> items = new ArrayList<>();
		items.addAll(request.getServices());
		
		List<ServiceItem> resultItems = new ArrayList<>();
		// список билетов
		OrderIdModel orderId = new OrderIdModel();
		
		Map<String, List<Customer>> serviceMap = new HashMap<>();
		for (ServiceItem service : request.getServices()) {
			List<Customer> customers = serviceMap.get(service.getSegment().getId());
			if (customers == null) {
				customers = new ArrayList<>();
				serviceMap.put(service.getSegment().getId(), customers);
			}
			Customer customer = request.getCustomers().get(service.getCustomer().getId());
			if (customer != null) {
				customer.setId(service.getCustomer().getId());
				customers.add(customer);
			}
		}

		try {
			client.createReservation(serviceMap, orderId);
			for (Entry<String, List<Customer>> mapEntry : serviceMap.entrySet()) {
				orderId.getServices().stream()
						.filter(f -> f.getCart() != null && !f.getCart().getItems().isEmpty()
								&& f.getCart().getItems().containsKey(mapEntry.getKey()))
						.forEach(c -> {
							Segment segment = c.getSegment();
							Price price = c.getPrice();
							request.getServices().stream()
									.filter(f -> f.getSegment().getId().equals(mapEntry.getKey()))
									.forEach(service -> {
										service.setCustomer(request.getCustomers().get(service.getCustomer().getId()));
										service.setSegment(segment);
										service.setPrice(price);
										resultItems.add(service);
									});
						});
			}
		} catch (ResponseError e) {
			e.printStackTrace();
			request.getServices().stream().forEach(c -> c.setConfirmed(false));
		}
		response.setOrderId(orderId.asString());
		response.setServices(resultItems);
		return response;
	}

	@Override
	public OrderResponse getPdfDocumentsResponse(OrderRequest request) {
		throw RestClient.createUnavailableMethod();
	}

	@Override
	public OrderResponse getResponse(String orderId) {
		throw RestClient.createUnavailableMethod();
	}

	@Override
	public OrderResponse getServiceResponse(String serviceId) {
		throw RestClient.createUnavailableMethod();
	}

	@Override
	public OrderResponse confirmResponse(String orderId) {
		// формируем ответ
		OrderResponse response = new OrderResponse();
		List<ServiceItem> resultItems = new ArrayList<>();
		// преобразовываем ид заказа в объект
		OrderIdModel orderIdModel = new OrderIdModel().create(orderId);
		// выкупаем заказы и формируем ответ
		client.payment(orderIdModel.getServices());
		addServiceItems(resultItems, orderIdModel);
		response.setOrderId(orderIdModel.asString());
		response.setServices(resultItems);
		return response;
	}
	
	private void addServiceItems(List<ServiceItem> resultItems, OrderIdModel orderIdModel) {
		orderIdModel.getServices().stream().forEach(c -> {
			ServiceItem serviceItem = new ServiceItem();
			serviceItem.setId(String.valueOf(c.getOrderId()));
			serviceItem.setConfirmed(c.getCode().equals(1));
			serviceItem.setError(serviceItem.getConfirmed() ? null : new RestError(c.getMessage()));
			resultItems.add(serviceItem);
		});
	}

	@Override
	public OrderResponse removeServicesResponse(OrderRequest request) {
		throw RestClient.createUnavailableMethod();
	}

	@Override
	public OrderResponse returnServicesResponse(OrderRequest request) {
		throw RestClient.createUnavailableMethod();
	}

	@Override
	public OrderResponse updateCustomersResponse(OrderRequest request) {
		throw RestClient.createUnavailableMethod();
	}

	@Override
	public OrderResponse prepareReturnServicesResponse(OrderRequest request) {
		throw RestClient.createUnavailableMethod();
	}

}
