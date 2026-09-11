package com.rotalog.repository;

import com.rotalog.domain.Motorista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * MotoristaRepository
 * 
 * FIXME: Sem paginação
 * FIXME: Sem filtros avançados
 */
@Repository
public interface MotoristaRepository extends JpaRepository<Motorista, Long> {

    Optional<Motorista> findByCnh(String cnh);

    List<Motorista> findByStatus(String status);

    List<Motorista> findByNomeContainingIgnoreCase(String nome);

    // FIXME: Query nativa desnecessária
    @Query(value = "SELECT * FROM motoristas WHERE vencimento_cnh < CURRENT_DATE", nativeQuery = true)
    List<Motorista> findMotoristasComCnhVencida();

    // TODO: Adicionar query para motoristas disponíveis
    // TODO: Adicionar paginação
}
