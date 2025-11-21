package br.com.fiap.mindcare.repository;

import br.com.fiap.mindcare.model.Meta;
import br.com.fiap.mindcare.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MetaRepository extends JpaRepository<Meta, Long> {
    
    Page<Meta> findByUsuarioOrderByCriadoEmDesc(Usuario usuario, Pageable pageable);
    
    List<Meta> findByUsuarioAndStatusOrderByCriadoEmDesc(Usuario usuario, Meta.Status status);
    
    List<Meta> findByUsuarioAndCategoriaOrderByCriadoEmDesc(Usuario usuario, Meta.Categoria categoria);
    
    @Query("SELECT m FROM Meta m WHERE m.usuario = :usuario AND m.status = :status " +
           "ORDER BY m.criadoEm DESC")
    Page<Meta> buscarPorStatus(@Param("usuario") Usuario usuario, 
                                @Param("status") Meta.Status status, 
                                Pageable pageable);
    
    @Query("SELECT m FROM Meta m WHERE m.usuario = :usuario " +
           "AND m.status = 'ATIVA' " +
           "AND (m.dataFim IS NULL OR m.dataFim >= :hoje) " +
           "ORDER BY m.dataFim ASC NULLS LAST")
    List<Meta> findMetasAtivas(@Param("usuario") Usuario usuario, @Param("hoje") LocalDate hoje);
    
    @Query("SELECT COUNT(m) FROM Meta m WHERE m.usuario = :usuario AND m.status = 'CONCLUIDA'")
    long countMetasConcluidas(@Param("usuario") Usuario usuario);
    
    @Query("SELECT m FROM Meta m WHERE m.usuario = :usuario " +
           "AND m.status = 'ATIVA' AND m.dataFim < :hoje")
    List<Meta> findMetasVencidas(@Param("usuario") Usuario usuario, @Param("hoje") LocalDate hoje);
}
