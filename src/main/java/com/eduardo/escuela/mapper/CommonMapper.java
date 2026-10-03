package com.eduardo.escuela.mapper;

public interface CommonMapper<RQ, RS, E> {
    public E requestAEntidad(RQ request);
    
    public RS entidadAResponse(E entidad);
}
