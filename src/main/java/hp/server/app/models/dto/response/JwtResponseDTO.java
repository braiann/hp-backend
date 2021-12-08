package hp.server.app.models.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtResponseDTO {

    private Long id;
    private String username;
    private String email;
    private List<String> roles;
    private String accessToken;
    private String type = "Bearer";
}
