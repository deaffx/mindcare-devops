package br.com.fiap.mindcare.repository;

import br.com.fiap.mindcare.model.MensagemIA;
import br.com.fiap.mindcare.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensagemIARepository extends JpaRepository<MensagemIA, Long> {
    
    Page<MensagemIA> findByUsuarioOrderByCriadoEmDesc(Usuario usuario, Pageable pageable);
    
    List<MensagemIA> findByUsuarioAndLidaFalseOrderByCriadoEmDesc(Usuario usuario);
    
    List<MensagemIA> findByUsuarioAndTipoOrderByCriadoEmDesc(Usuario usuario, MensagemIA.TipoMensagem tipo);
    
    @Query("SELECT m FROM MensagemIA m WHERE m.usuario = :usuario AND m.lida = false " +
           "ORDER BY m.criadoEm DESC")
    List<MensagemIA> findMensagensNaoLidas(@Param("usuario") Usuario usuario, Pageable pageable);
    
    @Query("SELECT COUNT(m) FROM MensagemIA m WHERE m.usuario = :usuario AND m.lida = false")
    long countMensagensNaoLidas(@Param("usuario") Usuario usuario);
    
    @Query("SELECT m FROM MensagemIA m WHERE m.usuario = :usuario " +
           "ORDER BY m.criadoEm DESC")
    List<MensagemIA> findUltimasMensagens(@Param("usuario") Usuario usuario, Pageable pageable);
}
