package com.tfg.ms_inventory.service;

import org.springframework.stereotype.Service;
import com.tfg.ms_inventory.dto.StockDTO;
import com.tfg.ms_inventory.dto.MovimientoDTO;
import org.springframework.transaction.annotation.Transactional;

import com.tfg.ms_inventory.repository.StockRepository;
import com.tfg.ms_inventory.repository.MovimientoRepository;
import com.tfg.ms_inventory.model.Stock;
import com.tfg.ms_inventory.model.Movimiento;
import com.tfg.ms_inventory.dto.MovimientoRequestDTO;

import org.springframework.beans.factory.annotation.Autowired;

@Service
public class InventarioService {
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private MovimientoRepository movimientoRepository;
    @Autowired
    private org.springframework.amqp.rabbit.core.RabbitTemplate rabbitTemplate;

    private void auditar(String accion, Long entidadId, String usuarioEmail, String detalles, Long productoId, Long proveedorId, Integer cantidad, Integer stockResultante) {
        com.tfg.ms_inventory.dto.AuditoriaEventoDTO evento = new com.tfg.ms_inventory.dto.AuditoriaEventoDTO(
                "INVENTORY", accion, entidadId, usuarioEmail, java.time.LocalDateTime.now(), detalles,
                productoId, proveedorId, cantidad, stockResultante);
        rabbitTemplate.convertAndSend(
                com.tfg.ms_inventory.config.RabbitMQConfig.EXCHANGE_AUDITORIA,
                com.tfg.ms_inventory.config.RabbitMQConfig.ROUTING_KEY_AUDITORIA,
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

        Stock stock;
        if (stockRepository.findByProductoId(movimientoRequest.getProductoId()).isEmpty()) {
            stock = crearStockInicial(movimientoRequest.getProductoId());
        } else {
            stock = stockRepository.findByProductoId(movimientoRequest.getProductoId()).get();
        }

        if (movimientoRequest.getTipo().equals("ENTRADA")) {
            stock.setCantidadDisponible(
                    stock.getCantidadDisponible()
                            + movimientoRequest.getCantidad());

        } else if (movimientoRequest.getTipo().equals("SALIDA")) {
            if (stock.getCantidadDisponible() < movimientoRequest.getCantidad()) {
                throw new IllegalArgumentException("No hay stock suficiente para realizar el movimiento");
            } else {
                stock.setCantidadDisponible(
                        stock.getCantidadDisponible()
                                - movimientoRequest.getCantidad());
            }
        }

        stockRepository.save(stock);
        Movimiento movimientoGenerado = crearMovimiento(movimientoRequest, stock.getCantidadDisponible());
        auditar("POST_MOVIMIENTO", movimientoGenerado.getId(), movimientoRequest.getUsuarioEmail(),
                "Movimiento de stock: " + movimientoRequest.getTipo(),
                movimientoRequest.getProductoId(), movimientoRequest.getProveedorId(),
                movimientoRequest.getCantidad(), stock.getCantidadDisponible());
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
        movimiento.setFecha(java.time.LocalDateTime.now());
        return movimientoRepository.save(movimiento);
    }

    public StockDTO consultarStock(Long productoId, String usuarioEmail) {
        auditar("GET_STOCK", productoId, usuarioEmail, "Consulta de stock", productoId, null, null, null);
        Stock stock = stockRepository.findByProductoId(productoId).orElse(null);
        if (stock == null) {
            return null;
        }
        return mapToStockDTO(stock);
    }

    public java.util.List<MovimientoDTO> obtenerMovimientos(String usuarioEmail) {
        auditar("GET_MOVIMIENTOS", null, usuarioEmail, "Consulta de todos los movimientos", null, null, null, null);
        return movimientoRepository.findAll().stream()
                .map(this::mapToMovimientoDTO)
                .collect(java.util.stream.Collectors.toList());
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
