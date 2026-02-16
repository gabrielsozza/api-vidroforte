package br.com.vidroforte.catalog.controller;

import br.com.vidroforte.catalog.model.StatusAprovacao;
import br.com.vidroforte.catalog.model.User;
import br.com.vidroforte.catalog.repository.UserRepository;
import br.com.vidroforte.catalog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/pendentes")
    public ResponseEntity<List<User>> listarPendentes() {
        List<User> pendentes = userService.listarPendentes();
        return ResponseEntity.ok(pendentes);
    }

    @GetMapping("/aprovados")
    public ResponseEntity<List<User>> listarAprovados() {
        List<User> aprovados = userRepository.findByStatus(StatusAprovacao.APPROVED);
        return ResponseEntity.ok(aprovados);
    }

    @GetMapping("/rejeitados")
    public ResponseEntity<List<User>> listarRejeitados() {
        List<User> rejeitados = userRepository.findByStatus(StatusAprovacao.REJECTED);
        return ResponseEntity.ok(rejeitados);
    }

    @PutMapping("/aprovar/{id}")
    public ResponseEntity<?> aprovarUsuario(@PathVariable Long id) {
        try {
            User user = userService.aprovarUsuario(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao aprovar usuário: " + e.getMessage());
        }
    }

    @PutMapping("/rejeitar/{id}")
    public ResponseEntity<?> rejeitarUsuario(@PathVariable Long id) {
        try {
            User user = userService.rejeitarUsuario(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao rejeitar usuário: " + e.getMessage());
        }
    }
}
