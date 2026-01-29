package no.chickendirect.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.chickendirect.address.Address;
import no.chickendirect.address.AddressService;
import no.chickendirect.customer.Customer;
import no.chickendirect.customer.CustomerService;
import no.chickendirect.customer.dto.CustomerSummaryResponse;
import no.chickendirect.exception.OrderNotFoundException;
import no.chickendirect.order.dto.OrderRequest;
import no.chickendirect.order.dto.OrderResponse;
import no.chickendirect.order.dto.OrderUpdateRequest;
import no.chickendirect.orderitem.OrderItem;
import no.chickendirect.orderitem.dto.OrderItemResponse;
import no.chickendirect.product.Product;
import no.chickendirect.product.dto.ProductResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;

    private final CustomerService customerService;
    private final AddressService addressService;

    public Order getOrderEntity(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    public OrderResponse createOrder(OrderRequest request) {
        log.info("Creating order for customer id={}", request.customerId());

        Customer customer = customerService.getCustomerEntity(request.customerId());

        Address shippingAddress = addressService.getAddressEntity(request.shippingAddressId());

        Order order = Order.builder()
                .customer(customer)
                .customerName(customer.getName())
                .customerEmail(customer.getEmail())
                .customerPhone(customer.getPhone())
                .shippingStreet(shippingAddress.getStreet())
                .shippingCity(shippingAddress.getCity())
                .shippingPostalCode(shippingAddress.getPostalCode())
                .shippingCountry(shippingAddress.getCountry())
                .totalPrice(request.totalPrice())
                .shippingCharge(request.shippingCharge())
                .isShipped(Boolean.TRUE.equals(request.isShipped()))
                .build();

        Order saved = orderRepository.save(order);
        return toOrderResponse(saved);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long id) {
        Order order = getOrderEntity(id);
        return toOrderResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::toOrderResponse)
                .toList();
    }

    public OrderResponse updateOrder(Long id, OrderUpdateRequest request) {
        log.info("Updating order with id={}, isShipped={}", id, request.isShipped());
        Order order = getOrderEntity(id);
        
        if (request.totalPrice() != null) {
            order.setTotalPrice(request.totalPrice());
        }
        if (request.shippingCharge() != null) {
            order.setShippingCharge(request.shippingCharge());
        }
        order.setIsShipped(request.isShipped());
        
        Order updated = orderRepository.save(order);
        return toOrderResponse(updated);
    }

    public void deleteOrder(Long id) {
        log.info("Deleting order with id={}", id);
        if (!orderRepository.existsById(id)) {
            throw new OrderNotFoundException(id);
        }
        orderRepository.deleteById(id);
    }

    private OrderResponse toOrderResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                new CustomerSummaryResponse(
                        order.getCustomer() != null ? order.getCustomer().getId() : null,
                        order.getCustomerName(),
                        order.getCustomerEmail()
                ),
                order.getShippingStreet(),
                order.getShippingCity(),
                order.getShippingPostalCode(),
                order.getShippingCountry(),
                order.getTotalPrice(),
                order.getShippingCharge(),
                order.getIsShipped(),
                order.getCreationDate(),
                order.getItems().stream().map(this::toOrderItemResponse).toList()
        );
    }

    private OrderItemResponse toOrderItemResponse(OrderItem orderItem) {
        BigDecimal lineTotal = orderItem.getProduct()
                .getPrice()
                .multiply(BigDecimal.valueOf(orderItem.getQuantity()));

        return new OrderItemResponse(
                orderItem.getId(),
                orderItem.getQuantity(),
                toProductResponse(orderItem.getProduct()),
                orderItem.getOrder().getId(),
                lineTotal
        );
    }


    private ProductResponse toProductResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStatus(),
                product.getQuantityOnHand()
        );
    }
}
