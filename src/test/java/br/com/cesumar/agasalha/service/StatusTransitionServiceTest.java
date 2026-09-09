package br.com.cesumar.agasalha.service;

import br.com.cesumar.agasalha.exception.TransicaoInvalidaException;
import br.com.cesumar.agasalha.model.AcaoStatus;
import br.com.cesumar.agasalha.model.StatusItem;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StatusTransitionServiceTest {

    private final StatusTransitionService service = new StatusTransitionService();

    @Test
    void aplicar_disponivelReservar_retornaReservado() {
        StatusItem resultado = service.aplicar(StatusItem.DISPONIVEL, AcaoStatus.RESERVAR);

        assertEquals(StatusItem.RESERVADO, resultado);
    }

    @Test
    void aplicar_reservadoEntregar_retornaEntregue() {
        StatusItem resultado = service.aplicar(StatusItem.RESERVADO, AcaoStatus.ENTREGAR);

        assertEquals(StatusItem.ENTREGUE, resultado);
    }

    @Test
    void aplicar_disponivelEntregar_lancaTransicaoInvalida() {
        assertThrows(TransicaoInvalidaException.class,
                () -> service.aplicar(StatusItem.DISPONIVEL, AcaoStatus.ENTREGAR));
    }

    @Test
    void aplicar_reservadoReservar_lancaTransicaoInvalida() {
        assertThrows(TransicaoInvalidaException.class,
                () -> service.aplicar(StatusItem.RESERVADO, AcaoStatus.RESERVAR));
    }

    @Test
    void aplicar_entregueReservar_lancaTransicaoInvalida() {
        assertThrows(TransicaoInvalidaException.class,
                () -> service.aplicar(StatusItem.ENTREGUE, AcaoStatus.RESERVAR));
    }

    @Test
    void aplicar_entregueEntregar_lancaTransicaoInvalida() {
        assertThrows(TransicaoInvalidaException.class,
                () -> service.aplicar(StatusItem.ENTREGUE, AcaoStatus.ENTREGAR));
    }

    @Test
    void aplicar_statusNuloReservar_lancaTransicaoInvalida() {
        assertThrows(TransicaoInvalidaException.class,
                () -> service.aplicar(null, AcaoStatus.RESERVAR));
    }

    @Test
    void aplicar_statusNuloEntregar_lancaTransicaoInvalida() {
        assertThrows(TransicaoInvalidaException.class,
                () -> service.aplicar(null, AcaoStatus.ENTREGAR));
    }
}
