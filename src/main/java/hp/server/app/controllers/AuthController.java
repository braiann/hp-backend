package hp.server.app.controllers;

import hp.server.app.models.dto.request.LoginRequestDTO;
import hp.server.app.models.dto.request.PasswordRequestDTO;
import hp.server.app.models.dto.request.RefreshTokenRequestDTO;
import hp.server.app.models.dto.response.MessageResponse;
import hp.server.app.models.dto.response.RefreshTokenResponseDTO;
import hp.server.app.models.entity.Person;
import hp.server.app.models.entity.RefreshToken;
import hp.server.app.security.jwt.JwtUtils;
import hp.server.app.services.AuthService;
import hp.server.app.services.RefreshTokenService;
import hp.server.app.utils.exceptions.RefreshTokenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private AuthService authService;
    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private JwtUtils jwtUtils;


    @PostMapping("/signin")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        logger.info("Enter to login()");
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), loginRequestDTO.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            return ResponseEntity.ok(authService.authenticateUser(authentication));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Bad Credentials: Username or Password are not valid!"));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerNewUser(@Valid @RequestBody Person person) {
        logger.info("Enter to registerNewUser()");
        try {
            return ResponseEntity.ok(authService.saveNewUser(person));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO) {
        logger.info("Enter to refreshToken()");
        logger.info("----- Token -> " + refreshTokenRequestDTO.getRefreshToken() + " -----");

        String token = refreshTokenRequestDTO.getRefreshToken();

        return refreshTokenService.getByToken(token)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getPerson)
                .map(person -> {
                    String accessToken = jwtUtils.generateTokenFromUsername(person.getUsername());
                    return ResponseEntity.ok(new RefreshTokenResponseDTO(accessToken, token, "Bearer"));
                }).orElseThrow(() -> new RefreshTokenException(token, "Refresh token is not exists in database!"));
    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN') or hasRole('ROLE_SUPERADMIN')")
    @PostMapping("/logout")
    public ResponseEntity logout() {
        logger.info("Enter to logout()");
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(null);
        return ResponseEntity.ok(new MessageResponse("Logout successfully!"));
    }

    @PostMapping("/password/forgotpassword")
    public ResponseEntity<?> forgotPassword(@RequestParam("email") String email) {
        logger.info("Enter to forgotPassword()");
        try {
            authService.requestPasswordChange(email);
            return ResponseEntity.accepted().body(new MessageResponse("Se ha enviado un codigo de restablecimiento de contraseña a tu email"));
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/password/resetpassword")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody PasswordRequestDTO passwordRequestDTO) {
        logger.info("Enter to resetPassword()");
        try {
            authService.resetPassword(passwordRequestDTO);
            return ResponseEntity.ok().body(new MessageResponse("Su contrasñea se ha modificado correctamente!"));
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().body(new MessageResponse(e.getMessage()));
        }
    }

}
