package com.eduardo.escuela.services.maestros;

import com.eduardo.escuela.repositories.GrupoRepository;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eduardo.escuela.dto.maestros.MaestroRequest;
import com.eduardo.escuela.dto.maestros.MaestroResponse;
import com.eduardo.escuela.entities.Maestro;
import com.eduardo.escuela.exceptions.ConflictoException;
import com.eduardo.escuela.exceptions.EntidadRelacionadaException;
import com.eduardo.escuela.mapper.MaestroMapper;
import com.eduardo.escuela.repositories.MaestroRepository;
import com.eduardo.escuela.utils.ServiceUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service 
@RequiredArgsConstructor 
@Transactional 
@Slf4j 
public class MaestroServiceImpl implements MaestroService{

    private final MaestroRepository maestroRepository;
    private final MaestroMapper maestroMapper;

    // private final CursoRepository cursoRepository;
    // private final CursoMapper cursoMapper;

    private final GrupoRepository grupoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MaestroResponse> listar() {
        log.info("Listando maestros");
        return maestroRepository.findAll().stream()
                .map(maestroMapper::entidadAResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MaestroResponse obtenerPorId(Long id) {
        return maestroMapper.entidadAResponse(obtenerMaestro(id));
    }

    @Override
    public MaestroResponse registrar(MaestroRequest request) {
        Maestro maestro = Maestro.crear(
                request.nombre(),
                request.apellidoPaterno(),
                request.apellidoMaterno(),
                request.email(),
                request.telefono()
        );

        validarDatosUnicos(maestro.getEmail(), maestro.getTelefono());

        maestroRepository.save(maestro);
        maestroRepository.flush();
        log.info("Maestro agregado con id: {}", maestro.getId());
        return maestroMapper.entidadAResponse(maestro);
    }

    @Override
    public MaestroResponse actualizar(MaestroRequest request, Long id) {
        Maestro maestro = obtenerMaestro(id);

        Maestro maestroConCambios = Maestro.crear(
                request.nombre(),
                request.apellidoPaterno(),
                request.apellidoMaterno(),
                request.email(),
                request.telefono()
        );

        validarCambiosUnicos(request.email(), request.telefono(), id);

        maestro.actualizar(
                maestroConCambios.getNombre(),
                maestroConCambios.getApellidoPaterno(),
                maestroConCambios.getApellidoMaterno(),
                maestroConCambios.getEmail(),
                maestroConCambios.getTelefono()
        );

        maestroRepository.save(maestro);
        maestroRepository.flush();

        log.info("Maestro {} actualizado con id: {}", maestro.getNombre(), maestro.getId());

        return maestroMapper.entidadAResponse(maestro);
    }

    @Override
    public void eliminar(Long id) {
        Maestro maestro = obtenerMaestro(id);

        // Validar si el maestro tiene grupos asignados antes de eliminarlo
        if(grupoRepository.existsByMaestroId(id))
            throw new EntidadRelacionadaException("No se puede eliminar si tiene grupos asignados");

        maestroRepository.delete(maestro);
        maestroRepository.flush();

        log.info("Maestro eliminado con id: {}", id);
    }

    // @Transactional(readOnly = true)
    // public List<DatosCurso> obtenerCursosDeUnMaestroConId(Long id){
    //     if(!maestroRepository.existsById(id))
    //         throw new ConflictoException("El maestro no existe con id: " + id);

    //     log.info("Listando cursos del maestro con id: {}", id);

    //     return cursoRepository.obtenerCursosPorIdMaestro(id).stream()
    //             .map(cursoMapper::entidadADatosCurso).toList();
    // }

    private Maestro obtenerMaestro(Long id){
        return ServiceUtils.obtenerEntidadOException(
                maestroRepository,
                id,
                Maestro.class
        );
    }

    private void validarDatosUnicos(String email, String telefono){
        if(maestroRepository.existsByEmail(email))
            throw new ConflictoException("Email ya existente");

        if(maestroRepository.existsByTelefono(telefono))
            throw new ConflictoException("Telefono ya existente");
    }

    private void validarCambiosUnicos(String email, String telefono, Long id){
        if(maestroRepository.existsByEmailAndIdNot(email, id))
            throw new ConflictoException("Otro maestro ya tiene este email");

        if(maestroRepository.existsByTelefonoAndIdNot(telefono, id))
            throw new ConflictoException("Otro maestro ya tiene este telefono");
    }
}