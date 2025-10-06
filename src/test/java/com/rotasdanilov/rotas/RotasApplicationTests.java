package com.rotasdanilov.rotas;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rotasdanilov.rotas.dto.ClienteRequest;
import com.rotasdanilov.rotas.dto.RotaRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RotasApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void deveCadastrarListarEOtimizarRota() throws Exception {
        String primeiro = mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new ClienteRequest(
                                "Cliente A", "Rua 1", -23.5505, -46.6333))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome", is("Cliente A")))
                .andReturn().getResponse().getContentAsString();

        String segundo = mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new ClienteRequest(
                                "Cliente B", "Rua 2", -22.9068, -43.1729))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String terceiro = mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new ClienteRequest(
                                "Cliente C", "Rua 3", -25.4284, -49.2733))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String primeiroId = mapper.readTree(primeiro).get("id").asText();
        String segundoId = mapper.readTree(segundo).get("id").asText();
        String terceiroId = mapper.readTree(terceiro).get("id").asText();

        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));

        mockMvc.perform(post("/rotas/otimizar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new RotaRequest(List.of(primeiroId, segundoId, terceiroId)))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sequencia", hasSize(3)))
                .andExpect(jsonPath("$.distanciaTotalKm").exists())
                .andExpect(jsonPath("$.tempoEstimadoHoras").exists());
    }
}
