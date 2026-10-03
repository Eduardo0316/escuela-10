package com.eduardo.escuela.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eduardo.escuela.entities.Maestro;

@Repository 
public interface MaestroRepository extends JpaRepository<Maestro, Long>{

}
