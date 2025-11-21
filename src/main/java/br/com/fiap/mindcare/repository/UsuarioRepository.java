package br.com.fiap.mindcare.repository;

import br.com.fiap.mindcare.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByEmail(String email);
    
    Optional<Usuario> findByEmailAndAtivoTrue(String email);
    
    boolean existsByEmail(String email);
    
    Page<Usuario> findByAtivoTrue(Pageable pageable);
    
    @Query("SELECT u FROM Usuario u WHERE u.ativo = true AND " +
           "(LOWER(u.nome) LIKE LOWER(CONCAT('%', :busca, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :busca, '%')))")
    Page<Usuario> buscarUsuarios(@Param("busca") String busca, Pageable pageable);
}
