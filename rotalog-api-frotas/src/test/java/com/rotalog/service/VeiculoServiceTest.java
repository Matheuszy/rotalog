package com.rotalog.service;

import com.rotalog.domain.Veiculo;
import com.rotalog.exception.ResourceNotFoundException;
import com.rotalog.repository.VeiculoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VeiculoServiceTest {

	@Mock
	private VeiculoRepository veiculoRepository;

	@Mock
	private NotificacaoClient notificacaoClient;

	@InjectMocks
	private VeiculoService veiculoService;

	@Nested
	@DisplayName("buscarPorId")
	class BuscarPorId {

		@Test
		@DisplayName("whenVeiculoExists_thenReturnsVeiculo")
		void whenVeiculoExists_thenReturnsVeiculo() {
			Veiculo veiculo = new Veiculo();
			veiculo.setId(1L);
			veiculo.setPlaca("ABC1234");
			veiculo.setModelo("Fiat Ducato");
			when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));

			Veiculo resultado = veiculoService.buscarPorId(1L);

			assertThat(resultado.getId()).isEqualTo(1L);
			assertThat(resultado.getPlaca()).isEqualTo("ABC1234");
		}

		@Test
		@DisplayName("whenVeiculoNotFound_thenThrowsResourceNotFoundException")
		void whenVeiculoNotFound_thenThrowsResourceNotFoundException() {
			when(veiculoRepository.findById(99L)).thenReturn(Optional.empty());

			assertThatThrownBy(() -> veiculoService.buscarPorId(99L))
					.isInstanceOf(ResourceNotFoundException.class)
					.hasMessageContaining("99");
		}
	}
}
