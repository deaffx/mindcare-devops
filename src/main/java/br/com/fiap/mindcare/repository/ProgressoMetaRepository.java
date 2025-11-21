package br.com.fiap.mindcare.repository;

import br.com.fiap.mindcare.model.Meta;
import br.com.fiap.mindcare.model.ProgressoMeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProgressoMetaRepository extends JpaRepository<ProgressoMeta, Long> {
    
    List<ProgressoMeta> findByMetaOrderByDataDesc(Meta meta);
    
    Optional<ProgressoMeta> findByMetaAndData(Meta meta, LocalDate data);
    
    @Query("SELECT p FROM ProgressoMeta p WHERE p.meta = :meta AND p.concluido = true " +
           "ORDER BY p.data DESC")
    List<ProgressoMeta> findProgressosConcluidos(@Param("meta") Meta meta);
    
    @Query("SELECT COUNT(p) FROM ProgressoMeta p WHERE p.meta = :meta AND p.concluido = true")
    long countProgressosConcluidos(@Param("meta") Meta meta);
    
    @Query("SELECT p FROM ProgressoMeta p WHERE p.meta = :meta " +
           "AND p.data BETWEEN :dataInicio AND :dataFim ORDER BY p.data")
    List<ProgressoMeta> findProgressosPorPeriodo(@Param("meta") Meta meta,
                                                   @Param("dataInicio") LocalDate dataInicio,
                                                   @Param("dataFim") LocalDate dataFim);
}
