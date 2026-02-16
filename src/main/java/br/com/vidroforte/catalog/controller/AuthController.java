package br.com.vidroforte.catalog.controller;

import br.com.vidroforte.catalog.dto.LoginRequest;
import br.com.vidroforte.catalog.model.StatusAprovacao;
import br.com.vidroforte.catalog.model.User;
import br.com.vidroforte.catalog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/register")
    public ResponseEntity<?> cadastrar(@RequestBody User user) {
        try {
            User novoUser = userService.cadastrarUsuario(user);
            return ResponseEntity.ok(novoUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao cadastrar: " + e.getMessage());
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> userOpt = userService.buscarPorEmail(loginRequest.getEmail());
    
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não encontrado");
        }
    
        User user = userOpt.get();
    
        // Verificar se a senha está correta
        if (!user.getSenha().equals(loginRequest.getSenha())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Senha incorreta");
        }
    
        // Verificar se o usuário está aprovado
        if (user.getStatus() != StatusAprovacao.APPROVED) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Seu cadastro ainda não foi aprovado pelo administrador.");
        }
    
        return ResponseEntity.ok(user);
    }

}
