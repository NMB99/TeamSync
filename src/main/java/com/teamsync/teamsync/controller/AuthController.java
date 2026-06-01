package com.teamsync.teamsync.controller;

import com.teamsync.teamsync.dto.AuthRequest;
import com.teamsync.teamsync.dto.AuthResponse;
import com.teamsync.teamsync.entity.Team;
import com.teamsync.teamsync.entity.User;
import com.teamsync.teamsync.enums.Role;
import com.teamsync.teamsync.enums.TeamCategory;
import com.teamsync.teamsync.repository.TeamRepository;
import com.teamsync.teamsync.repository.UserRepository;
import com.teamsync.teamsync.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication", description = "Login and initial setup")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final PasswordEncoder passwordEncoder;

    @Operation(summary = "Login", description = "Authenticates a user and returns a JWT token")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getEmail(),
                            authRequest.getPassword()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);
            String role = userDetails.getAuthorities()
                    .stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse("ROLE_TEAM_MEMBER");

            return ResponseEntity.ok(new AuthResponse(token, role));
        }
        catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @Operation(summary = "Initial Setup", description = "Creates the first ADMIN user. Self-locks after first use.")
    @Transactional
    @PostMapping("/setup")
    public ResponseEntity<String> setup() {

        if (userRepository.count() > 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Setup already complete");
        }

        Team team = new Team();
        team.setName("Admin Team");
        team.setDescription("Default admin team");
        team.setCategory(TeamCategory.DEVELOPMENT);
        teamRepository.save(team);

        User admin = new User();
        admin.setFullName("Admin");
        admin.setEmail("admin@teamsync.com");
        admin.setPassword(passwordEncoder.encode("Admin@123"));
        admin.setRole(Role.ADMIN);
        admin.setTeam(team);
        userRepository.save(admin);

        return ResponseEntity.ok("Admin created successfully.");
    }
}
