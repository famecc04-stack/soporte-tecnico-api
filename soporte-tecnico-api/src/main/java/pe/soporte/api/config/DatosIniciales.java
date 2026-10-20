package pe.soporte.api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import pe.soporte.api.dto.SolicitudRequest;
import pe.soporte.api.model.Cliente;
import pe.soporte.api.model.Prioridad;
import pe.soporte.api.model.Tecnico;
import pe.soporte.api.service.ClienteService;
import pe.soporte.api.service.SolicitudService;
import pe.soporte.api.service.TecnicoService;

/**
 * Carga datos de ejemplo en memoria al iniciar la aplicación para facilitar las pruebas.
 * Se desactiva en el perfil "test".
 */
@Component
@Profile("!test")
public class DatosIniciales implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatosIniciales.class);

    private final ClienteService clienteService;
    private final TecnicoService tecnicoService;
    private final SolicitudService solicitudService;

    public DatosIniciales(ClienteService clienteService, TecnicoService tecnicoService,
                          SolicitudService solicitudService) {
        this.clienteService = clienteService;
        this.tecnicoService = tecnicoService;
        this.solicitudService = solicitudService;
    }

    @Override
    public void run(String... args) {
        // Clientes -> ids 1 y 2
        clienteService.registrar(new Cliente(null, "María López", "maria.lopez@comercialandina.pe",
                "987654321", "Comercial Andina S.A.C."));
        clienteService.registrar(new Cliente(null, "José Quispe", "jose.quispe@ferreteriasur.pe",
                "912345678", "Ferretería del Sur E.I.R.L."));

        // Técnicos -> ids 1, 2 y 3 (el 3 no está disponible)
        tecnicoService.registrar(new Tecnico(null, "Carlos Ramírez", "carlos.ramirez@soportetech.pe",
                "Redes y conectividad", true));
        tecnicoService.registrar(new Tecnico(null, "Ana Torres", "ana.torres@soportetech.pe",
                "Hardware y periféricos", true));
        tecnicoService.registrar(new Tecnico(null, "Luis Mendoza", "luis.mendoza@soportetech.pe",
                "Software y licencias", false));

        // Solicitudes -> ids 1 y 2
        solicitudService.registrar(solicitud("Impresora no imprime en contabilidad",
                "La impresora HP del área de contabilidad muestra error de atasco aunque no hay papel trabado.",
                Prioridad.MEDIA, 1L, 2L));
        solicitudService.registrar(solicitud("Correo corporativo no sincroniza",
                "Desde ayer el correo no sincroniza en los celulares de los vendedores.",
                Prioridad.ALTA, 2L, null));

        log.info("Datos de ejemplo cargados: 2 clientes, 3 técnicos, 2 solicitudes");
    }

    private SolicitudRequest solicitud(String titulo, String descripcion, Prioridad prioridad,
                                       Long clienteId, Long tecnicoId) {
        SolicitudRequest r = new SolicitudRequest();
        r.setTitulo(titulo);
        r.setDescripcion(descripcion);
        r.setPrioridad(prioridad);
        r.setClienteId(clienteId);
        r.setTecnicoId(tecnicoId);
        return r;
    }
}
