package com.tfg.ms_inventory.Service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import main.java.com.tfg.ms_inventory.DTO.StockDTO;

import com.tfg.ms_inventory.Repository.StockRepository;
import com.tfg.ms_inventory.Repository.MovimientoRepository;
import com.tfg.ms_inventory.Model.Stock;
import com.tfg.ms_inventory.Model.Movimiento;
import com.tfg.ms_inventory.DTO.MovimientoRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class InventarioService {
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private MovimientoRepository movimientoRepository;

    public void crearStockInicial(Long productoId) {
        Stock stock = new Stock();
        stock.setProductoId(productoId);
        stock.setCantidadDisponible(0);
        stockRepository.save(stock);
    }

    public MovimientoDTO procesarMovimiento(MovimientoRequestDTO movimientoRequest) {

    }

    /*
     * egún el FDD, el servicio de inventario (InventarioService) debe ser capaz de
     * gestionar las entradas/salidas de stock y permitir las consultas.
     * 
     * Aquí tienes la lista exacta de los métodos que debes añadir a tu
     * InventarioService y lo que debe hacer cada uno:
     * 
     * 1. Método para Procesar Movimientos (El más importante)
     * Debes crear un método transaccional que reciba el DTO que acabas de crear y
     * el email del usuario. Debe servir tanto para ENTRADA como para SALIDA.
     * 
     * java
     * 
     * @Transactional
     * public MovimientoDTO procesarMovimiento(MovimientoRequestDTO dto, String
     * usuarioEmail) {
     * // 1. Buscar el stock actual del producto (usando
     * stockRepository.findByProductoId).
     * // Si no existe, puedes crearlo en ese momento con cantidad 0 usando el
     * método que ya tienes.
     * 
     * // 2. Comprobar el tipo ("ENTRADA" o "SALIDA") y actualizar la cantidad:
     * // - Si es ENTRADA: sumas la cantidad.
     * // - Si es SALIDA: restas la cantidad. ¡OJO! Aquí debes hacer un 'if' para
     * lanzar
     * // una excepción (por ejemplo, RuntimeException("Stock insuficiente")) si la
     * // cantidad disponible es menor que la cantidad que intentan sacar.
     * // 3. Guardar el objeto Stock actualizado en la base de datos.
     * // 4. Crear el nuevo objeto Movimiento:
     * // - Rellenar productoId, proveedorId, cantidad, tipo.
     * // - fecha = LocalDateTime.now()
     * // - usuarioEmail = el que recibes por parámetro
     * // - stockResultante = la cantidad que ha quedado en el objeto Stock tras la
     * suma/resta.
     * // 5. Guardar el Movimiento en la base de datos.
     * // 6. Simular auditoría:
     * System.out.println("Enviando a RabbitMQ el movimiento...");
     * // 7. Devolver el MovimientoDTO (usando tu método mapToMovimientoDTO).
     * }
     * 2. Método para Consultar Stock
     * El FDD especifica: "Consultar el stock disponible de un producto".
     * 
     * java
     * public StockDTO consultarStock(Long productoId) {
     * // Buscar el stock por productoId.
     * // Si no existe, puedes lanzar una excepción o devolver un StockDTO con
     * cantidad 0.
     * // Retornar el mapeo a DTO.
     * }
     * 3. Método para Consultar el Historial de Movimientos
     * El FDD especifica: "Consultar el listado de movimientos históricos".
     * 
     * java
     * public List<MovimientoDTO> obtenerMovimientos() {
     * // Usar movimientoRepository.findAll()
     * // Mapear la lista de Movimiento a MovimientoDTO y retornarla.
     * }
     * // Opcional pero recomendado por el FDD:
     * public List<MovimientoDTO> obtenerMovimientosPorProducto(Long productoId) {
     * // Necesitarás añadir un método findByProductoId en tu MovimientoRepository.
     * // Luego devuelves la lista mapeada.
     * }
     * ⚠️ Un par de detalles importantes en tu código actual:
     * Asegúrate de importar @Transactional (el de
     * org.springframework.transaction.annotation.Transactional) encima del método
     * procesarMovimiento para que, si da un error de stock negativo, no se guarde
     * ni el stock ni el movimiento a medias.
     * Veo que usaste la clase StockDTO y MovimientoDTO en los métodos de mapeo,
     * pero te faltan sus correspondientes import com.tfg.ms_inventory.DTO.StockDTO;
     * arriba.
     * Te faltará importar java.util.Optional en el Repositorio de Stock que acabas
     * de crear.
     * Sigue esa estructura para el método procesarMovimiento paso a paso y tendrás
     * el 90% del microservicio hecho. ¡Dale caña y me dices si algún paso se te
     * atasca!
     */

    // Mapeos a DTO
    public StockDTO mapToStockDTO(Stock stock) {
        return new StockDTO(stock.getProductoId(), stock.getCantidadDisponible());
    }

    public MovimientoDTO mapToMovimientoDTO(Movimiento movimiento) {
        return new MovimientoDTO(movimiento.getId(), movimiento.getProductoId(), movimiento.getProveedorId(),
                movimiento.getUsuarioEmail(), movimiento.getCantidad(), movimiento.getTipo(), movimiento.getFecha(),
                movimiento.getStockResultante());
    }

}
