package hp.server.app.models.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "localidad")
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_localidad")
    private Long id;
    @Column(name = "codigo_postal")
    @NotNull
    @NotEmpty
    private String postalCode;
    @Column(name = "nombre")
    @NotEmpty
    private String name;
    @ManyToOne
    @JoinColumn(name = "id_fk_provincia")
    private Province province;

}
