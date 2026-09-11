package com.rotalog.repository;

import com.rotalog.domain.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * VeiculoRepository
 * 
 * FIXME: Queries nativas misturadas com derived queries
 * FIXME: Sem paginação
 * FIXME: Sem specification pattern
 */
@Repository
public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {

    Optional<Veiculo> findByPlaca(String placa);

    List<Veiculo> findByStatus(String status);

    // FIXME: Query nativa quando poderia ser derived query
    @Query(value = "SELECT * FROM veiculos WHERE quilometragem > :km", nativeQuery = true)
    List<Veiculo> findVeiculosComQuilometragemAcimaDe(@Param("km") Long quilometragem);

    // FIXME: Sem paginação - pode retornar milhares de registros
    List<Veiculo> findByModeloContainingIgnoreCase(String modelo);

    @Query("SELECT v FROM Veiculo v WHERE v.status = 'ATIVO' AND v.quilometragem > :limite")
    List<Veiculo> findVeiculosAtivosComQuilometragemAlta(@Param("limite") Long limite);

    // TODO: Adicionar query para veículos que precisam de manutenção
    // TODO: Adicionar query para veículos por ano de fabricação
    // TODO: Adicionar paginação
}
