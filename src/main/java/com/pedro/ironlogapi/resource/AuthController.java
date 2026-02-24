package com.pedro.ironlogapi.resource;


import com.pedro.ironlogapi.DTO.AuthenticationDTO;
import com.pedro.ironlogapi.DTO.LoginResponseDTO;
import com.pedro.ironlogapi.DTO.RegisterDTO;
import com.pedro.ironlogapi.entities.User;
import com.pedro.ironlogapi.repositories.UserRepository;
import com.pedro.ironlogapi.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity login (@RequestBody AuthenticationDTO data) {
        var userNamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = this.authenticationManager.authenticate(userNamePassword);
        var token = tokenService.generateToken((User) auth.getPrincipal());
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity register (@RequestBody RegisterDTO data) {
        if (this.userRepository.findByEmail(data.email()) != null) {
            return ResponseEntity.badRequest().body("Email ja cadastrado!");
        }
        String ecryptedPassword = passwordEncoder.encode(data.password());
        User newUser = new User(null, data.name(), data.email(), ecryptedPassword);
        this.userRepository.save(newUser);
        return ResponseEntity.ok().build();
    }

}
