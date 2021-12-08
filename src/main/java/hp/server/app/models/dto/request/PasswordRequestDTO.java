package hp.server.app.models.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordRequestDTO {

    @NotBlank
    private String code;
    @NotBlank
    private String newPassword;
    @NotBlank
    private String confirmPassword;
}
