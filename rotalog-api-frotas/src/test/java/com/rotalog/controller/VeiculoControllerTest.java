package com.rotalog.controller;

import com.rotalog.domain.Veiculo;
import com.rotalog.exception.ResourceNotFoundException;
import com.rotalog.service.VeiculoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VeiculoControllerTest {

	@Mock
	private VeiculoService veiculoService;

	@InjectMocks
	private VeiculoController veiculoController;

	@Nested
	@DisplayName("GET /veiculos/{id}")
	class BuscarPorId {

		@Test
		@DisplayName("whenVeiculoExists_thenReturns200WithVeiculo")
		void whenVeiculoExists_thenReturns200WithVeiculo() {
			Veiculo veiculo = new Veiculo();
			veiculo.setId(1L);
			veiculo.setPlaca("ABC1234");
			when(veiculoService.buscarPorId(1L)).thenReturn(veiculo);

			ResponseEntity<Veiculo> response = veiculoController.buscarPorId(1L);

			Veiculo body = response.getBody();

			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
			assertThat(body).isNotNull();
			assertThat(body.getId()).isEqualTo(1L);
			assertThat(body.getPlaca()).isEqualTo("ABC1234");
		}

		@Test
		@DisplayName("whenVeiculoNotFound_thenPropagatesResourceNotFoundException")
		void whenVeiculoNotFound_thenPropagatesResourceNotFoundException() {
			when(veiculoService.buscarPorId(99L)).thenThrow(new ResourceNotFoundException("Veículo não encontrado: 99"));

			assertThatThrownBy(() -> veiculoController.buscarPorId(99L))
					.isInstanceOf(ResourceNotFoundException.class)
					.hasMessageContaining("99");
		}
	}
}
