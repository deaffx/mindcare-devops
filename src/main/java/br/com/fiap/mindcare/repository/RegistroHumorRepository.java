package br.com.fiap.mindcare.repository;

import br.com.fiap.mindcare.model.RegistroHumor;
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
public interface RegistroHumorRepository extends JpaRepository<RegistroHumor, Long> {
    
    Page<RegistroHumor> findByUsuarioOrderByDataDesc(Usuario usuario, Pageable pageable);
    
    List<RegistroHumor> findByUsuarioAndDataBetweenOrderByDataDesc(
            Usuario usuario, LocalDate dataInicio, LocalDate dataFim);
    
       @Query("SELECT r FROM RegistroHumor r WHERE r.usuario = :usuario AND r.data = :data ORDER BY r.criadoEm DESC")
       List<RegistroHumor> findByUsuarioAndDataOrderByCriadoEmDesc(@Param("usuario") Usuario usuario, @Param("data") LocalDate data);
    
    @Query("SELECT r FROM RegistroHumor r WHERE r.usuario = :usuario " +
           "ORDER BY r.data DESC")
    List<RegistroHumor> findUltimosRegistros(@Param("usuario") Usuario usuario, Pageable pageable);
    
    @Query("SELECT AVG(r.nivelHumor) FROM RegistroHumor r WHERE r.usuario = :usuario " +
           "AND r.data BETWEEN :dataInicio AND :dataFim")
    Double calcularMediaHumor(@Param("usuario") Usuario usuario, 
                              @Param("dataInicio") LocalDate dataInicio, 
                              @Param("dataFim") LocalDate dataFim);
    
    long countByUsuarioAndDataBetween(Usuario usuario, LocalDate dataInicio, LocalDate dataFim);
}
