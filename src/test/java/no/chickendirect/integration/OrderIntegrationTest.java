package no.chickendirect.integration;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.chickendirect.address.dto.AddressRequest;
import no.chickendirect.customer.dto.CustomerRequest;
import no.chickendirect.order.dto.OrderRequest;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class OrderIntegrationTest {

    @Container
    @SuppressWarnings("resource")
    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("chicken_direct")
                    .withUsername("postgres")
                    .withPassword("postgres");

    @DynamicPropertySource
    @SuppressWarnings("unused")
    static void configureDataSource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> true);
    }

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Long customerId;
    private Long addressId;

    @BeforeEach
    void setupData() throws Exception {
        // Create Customer
        String email = "order_test_" + java.util.UUID.randomUUID() + "@example.com";
        CustomerRequest customerRequest = new CustomerRequest("Order Customer", "11111111", email);
        String customerResponse = mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        customerId = objectMapper.readTree(customerResponse).get("id").asLong();

        // Create Address
        AddressRequest addressRequest = new AddressRequest("Order Street", "Order City", "1234", "Country");
        String addressResponse = mockMvc.perform(post("/api/customers/{id}/addresses", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addressRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        addressId = objectMapper.readTree(addressResponse).get("id").asLong();
    }

    @Test
    void createOrder_thenGetOrder() throws Exception {
        OrderRequest orderRequest = new OrderRequest(
                customerId,
                addressId,
                BigDecimal.valueOf(500),
                BigDecimal.valueOf(50),
                false
        );

        String response = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.totalPrice").value(500))
                .andReturn().getResponse().getContentAsString();

        Long orderId = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(get("/api/orders/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customer.id").value(customerId));
    }

    @Test
    void deleteCustomer_withOrders_shouldSucceedAndKeepOrder() throws Exception {
        // Create Order
        OrderRequest orderRequest = new OrderRequest(
                customerId,
                addressId,
                BigDecimal.valueOf(500),
                BigDecimal.valueOf(50),
                false
        );

        String response = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long orderId = objectMapper.readTree(response).get("id").asLong();

        // Delete Customer
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/customers/{id}", customerId))
                .andExpect(status().isNoContent());

        // Check if Order still exists with customer info
        mockMvc.perform(get("/api/orders/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customer.id").isEmpty())
                .andExpect(jsonPath("$.customer.name").value("Order Customer"))
                .andExpect(jsonPath("$.customer.email").exists());
    }

    @Test
    void getOrder_notFound_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/orders/{id}", 9999L))
                .andExpect(status().isNotFound());
    }
}
