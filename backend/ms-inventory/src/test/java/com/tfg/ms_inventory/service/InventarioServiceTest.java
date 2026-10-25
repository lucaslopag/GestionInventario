package com.tfg.ms_inventory.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.tfg.ms_inventory.dto.MovimientoDTO;
import com.tfg.ms_inventory.dto.MovimientoRequestDTO;
import com.tfg.ms_inventory.dto.StockDTO;
import com.tfg.ms_inventory.model.Movimiento;
import com.tfg.ms_inventory.model.Stock;
import com.tfg.ms_inventory.repository.MovimientoRepository;
import com.tfg.ms_inventory.repository.StockRepository;

@ExtendWith(MockitoExtension.class)
class InventarioServiceTest {

    @Mock
    private StockRepository stockRepository;
    @Mock
    private MovimientoRepository movimientoRepository;
    @Mock
    private RabbitTemplate rabbitTemplate;

    private InventarioService service;

    @BeforeEach
    void setUp() {
        service = new InventarioService(stockRepository, movimientoRepository, rabbitTemplate);
    }

    private Stock stockConCantidad(Long productoId, int cantidad) {
        Stock stock = new Stock();
        stock.setProductoId(productoId);
        stock.setCantidadDisponible(cantidad);
        return stock;
    }

    private MovimientoRequestDTO peticion(String tipo, Integer cantidad) {
        MovimientoRequestDTO request = new MovimientoRequestDTO();
        request.setProductoId(1L);
        request.setTipo(tipo);
        request.setCantidad(cantidad);
        request.setUsuarioEmail("usuario@ejemplo.com");
        return request;
    }

    private void guardarMovimientoDevuelveElMismo() {
        when(movimientoRepository.save(any(Movimiento.class))).thenAnswer(invocation -> {
            Movimiento movimiento = invocation.getArgument(0);
            movimiento.setId(10L);
            return movimiento;
        });
    }

    @Test
    void entradaSumaAlStockExistente() {
        when(stockRepository.findByProductoId(1L)).thenReturn(Optional.of(stockConCantidad(1L, 5)));
        guardarMovimientoDevuelveElMismo();

        MovimientoDTO resultado = service.procesarMovimiento(peticion("ENTRADA", 3));

        assertEquals(8, resultado.getStockResultante());
        assertEquals("ENTRADA", resultado.getTipo());
    }

    @Test
    void salidaRestaAlStockExistente() {
        when(stockRepository.findByProductoId(1L)).thenReturn(Optional.of(stockConCantidad(1L, 5)));
        guardarMovimientoDevuelveElMismo();

        MovimientoDTO resultado = service.procesarMovimiento(peticion("SALIDA", 2));

        assertEquals(3, resultado.getStockResultante());
    }

    @Test
    void salidaConStockInsuficienteDaConflictoYNoGuardaMovimiento() {
        when(stockRepository.findByProductoId(1L)).thenReturn(Optional.of(stockConCantidad(1L, 2)));

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.procesarMovimiento(peticion("SALIDA", 5)));

        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());
        verify(movimientoRepository, never()).save(any(Movimiento.class));
    }

    @Test
    void entradaDeProductoSinStockCreaElStockInicial() {
        when(stockRepository.findByProductoId(1L)).thenReturn(Optional.empty());
        guardarMovimientoDevuelveElMismo();

        MovimientoDTO resultado = service.procesarMovimiento(peticion("ENTRADA", 4));

        assertEquals(4, resultado.getStockResultante());
    }

    @Test
    void tipoDesconocidoDaBadRequest() {
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.procesarMovimiento(peticion("OTRO", 1)));

        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
    }

    @Test
    void cantidadCeroDaBadRequest() {
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.procesarMovimiento(peticion("ENTRADA", 0)));

        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
    }

    @Test
    void consultarStockDeProductoSinStockDevuelveCero() {
        when(stockRepository.findByProductoId(7L)).thenReturn(Optional.empty());

        StockDTO resultado = service.consultarStock(7L, "usuario@ejemplo.com");

        assertEquals(0, resultado.getCantidadDisponible());
    }
}
