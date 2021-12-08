package hp.server.app.controllers;

import hp.server.app.models.dto.request.LoginRequestDTO;
import hp.server.app.models.dto.response.JwtResponseDTO;
import hp.server.app.models.dto.response.MessageResponse;
import hp.server.app.models.entity.Person;
import hp.server.app.models.entity.Role;
import hp.server.app.models.repository.AddressRepository;
import hp.server.app.models.repository.PersonRepository;
import hp.server.app.models.repository.RoleRepository;
import hp.server.app.security.jwt.JwtUtils;
import hp.server.app.security.services.UserDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        logger.info("Enter to login()");
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), loginRequestDTO.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = jwtUtils.generateJwtToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream().map(role -> role.getAuthority()).collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponseDTO(
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles,
                accessToken,
                "Bearer"
        ));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerNewUser(@Valid @RequestBody Person person) {
        logger.info("Enter to registerNewUser()");

        Optional<Person> p = personRepository.findByUsername(person.getUsername());
        if (p.isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken"));
        }

        Person pe = personRepository.findByEmail(person.getEmail());
        if (pe != null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        Role role = person.getRole();

        Role rolePerson;
        if (role == null) {
            rolePerson = roleRepository.findByDescription(Role.ROLE_USER);
        } else {
            String descriptionRole = role.getDescription();

            switch (descriptionRole) {
                case "ROLE_ADMIN":
                    rolePerson = roleRepository.findByDescription(Role.ROLE_ADMIN);
                break;

                case "ROLE_SUPERADMIN":
                    rolePerson = roleRepository.findByDescription(Role.ROLE_SUPERADMIN);
                break;

                default:
                    rolePerson = roleRepository.findByDescription(Role.ROLE_USER);

            }
        }

        person.setPassword(encoder.encode(person.getPassword()));
        addressRepository.save(person.getAddress());
        person.setRole(rolePerson);
        personRepository.save(person);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

}
