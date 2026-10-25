package com.tfg.ms_inventory.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.tfg.ms_inventory.config.RabbitMQConfig;
import com.tfg.ms_inventory.dto.AuditoriaEventoDTO;
import com.tfg.ms_inventory.dto.MovimientoDTO;
import com.tfg.ms_inventory.dto.MovimientoRequestDTO;
import com.tfg.ms_inventory.dto.StockDTO;
import com.tfg.ms_inventory.model.Movimiento;
import com.tfg.ms_inventory.model.Stock;
import com.tfg.ms_inventory.repository.MovimientoRepository;
import com.tfg.ms_inventory.repository.StockRepository;

@Service
public class InventarioService {

    private final StockRepository stockRepository;
    private final MovimientoRepository movimientoRepository;
    private final RabbitTemplate rabbitTemplate;

    public InventarioService(StockRepository stockRepository,
                             MovimientoRepository movimientoRepository,
                             RabbitTemplate rabbitTemplate) {
        this.stockRepository = stockRepository;
        this.movimientoRepository = movimientoRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    private void auditar(String accion, Long entidadId, String usuarioEmail, String detalles, Long productoId, Long proveedorId, Integer cantidad, Integer stockResultante) {
        AuditoriaEventoDTO evento = new AuditoriaEventoDTO(
                "INVENTORY", accion, entidadId, usuarioEmail, LocalDateTime.now(), detalles,
                productoId, proveedorId, cantidad, stockResultante);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_AUDITORIA,
                RabbitMQConfig.ROUTING_KEY_AUDITORIA,
                evento
        );
    }

    public Stock crearStockInicial(Long productoId) {
        Stock stock = new Stock();
        stock.setProductoId(productoId);
        stock.setCantidadDisponible(0);
        stockRepository.save(stock);
        return stock;
    }

    @Transactional
    public MovimientoDTO procesarMovimiento(MovimientoRequestDTO movimientoRequest) {
        String tipo = movimientoRequest.getTipo();
        Integer cantidad = movimientoRequest.getCantidad();

        if (!"ENTRADA".equals(tipo) && !"SALIDA".equals(tipo)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El tipo de movimiento debe ser ENTRADA o SALIDA");
        }
        if (cantidad == null || cantidad <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cantidad debe ser mayor que 0");
        }

        Stock stock = stockRepository.findByProductoId(movimientoRequest.getProductoId())
                .orElseGet(() -> crearStockInicial(movimientoRequest.getProductoId()));

        if (tipo.equals("ENTRADA")) {
            stock.setCantidadDisponible(stock.getCantidadDisponible() + cantidad);
        } else {
            if (stock.getCantidadDisponible() < cantidad) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "No hay stock suficiente para realizar el movimiento");
            }
            stock.setCantidadDisponible(stock.getCantidadDisponible() - cantidad);
        }

        stockRepository.save(stock);
        Movimiento movimientoGenerado = crearMovimiento(movimientoRequest, stock.getCantidadDisponible());
        auditar("POST_MOVIMIENTO", movimientoGenerado.getId(), movimientoRequest.getUsuarioEmail(),
                "Movimiento de stock: " + tipo,
                movimientoRequest.getProductoId(), movimientoRequest.getProveedorId(),
                cantidad, stock.getCantidadDisponible());
        return mapToMovimientoDTO(movimientoGenerado);
    }

    public Movimiento crearMovimiento(MovimientoRequestDTO movimientoRequest, Integer stockFinal) {
        Movimiento movimiento = new Movimiento();
        movimiento.setProductoId(movimientoRequest.getProductoId());
        movimiento.setProveedorId(movimientoRequest.getProveedorId());
        movimiento.setUsuarioEmail(movimientoRequest.getUsuarioEmail());
        movimiento.setCantidad(movimientoRequest.getCantidad());
        movimiento.setTipo(movimientoRequest.getTipo());
        movimiento.setStockResultante(stockFinal);
        movimiento.setFecha(LocalDateTime.now());
        return movimientoRepository.save(movimiento);
    }

    public StockDTO consultarStock(Long productoId, String usuarioEmail) {
        auditar("GET_STOCK", productoId, usuarioEmail, "Consulta de stock", productoId, null, null, null);
        Stock stock = stockRepository.findByProductoId(productoId).orElse(null);
        if (stock == null) {
            return new StockDTO(productoId, 0);
        }
        return mapToStockDTO(stock);
    }

    public List<StockDTO> consultarTodosLosStocks(String usuarioEmail) {
        auditar("GET_ALL_STOCK", null, usuarioEmail, "Consulta de todo el stock", null, null, null, null);
        return stockRepository.findAll().stream()
                .map(this::mapToStockDTO)
                .collect(Collectors.toList());
    }

    public List<MovimientoDTO> obtenerMovimientos(String usuarioEmail) {
        auditar("GET_MOVIMIENTOS", null, usuarioEmail, "Consulta de todos los movimientos", null, null, null, null);
        return movimientoRepository.findAll().stream()
                .map(this::mapToMovimientoDTO)
                .collect(Collectors.toList());
    }

    public StockDTO mapToStockDTO(Stock stock) {
        return new StockDTO(stock.getProductoId(), stock.getCantidadDisponible());
    }

    public MovimientoDTO mapToMovimientoDTO(Movimiento movimiento) {
        return new MovimientoDTO(movimiento.getId(), movimiento.getProductoId(), movimiento.getProveedorId(),
                movimiento.getUsuarioEmail(), movimiento.getCantidad(), movimiento.getTipo(), movimiento.getFecha(),
                movimiento.getStockResultante());
    }

    public Movimiento mapToMovimiento(MovimientoRequestDTO movimientoRequest) {
        Movimiento movimiento = new Movimiento();
        movimiento.setProductoId(movimientoRequest.getProductoId());
        movimiento.setProveedorId(movimientoRequest.getProveedorId());
        movimiento.setUsuarioEmail(movimientoRequest.getUsuarioEmail());
        movimiento.setCantidad(movimientoRequest.getCantidad());
        movimiento.setTipo(movimientoRequest.getTipo());
        movimiento.setFecha(movimientoRequest.getFecha());
        return movimiento;
    }

    public Stock mapToStock(StockDTO stockDTO) {
        Stock stock = new Stock();
        stock.setProductoId(stockDTO.getProductoId());
        stock.setCantidadDisponible(stockDTO.getCantidadDisponible());
        return stock;
    }

}
