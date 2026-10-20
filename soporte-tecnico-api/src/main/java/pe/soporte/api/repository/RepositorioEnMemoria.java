package pe.soporte.api.repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Almacenamiento genérico en memoria que simula una base de datos.
 * <p>
 * Usa un {@link ConcurrentHashMap} (seguro ante peticiones concurrentes) donde la
 * clave es el id y un {@link AtomicLong} que genera ids autoincrementales.
 * Los datos se pierden al detener la aplicación.
 *
 * @param <T> tipo de entidad almacenada
 */
public abstract class RepositorioEnMemoria<T> {

    private final Map<Long, T> datos = new ConcurrentHashMap<>();
    private final AtomicLong secuencia = new AtomicLong(0);

    protected abstract Long obtenerId(T entidad);

    protected abstract void asignarId(T entidad, Long id);

    /** Inserta (si no tiene id) o reemplaza (si ya tiene id) la entidad. */
    public T guardar(T entidad) {
        if (obtenerId(entidad) == null) {
            asignarId(entidad, secuencia.incrementAndGet());
        }
        datos.put(obtenerId(entidad), entidad);
        return entidad;
    }

    public Optional<T> buscarPorId(Long id) {
        return Optional.ofNullable(datos.get(id));
    }

    /** Devuelve una copia de todos los registros ordenada por id. */
    public List<T> listarTodos() {
        List<T> lista = new ArrayList<>(datos.values());
        lista.sort(Comparator.comparing(this::obtenerId));
        return lista;
    }

    public boolean existePorId(Long id) {
        return datos.containsKey(id);
    }

    public void eliminarPorId(Long id) {
        datos.remove(id);
    }

    public long contar() {
        return datos.size();
    }
}
