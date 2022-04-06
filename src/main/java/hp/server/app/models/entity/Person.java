package hp.server.app.models.entity;

import annotations.Password;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "persona")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_persona")
    private Long id;
    @Column(name = "tipodoc")
    @NotEmpty
    @NotBlank
    private String documentType;
    @Column(name = "numerodoc")
    @NotNull
    private Long documentNumber;
    @Column(name = "nombre")
    @NotEmpty
    @NotBlank
    private String firstName;
    @Column(name = "apellido")
    @NotEmpty
    @NotBlank
    private String lastName;
    @Column(name = "telefono")
    @NotEmpty
    @NotBlank
    private String phoneNumber;
    @Column(name = "username")
    @NotNull
    @NotBlank
    private String username;
    @Column(name = "email")
    @NotEmpty
    @NotBlank
    @Email
    private String email;
    @Column(name = "password")
    @NotEmpty
    @Password
    private String password;
    @Column(name = "fechanacimiento")
    private LocalDateTime birthDate;
    @Column(name = "sexo")
    @NotEmpty
    private String gender;
    @Column(name = "nacionalidad")
    @NotEmpty
    @NotBlank
    private String nacionality;
    @Column(name = "estadocivil")
    @NotEmpty
    private String maritalStatus;
    @ManyToOne
    @JoinColumn(name = "id_tipo_persona")
    private Role role;
    @OneToOne
    @JoinColumn(name = "id_direccion")
    @NotNull
    private Address address;
}
