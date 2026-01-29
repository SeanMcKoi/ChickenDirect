package no.chickendirect.unit.orderitem;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;

import static org.mockito.ArgumentMatchers.any;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.junit.jupiter.MockitoExtension;

import no.chickendirect.exception.OrderItemNotFoundException;
import no.chickendirect.exception.OrderNotFoundException;
import no.chickendirect.exception.ProductNotFoundException;
import no.chickendirect.order.Order;
import no.chickendirect.order.OrderService;
import no.chickendirect.orderitem.OrderItem;
import no.chickendirect.orderitem.OrderItemRepository;
import no.chickendirect.orderitem.OrderItemService;
import no.chickendirect.orderitem.dto.OrderItemRequest;
import no.chickendirect.orderitem.dto.OrderItemResponse;
import no.chickendirect.product.Product;
import no.chickendirect.product.ProductService;
import no.chickendirect.productstatus.ProductStatus;

@ExtendWith(MockitoExtension.class)
class OrderItemServiceTest {

    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private ProductService productService;
    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderItemService orderItemService;

    @Test
    void createOrderItem_shouldSaveAndReturnResponse() {
        OrderItemRequest request = new OrderItemRequest(1L, 5, 2L);

        Product product = Product.builder()
                .id(1L)
                .name("Product")
                .price(BigDecimal.TEN)
                .status(ProductStatus.IN_STOCK)
                .quantityOnHand(100)
                .build();
        Order order = Order.builder().id(2L).build();

        when(productService.getProductEntity(1L)).thenReturn(product);
        when(orderService.getOrderEntity(2L)).thenReturn(order);

        OrderItem savedItem = OrderItem.builder()
                .id(10L)
                .product(product)
                .order(order)
                .quantity(5)
                .build();

        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(savedItem);

        OrderItemResponse response = orderItemService.createOrderItem(request);

        ArgumentCaptor<OrderItem> captor = ArgumentCaptor.forClass(OrderItem.class);
        verify(orderItemRepository).save(captor.capture());
        OrderItem toSave = captor.getValue();

        assertEquals(product, toSave.getProduct());
        assertEquals(order, toSave.getOrder());
        assertEquals(5, toSave.getQuantity());

        assertEquals(10L, response.id());
        assertEquals(5, response.quantity());
        assertEquals(1L, response.product().id());
        assertEquals(2L, response.orderId());
    }

    @Test
    void createOrderItem_productNotFound_shouldThrowException() {
        OrderItemRequest request = new OrderItemRequest(99L, 5, 2L);
        when(productService.getProductEntity(99L)).thenThrow(new ProductNotFoundException(99L));

        assertThrows(ProductNotFoundException.class, () -> orderItemService.createOrderItem(request));
    }

    @Test
    void createOrderItem_orderNotFound_shouldThrowException() {
        OrderItemRequest request = new OrderItemRequest(1L, 5, 99L);
        Product product = Product.builder().id(1L).build();

        when(productService.getProductEntity(1L)).thenReturn(product);
        when(orderService.getOrderEntity(99L)).thenThrow(new OrderNotFoundException(99L));

        assertThrows(OrderNotFoundException.class, () -> orderItemService.createOrderItem(request));
    }

    @Test
    void getOrderItem_shouldReturnResponse() {
        Product product = Product.builder()
                .id(1L)
                .name("Product")
                .price(BigDecimal.TEN)
                .status(ProductStatus.IN_STOCK)
                .quantityOnHand(100)
                .build();
        Order order = Order.builder().id(2L).build();
        OrderItem item = OrderItem.builder()
                .id(10L)
                .product(product)
                .order(order)
                .quantity(5)
                .build();

        when(orderItemRepository.findById(10L)).thenReturn(Optional.of(item));

        OrderItemResponse response = orderItemService.getOrderItem(10L);

        assertEquals(10L, response.id());
    }

    @Test
    void getOrderItem_notFound_shouldThrowException() {
        when(orderItemRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(OrderItemNotFoundException.class, () -> orderItemService.getOrderItem(99L));
    }

    @Test
    void deleteOrderItem_shouldDeleteIfFound() {
        when(orderItemRepository.existsById(10L)).thenReturn(true);
        orderItemService.deleteOrderItem(10L);
        verify(orderItemRepository).deleteById(10L);
    }

    @Test
    void deleteOrderItem_notFound_shouldThrowException() {
        when(orderItemRepository.existsById(99L)).thenReturn(false);
        assertThrows(OrderItemNotFoundException.class, () -> orderItemService.deleteOrderItem(99L));
    }
}
