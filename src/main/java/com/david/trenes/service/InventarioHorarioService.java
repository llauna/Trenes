package com.david.trenes.service;

import com.david.trenes.model.Horario;
import com.david.trenes.model.InventarioHorario;
import com.david.trenes.model.Tren;
import com.david.trenes.repository.InventarioHorarioRepository;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.CONFLICT;

@Service
@RequiredArgsConstructor
public class InventarioHorarioService {

    public enum Clase {
        TURISTA, PRIMERA
    }

    private final InventarioHorarioRepository inventarioHorarioRepository;
    private final MongoTemplate mongoTemplate;

    public InventarioHorario ensureInventario(Horario horario, Tren tren) {
        String horarioId = horario.getId();

        return inventarioHorarioRepository.findById(horarioId).orElseGet(() -> {
            int capTurista = calcularCapacidadDesdeVagones(tren, Clase.TURISTA);
            int capPrimera = calcularCapacidadDesdeVagones(tren, Clase.PRIMERA);

            InventarioHorario inv = InventarioHorario.builder()
                    .horarioId(horarioId)
                    .trenId(tren.getId())
                    .capacidadTurista(capTurista)
                    .capacidadPrimera(capPrimera)
                    .vendidosTurista(0)
                    .vendidosPrimera(0)
                    .fechaCreacion(LocalDateTime.now())
                    .fechaActualizacion(LocalDateTime.now())
                    .build();

            return inventarioHorarioRepository.save(inv);
        });
    }

    public boolean intentarVender(String horarioId, Clase clase, int cantidad) {
        if (cantidad <= 0) return true;

        String vendidosField = (clase == Clase.PRIMERA) ? "vendidos_primera" : "vendidos_turista";
        String capacidadField = (clase == Clase.PRIMERA) ? "capacidad_primera" : "capacidad_turista";

        Document queryDoc = new Document("_id", horarioId)
                .append("$expr", new Document("$lte", java.util.List.of(
                        new Document("$add", java.util.List.of("$" + vendidosField, cantidad)),
                        "$" + capacidadField
                )));

        Update update = new Update()
                .inc(vendidosField, cantidad)
                .set("fecha_actualizacion", LocalDateTime.now());

        var query = new BasicQuery(queryDoc);

        InventarioHorario modified = mongoTemplate.findAndModify(
                query,
                update,
                FindAndModifyOptions.options().returnNew(true),
                InventarioHorario.class
        );

        return modified != null;
    }

    public void compensarVenta(String horarioId, Clase clase, int cantidad) {
        if (cantidad <= 0) return;

        String vendidosField = (clase == Clase.PRIMERA) ? "vendidos_primera" : "vendidos_turista";

        // Compensación "best effort": decrementa sin condición.
        // Si quieres impedir negativos estrictamente, se puede añadir $expr (vendidos >= cantidad).
        Update update = new Update()
                .inc(vendidosField, -cantidad)
                .set("fecha_actualizacion", LocalDateTime.now());

        mongoTemplate.updateFirst(
                new BasicQuery(new Document("_id", horarioId)),
                update,
                InventarioHorario.class
        );
    }

    public void incrementarCapacidad(String horarioId, Clase clase, int delta) {
        if (delta <= 0) return;

        String capacidadField = (clase == Clase.PRIMERA) ? "capacidad_primera" : "capacidad_turista";

        Update update = new Update()
                .inc(capacidadField, delta)
                .set("fecha_actualizacion", LocalDateTime.now());

        mongoTemplate.updateFirst(
                new BasicQuery(new Document("_id", horarioId)),
                update,
                InventarioHorario.class
        );
    }

    public InventarioHorario getInventarioOrThrow(String horarioId) {
        return inventarioHorarioRepository.findById(horarioId)
                .orElseThrow(() -> new ResponseStatusException(CONFLICT, "Inventario no inicializado para este horario"));
    }

    private int calcularCapacidadDesdeVagones(Tren tren, Clase clase) {
        if (tren == null || tren.getVagones() == null) return 0;

        return tren.getVagones().stream()
                .filter(v -> Boolean.TRUE.equals(v.getActivo()))
                .filter(v -> {
                    if (clase == Clase.PRIMERA) return v.getTipo() == Tren.TipoVagon.PASAJEROS_PRIMERA;
                    return v.getTipo() == Tren.TipoVagon.PASAJEROS_TURISTA;
                })
                .map(v -> v.getCapacidadPasajeros() == null ? 0 : v.getCapacidadPasajeros())
                .mapToInt(Integer::intValue)
                .sum();
    }
}
