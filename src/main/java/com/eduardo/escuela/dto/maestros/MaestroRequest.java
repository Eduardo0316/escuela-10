package com.eduardo.escuela.dto.maestros;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos requeridos para un maestro")
public record MaestroRequest(
    @Schema(description = "Nombre del maestro", example = "Máximo")
    @NotBlank(message = "El nombre es requerido")
    @Size(min = 1, max = 50, message = "El nombre debe tener entre 1 y 50 caracteres")
    String nombre,
    
    @Schema(description = "Apellido paterno del maestro", example = "Juárez")
    @NotBlank(message = "El apellido paterno es requerido")
    @Size(min = 1, max = 50, message = "El apellido paterno debe tener entre 1 y 50 caracteres")
    String apellidoPaterno,

    @Schema(description = "Apellido materno del maestro", example = "Pérez")
    @NotBlank(message = "El apellido materno es requerido")
    @Size(min = 1, max = 50, message = "El apellido paterno debe tener entre 1 y 50 caracteres")
    String apellidoMaterno,

    @Schema(description = "Correo electronico del maestro", example = "test@test.com")
    @NotBlank(message = "El email es requerido")
    @Size(min = 8, max = 100, message = "El email debe tener entre 8 y 100 caracteres")
    @Email(message = "El email debe tener un formato valido (ejemplo@gmail.com)")
    String email,
    
    @Schema(description = "Número telefónico paterno del maestro", example = "2223334455")
    @NotBlank(message = "El número es requerido")
    @Pattern(regexp = "^[0-9]{10}", message = "El teléfono debe contener solo 10 digitos")
    String telefono
) {

}
