package com.jayanith.wallet;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Two or more test cases per functionality. Each test prints its Input and Output
 * so the console log doubles as the test-case evidence (see docs/screenshots).
 */
@SpringBootTest
@AutoConfigureMockMvc
class WalletApiTest {

    @Autowired
    MockMvc mvc;

    private MvcResult call(String label, String username, MockHttpServletRequestBuilder req, String body) throws Exception {
        if (body != null) req.contentType(MediaType.APPLICATION_JSON).content(body);
        MvcResult r = mvc.perform(req.with(user(username))).andReturn();
        System.out.println("[" + label + "] INPUT  : user=" + username + " " + r.getRequest().getMethod() + " "
                + r.getRequest().getRequestURI() + (body != null ? " body=" + body : ""));
        System.out.println("[" + label + "] OUTPUT : HTTP " + r.getResponse().getStatus() + " " + r.getResponse().getContentAsString());
        return r;
    }

    private void add(String u, String amount) throws Exception {
        mvc.perform(post("/api/wallet/add").with(user(u)).contentType(MediaType.APPLICATION_JSON)
                .content("{\"amount\":" + amount + "}")).andExpect(status().isOk());
    }

    @Nested
    @DisplayName("1. View current balance")
    class Balance {
        @Test
        @DisplayName("TC1.1 New user sees balance 0")
        void newUserZero() throws Exception {
            call("TC1.1", "user_b1", get("/api/wallet/balance"), null);
            mvc.perform(get("/api/wallet/balance").with(user("user_b1")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("user_b1"))
                    .andExpect(jsonPath("$.balance").value(0));
        }

        @Test
        @DisplayName("TC1.2 Balance reflects deposits and withdrawals")
        void balanceAfterOps() throws Exception {
            add("user_b2", "1000");
            mvc.perform(post("/api/wallet/withdraw").with(user("user_b2")).contentType(MediaType.APPLICATION_JSON)
                    .content("{\"amount\":250.50}")).andExpect(status().isOk());
            call("TC1.2", "user_b2", get("/api/wallet/balance"), null);
            mvc.perform(get("/api/wallet/balance").with(user("user_b2")))
                    .andExpect(jsonPath("$.balance").value(749.50));
        }

        @Test
        @DisplayName("TC1.3 Unauthenticated request is rejected")
        void unauthenticated() throws Exception {
            MvcResult r = mvc.perform(get("/api/wallet/balance")).andReturn();
            System.out.println("[TC1.3] INPUT  : no login GET /api/wallet/balance");
            System.out.println("[TC1.3] OUTPUT : HTTP " + r.getResponse().getStatus() + " (redirect to login / unauthorized)");
            org.junit.jupiter.api.Assertions.assertTrue(r.getResponse().getStatus() == 302 || r.getResponse().getStatus() == 401);
        }
    }

    @Nested
    @DisplayName("2. Add money")
    class AddMoney {
        @Test
        @DisplayName("TC2.1 Add 500 to empty wallet gives 500")
        void addValid() throws Exception {
            call("TC2.1", "user_a1", post("/api/wallet/add"), "{\"amount\":500}");
            mvc.perform(get("/api/wallet/balance").with(user("user_a1")))
                    .andExpect(jsonPath("$.balance").value(500));
        }

        @Test
        @DisplayName("TC2.2 Two deposits accumulate (200 + 300.75 = 500.75)")
        void addTwice() throws Exception {
            add("user_a2", "200");
            MvcResult r = call("TC2.2", "user_a2", post("/api/wallet/add"), "{\"amount\":300.75}");
            org.junit.jupiter.api.Assertions.assertTrue(r.getResponse().getContentAsString().contains("500.75"));
        }

        @Test
        @DisplayName("TC2.3 Negative amount is rejected with 400")
        void addNegative() throws Exception {
            MvcResult r = call("TC2.3", "user_a3", post("/api/wallet/add"), "{\"amount\":-100}");
            org.junit.jupiter.api.Assertions.assertEquals(400, r.getResponse().getStatus());
        }

        @Test
        @DisplayName("TC2.4 Zero / missing amount is rejected with 400")
        void addZero() throws Exception {
            MvcResult r = call("TC2.4", "user_a4", post("/api/wallet/add"), "{\"amount\":0}");
            org.junit.jupiter.api.Assertions.assertEquals(400, r.getResponse().getStatus());
            MvcResult r2 = call("TC2.4b", "user_a4", post("/api/wallet/add"), "{}");
            org.junit.jupiter.api.Assertions.assertEquals(400, r2.getResponse().getStatus());
        }
    }

    @Nested
    @DisplayName("3. Withdraw money")
    class Withdraw {
        @Test
        @DisplayName("TC3.1 Withdraw 300 from 1000 leaves 700")
        void withdrawValid() throws Exception {
            add("user_w1", "1000");
            MvcResult r = call("TC3.1", "user_w1", post("/api/wallet/withdraw"), "{\"amount\":300}");
            org.junit.jupiter.api.Assertions.assertEquals(200, r.getResponse().getStatus());
            mvc.perform(get("/api/wallet/balance").with(user("user_w1")))
                    .andExpect(jsonPath("$.balance").value(700));
        }

        @Test
        @DisplayName("TC3.2 Withdraw more than balance is rejected and balance unchanged")
        void withdrawInsufficient() throws Exception {
            add("user_w2", "100");
            MvcResult r = call("TC3.2", "user_w2", post("/api/wallet/withdraw"), "{\"amount\":150}");
            org.junit.jupiter.api.Assertions.assertEquals(400, r.getResponse().getStatus());
            mvc.perform(get("/api/wallet/balance").with(user("user_w2")))
                    .andExpect(jsonPath("$.balance").value(100));
        }

        @Test
        @DisplayName("TC3.3 Withdraw exact full balance leaves 0")
        void withdrawAll() throws Exception {
            add("user_w3", "250");
            call("TC3.3", "user_w3", post("/api/wallet/withdraw"), "{\"amount\":250}");
            mvc.perform(get("/api/wallet/balance").with(user("user_w3")))
                    .andExpect(jsonPath("$.balance").value(0));
        }
    }

    @Nested
    @DisplayName("4. Transaction history (optional)")
    class History {
        @Test
        @DisplayName("TC4.1 New user has empty history")
        void emptyHistory() throws Exception {
            call("TC4.1", "user_h1", get("/api/wallet/transactions"), null);
            mvc.perform(get("/api/wallet/transactions").with(user("user_h1")))
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("TC4.2 History lists transactions newest first; failed withdrawal not recorded")
        void historyOrder() throws Exception {
            add("user_h2", "1000");
            mvc.perform(post("/api/wallet/withdraw").with(user("user_h2")).contentType(MediaType.APPLICATION_JSON)
                    .content("{\"amount\":400}"));
            mvc.perform(post("/api/wallet/withdraw").with(user("user_h2")).contentType(MediaType.APPLICATION_JSON)
                    .content("{\"amount\":5000}"));
            call("TC4.2", "user_h2", get("/api/wallet/transactions"), null);
            mvc.perform(get("/api/wallet/transactions").with(user("user_h2")))
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].type").value("DEBIT"))
                    .andExpect(jsonPath("$[0].balanceAfter").value(600))
                    .andExpect(jsonPath("$[1].type").value("CREDIT"));
        }

        @Test
        @DisplayName("TC4.3 Users only see their own transactions")
        void isolation() throws Exception {
            add("user_h3", "50");
            call("TC4.3", "user_h4", get("/api/wallet/transactions"), null);
            mvc.perform(get("/api/wallet/transactions").with(user("user_h4")))
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }
}
