package pe.soporte.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de integración de la API (levantan el contexto completo de Spring
 * y ejecutan peticiones HTTP simuladas con MockMvc).
 * Ejecutar con: mvn test
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SolicitudApiTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private long clienteId;
    private long tecnicoDisponibleId;
    private long tecnicoNoDisponibleId;

    @BeforeEach
    void prepararDatos() throws Exception {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        clienteId = crear("/api/clientes", """
                {"nombre":"Cliente Prueba","email":"cliente%s@test.pe","telefono":"999888777","empresa":"Test SAC"}
                """.formatted(sufijo));
        tecnicoDisponibleId = crear("/api/tecnicos", """
                {"nombre":"Técnico Uno","email":"tec1%s@test.pe","especialidad":"Redes","disponible":true}
                """.formatted(sufijo));
        tecnicoNoDisponibleId = crear("/api/tecnicos", """
                {"nombre":"Técnico Dos","email":"tec2%s@test.pe","especialidad":"Software","disponible":false}
                """.formatted(sufijo));
    }

    @Test
    void registrarSolicitudValidaDevuelve201EnEstadoAbierta() throws Exception {
        mvc.perform(post("/api/solicitudes").contentType(MediaType.APPLICATION_JSON)
                        .content(solicitudJson(clienteId, null)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.estado").value("ABIERTA"))
                .andExpect(jsonPath("$.clienteId").value(clienteId));
    }

    @Test
    void registrarSolicitudSinTituloDevuelve400ConDetalle() throws Exception {
        String json = """
                {"descripcion":"Descripción suficientemente larga","prioridad":"ALTA","clienteId":%d}
                """.formatted(clienteId);
        mvc.perform(post("/api/solicitudes").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detalles.titulo").exists());
    }

    @Test
    void prioridadInexistenteDevuelve400() throws Exception {
        String json = """
                {"titulo":"Título válido","descripcion":"Descripción suficientemente larga",
                 "prioridad":"URGENTE","clienteId":%d}
                """.formatted(clienteId);
        mvc.perform(post("/api/solicitudes").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void clienteInexistenteDevuelve404() throws Exception {
        mvc.perform(post("/api/solicitudes").contentType(MediaType.APPLICATION_JSON)
                        .content(solicitudJson(99999L, null)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Cliente con id 99999 no encontrado"));
    }

    @Test
    void tecnicoNoDisponibleDevuelve409() throws Exception {
        mvc.perform(post("/api/solicitudes").contentType(MediaType.APPLICATION_JSON)
                        .content(solicitudJson(clienteId, tecnicoNoDisponibleId)))
                .andExpect(status().isConflict());
    }

    @Test
    void cicloCompletoActualizarCambiarEstadoYEliminar() throws Exception {
        long id = crear("/api/solicitudes", solicitudJson(clienteId, null));

        // No puede pasar a EN_PROCESO sin técnico
        mvc.perform(patch("/api/solicitudes/{id}/estado", id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"EN_PROCESO\"}"))
                .andExpect(status().isConflict());

        // Asignar técnico y pasar a EN_PROCESO
        mvc.perform(patch("/api/solicitudes/{id}/tecnico", id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tecnicoId\":" + tecnicoDisponibleId + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tecnicoId").value(tecnicoDisponibleId));
        mvc.perform(patch("/api/solicitudes/{id}/estado", id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"EN_PROCESO\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("EN_PROCESO"));

        // PUT completo cambiando prioridad
        String actualizado = """
                {"titulo":"Título actualizado","descripcion":"Descripción actualizada del problema",
                 "prioridad":"CRITICA","clienteId":%d,"tecnicoId":%d}
                """.formatted(clienteId, tecnicoDisponibleId);
        mvc.perform(put("/api/solicitudes/{id}", id).contentType(MediaType.APPLICATION_JSON).content(actualizado))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prioridad").value("CRITICA"))
                .andExpect(jsonPath("$.estado").value("EN_PROCESO"));

        // Transición inválida EN_PROCESO -> CERRADA
        mvc.perform(patch("/api/solicitudes/{id}/estado", id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"CERRADA\"}"))
                .andExpect(status().isConflict());

        // Eliminar y comprobar 404
        mvc.perform(delete("/api/solicitudes/{id}", id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/solicitudes/{id}", id)).andExpect(status().isNotFound());
    }

    @Test
    void filtrarPorClienteSoloDevuelveSusSolicitudes() throws Exception {
        crear("/api/solicitudes", solicitudJson(clienteId, null));
        mvc.perform(get("/api/solicitudes").param("clienteId", String.valueOf(clienteId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].clienteId").value(clienteId));
    }

    @Test
    void idNoNumericoDevuelve400() throws Exception {
        mvc.perform(get("/api/solicitudes/abc")).andExpect(status().isBadRequest());
    }

    @Test
    void emailDuplicadoDeClienteDevuelve409() throws Exception {
        String email = mapper.readTree(mvc.perform(get("/api/clientes/{id}", clienteId))
                .andReturn().getResponse().getContentAsString()).get("email").asText();
        String json = """
                {"nombre":"Otro Cliente","email":"%s","telefono":"911222333"}
                """.formatted(email);
        mvc.perform(post("/api/clientes").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isConflict());
    }

    // ---------- utilidades ----------

    private String solicitudJson(Long clienteId, Long tecnicoId) {
        return """
                {"titulo":"Laptop no enciende","descripcion":"La laptop del gerente no enciende desde esta mañana",
                 "prioridad":"ALTA","clienteId":%d%s}
                """.formatted(clienteId, tecnicoId == null ? "" : ",\"tecnicoId\":" + tecnicoId);
    }

    private long crear(String url, String json) throws Exception {
        MvcResult r = mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode nodo = mapper.readTree(r.getResponse().getContentAsString());
        return nodo.get("id").asLong();
    }
}
