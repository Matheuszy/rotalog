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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VeiculoServiceTest {

	@Mock
	private VeiculoRepository veiculoRepository;

	@Mock
	private NotificacaoClient notificacaoClient;

	@InjectMocks
	private VeiculoService veiculoService;

	// -------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------

	private Veiculo veiculoComId(Long id, String placa, String modelo, String status, Long km) {
		Veiculo v = new Veiculo();
		v.setId(id);
		v.setPlaca(placa);
		v.setModelo(modelo);
		v.setStatus(status);
		v.setQuilometragem(km);
		return v;
	}

	// -------------------------------------------------------------------
	// buscarPorId
	// -------------------------------------------------------------------

	@Nested
	@DisplayName("buscarPorId")
	class BuscarPorId {

		@Test
		@DisplayName("whenVeiculoExists_thenReturnsVeiculo")
		void whenVeiculoExists_thenReturnsVeiculo() {
			Veiculo veiculo = veiculoComId(1L, "ABC1234", "Fiat Ducato", "ATIVO", 0L);
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

	// -------------------------------------------------------------------
	// listarTodos
	// -------------------------------------------------------------------

	@Nested
	@DisplayName("listarTodos")
	class ListarTodos {

		@Test
		@DisplayName("whenCalled_thenReturnsDelegatedList")
		void whenCalled_thenReturnsDelegatedList() {
			List<Veiculo> lista = List.of(
					veiculoComId(1L, "ABC1234", "Sprinter", "ATIVO", 10000L),
					veiculoComId(2L, "XYZ5678", "Iveco Daily", "INATIVO", 80000L)
			);
			when(veiculoRepository.findAll()).thenReturn(lista);

			List<Veiculo> resultado = veiculoService.listarTodos();

			assertThat(resultado).hasSize(2);
			assertThat(resultado.get(0).getPlaca()).isEqualTo("ABC1234");
		}
	}

	// -------------------------------------------------------------------
	// calcularCustoManutencao
	// -------------------------------------------------------------------

	@Nested
	@DisplayName("calcularCustoManutencao")
	class CalcularCustoManutencao {

		@Test
		@DisplayName("whenZeroKm_thenReturnsCustoBase")
		void whenZeroKm_thenReturnsCustoBase() {
			// custo = 500 + (0 * 0.05) = 500.0
			Double custo = veiculoService.calcularCustoManutencao("Qualquer", 0L);

			assertThat(custo).isEqualTo(500.0);
		}

		@Test
		@DisplayName("whenHighKm_thenReturnsExpectedCost")
		void whenHighKm_thenReturnsExpectedCost() {
			// custo = 500 + (10000 * 0.05) = 1000.0
			Double custo = veiculoService.calcularCustoManutencao("Sprinter", 10_000L);

			assertThat(custo).isEqualTo(1000.0);
		}
	}

	// -------------------------------------------------------------------
	// precisaDeManutencao
	// -------------------------------------------------------------------

	@Nested
	@DisplayName("precisaDeManutencao")
	class PrecisaDeManutencao {

		@Test
		@DisplayName("whenKmBelowThreshold_thenReturnsFalse")
		void whenKmBelowThreshold_thenReturnsFalse() {
			Veiculo veiculo = veiculoComId(1L, "ABC1234", "Sprinter", "ATIVO", 30_000L);
			when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));

			Boolean resultado = veiculoService.precisaDeManutencao(1L);

			assertThat(resultado).isFalse();
		}

		@Test
		@DisplayName("whenKmAtThreshold_thenReturnsTrue")
		void whenKmAtThreshold_thenReturnsTrue() {
			Veiculo veiculo = veiculoComId(1L, "ABC1234", "Sprinter", "ATIVO", 50_000L);
			when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));

			Boolean resultado = veiculoService.precisaDeManutencao(1L);

			assertThat(resultado).isTrue();
		}
	}

	// -------------------------------------------------------------------
	// registrarVeiculo
	// -------------------------------------------------------------------

	@Nested
	@DisplayName("registrarVeiculo")
	class RegistrarVeiculo {

		@Test
		@DisplayName("whenValidData_thenSavesAndReturnsVeiculo")
		void whenValidData_thenSavesAndReturnsVeiculo() {
			when(veiculoRepository.findByPlaca("ABC1234")).thenReturn(Optional.empty());
			Veiculo salvo = veiculoComId(1L, "ABC1234", "Sprinter", "ATIVO", 0L);
			when(veiculoRepository.save(any(Veiculo.class))).thenReturn(salvo);

			Veiculo resultado = veiculoService.registrarVeiculo("ABC1234", "Sprinter", 2022);

			assertThat(resultado.getPlaca()).isEqualTo("ABC1234");
			assertThat(resultado.getStatus()).isEqualTo("ATIVO");
		}

		@Test
		@DisplayName("whenPlacaIsNull_thenThrowsException")
		void whenPlacaIsNull_thenThrowsException() {
			assertThatThrownBy(() -> veiculoService.registrarVeiculo(null, "Sprinter", 2022))
					.isInstanceOf(RuntimeException.class)
					.hasMessageContaining("Placa é obrigatória");
		}

		@Test
		@DisplayName("whenPlacaWrongLength_thenThrowsException")
		void whenPlacaWrongLength_thenThrowsException() {
			assertThatThrownBy(() -> veiculoService.registrarVeiculo("AB", "Sprinter", 2022))
					.isInstanceOf(RuntimeException.class)
					.hasMessageContaining("7 caracteres");
		}

		@Test
		@DisplayName("whenPlacaAlreadyExists_thenThrowsException")
		void whenPlacaAlreadyExists_thenThrowsException() {
			Veiculo existente = veiculoComId(1L, "ABC1234", "Sprinter", "ATIVO", 0L);
			when(veiculoRepository.findByPlaca("ABC1234")).thenReturn(Optional.of(existente));

			assertThatThrownBy(() -> veiculoService.registrarVeiculo("ABC1234", "Sprinter", 2022))
					.isInstanceOf(RuntimeException.class)
					.hasMessageContaining("já existe");
		}

		@Test
		@DisplayName("whenAnoInvalid_thenThrowsException")
		void whenAnoInvalid_thenThrowsException() {
			when(veiculoRepository.findByPlaca("ABC1234")).thenReturn(Optional.empty());

			assertThatThrownBy(() -> veiculoService.registrarVeiculo("ABC1234", "Sprinter", 1800))
					.isInstanceOf(RuntimeException.class)
					.hasMessageContaining("Ano de fabricação inválido");
		}
	}

	// -------------------------------------------------------------------
	// obterVeiculosPorStatus
	// -------------------------------------------------------------------

	@Nested
	@DisplayName("obterVeiculosPorStatus")
	class ObterVeiculosPorStatus {

		@Test
		@DisplayName("whenValidStatus_thenReturnsList")
		void whenValidStatus_thenReturnsList() {
			List<Veiculo> ativos = List.of(veiculoComId(1L, "ABC1234", "Sprinter", "ATIVO", 5000L));
			when(veiculoRepository.findByStatus("ATIVO")).thenReturn(ativos);

			List<Veiculo> resultado = veiculoService.obterVeiculosPorStatus("ATIVO");

			assertThat(resultado).hasSize(1);
		}

		@Test
		@DisplayName("whenInvalidStatus_thenThrowsException")
		void whenInvalidStatus_thenThrowsException() {
			assertThatThrownBy(() -> veiculoService.obterVeiculosPorStatus("PENDENTE"))
					.isInstanceOf(RuntimeException.class)
					.hasMessageContaining("Status inválido");
		}
	}
}
