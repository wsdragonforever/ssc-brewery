package guru.sfg.brewery.web.controllers.api;

import guru.sfg.brewery.web.controllers.BaseIT;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// @WebMvcTest
@SpringBootTest
public class BeerRestControllerIT extends BaseIT {

    @Test
    void deleteBeerBadCreds() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/0e0ea3da-71e6-4395-a8c4-27e2a563fc10")
            .header("Api-Key", "spring").header("Api-Secret", "gruexxx"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteBeer() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/0e0ea3da-71e6-4395-a8c4-27e2a563fc10")
        .header("Api-Key", "spring")
        .header("Api-Secret", "guru"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteBeerHttpBasic() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/0e0ea3da-71e6-4395-a8c4-27e2a563fc10")
        .with(httpBasic("spring", "guru")))
        .andExpect(status().is2xxSuccessful());
    }

    @Test
    void deleteBeerNoAuth() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/0e0ea3da-71e6-4395-a8c4-27e2a563fc10"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findBeers() throws Exception {
        mockMvc.perform(get("/api/v1/beer/"))
                .andExpect(status().isOk());
    }

//    @Test
//    void findBeerById() throws Exception {
//        mockMvc.perform(get("/api/v1/beer/0e0ea3da-71e6-4395-a8c4-27e2a563fc10"))
//                .andExpect(status().isOk());
//    }

    @Test
    void findBeerByUpc() throws Exception {
        mockMvc.perform(get("/api/v1/beerUpc/0631234200036"))
                .andExpect(status().isOk());
    }
}
