package hp.server.app.models.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "password_reset")
public class PasswordReset {

    public static final int EXPIRATION = 60 * 24;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_persona")
    private Person person;
    @Column(name = "codigo", unique = true, nullable = false)
    private String code;
    @Column(name = "expiry_date")
    private Date expiryDate;
}
