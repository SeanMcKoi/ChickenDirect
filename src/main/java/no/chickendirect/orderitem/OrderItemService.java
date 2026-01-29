package no.chickendirect.orderitem;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.chickendirect.exception.OrderItemNotFoundException;
import no.chickendirect.order.Order;
import no.chickendirect.order.OrderService;
import no.chickendirect.orderitem.dto.OrderItemRequest;
import no.chickendirect.orderitem.dto.OrderItemResponse;
import no.chickendirect.product.Product;
import no.chickendirect.product.ProductService;
import no.chickendirect.product.dto.ProductResponse;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;

    private final ProductService productService;
    private final OrderService orderService;

    public OrderItemResponse createOrderItem(OrderItemRequest request) {
        log.info("Creating order item for product id={}", request.productId());

        Product product = productService.getProductEntity(request.productId());

        Order order = orderService.getOrderEntity(request.orderId());

        OrderItem orderItem = OrderItem.builder()
                .quantity(request.quantity())
                .product(product)
                .order(order)
                .build();

        OrderItem saved = orderItemRepository.save(orderItem);
        return toOrderItemResponse(saved);
    }

    @Transactional(readOnly = true)
    public OrderItemResponse getOrderItem(Long id) {
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new OrderItemNotFoundException(id));
        return toOrderItemResponse(orderItem);
    }

    @Transactional(readOnly = true)
    public List<OrderItemResponse> getAllOrderItems() {
        return orderItemRepository.findAll().stream()
                .map(this::toOrderItemResponse)
                .toList();
    }

    public void deleteOrderItem(Long id) {
        log.info("Deleting order item with id={}", id);
        if (!orderItemRepository.existsById(id)) {
            throw new OrderItemNotFoundException(id);
        }
        orderItemRepository.deleteById(id);
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
