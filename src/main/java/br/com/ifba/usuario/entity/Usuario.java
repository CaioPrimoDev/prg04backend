package br.com.ifba.usuario.entity;

import br.com.ifba.infrastructure.entity.PersistenceEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.mapping.PersistentEntity;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "usuario")
public class Usuario extends PersistenceEntity {

    private String cpf;
    private String email;
    private String senha;
    private Boolean status = true;
}
