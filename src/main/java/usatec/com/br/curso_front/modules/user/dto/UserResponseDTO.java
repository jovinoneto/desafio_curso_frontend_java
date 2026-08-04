package usatec.com.br.curso_front.modules.user.dto;

import usatec.com.br.curso_front.modules.user.enums.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponseDTO(
		UUID id,
		String name,
		String email,
		String role,
		LocalDateTime createdAt,
		LocalDateTime updatedAt

) {
	// métodos para verificar qual é role do usuário
	public boolean isAdminOrCoordinator() {
		return "ADMINISTRATOR".equals(this.role) || "COORDINATOR".equals(this.role);
	}

	public boolean isStudentOrTeacher() {
		return "STUDENT".equals(this.role) || "TEACHER".equals(this.role);
	}

	public Integer getRoleId() {
		if(this.role == null) return null;
		try {
			return UserRole.valueOf(this.role.toUpperCase()).ordinal() + 1;
		} catch(IllegalArgumentException e) {
			return null;
		}
	}
}
