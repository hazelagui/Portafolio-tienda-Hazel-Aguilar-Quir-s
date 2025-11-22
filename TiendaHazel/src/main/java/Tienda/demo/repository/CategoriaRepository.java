package Tienda.demo.repository;

// es un conjunto de las listas, agarra datos de la bd gracias a domain y lo mete a una lista, servicios los usa para hacer la logica


import Tienda.demo.domain.Categoria;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria,Integer>{
    public List<Categoria> findByActivoTrue();

}