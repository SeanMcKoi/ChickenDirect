package no.chickendirect.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.chickendirect.address.dto.AddressRequest;
import no.chickendirect.customer.dto.CustomerRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class CustomerAddressIntegrationTest {

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

    private Long createdCustomerId;

    @BeforeEach
    @SuppressWarnings("unused")
    void setupCustomer() throws Exception {
        String email = "test+" + java.util.UUID.randomUUID() + "@example.com";

        CustomerRequest request = new CustomerRequest("John", "12345678", email);

        String response = mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        createdCustomerId = objectMapper.readTree(response).get("id").asLong();
    }


    @Test
    void createCustomer_addAddress_thenGetCustomer() throws Exception {
        AddressRequest addressRequest = new AddressRequest(
                "Street 1", "Oslo", "0001", "Norway"
        );

        mockMvc.perform(post("/api/customers/{id}/addresses", createdCustomerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addressRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.city").value("Oslo"));

        mockMvc.perform(get("/api/customers/{id}", createdCustomerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdCustomerId))
                .andExpect(jsonPath("$.addresses", hasSize(1)))
                .andExpect(jsonPath("$.orders", hasSize(0)));
    }

    @Test
    void getCustomer_notFound_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/customers/{id}", 9999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Customer not found"));
    }
}
