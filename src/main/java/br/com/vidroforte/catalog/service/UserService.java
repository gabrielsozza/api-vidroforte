package br.com.vidroforte.catalog.service;

import br.com.vidroforte.catalog.model.User;
import br.com.vidroforte.catalog.model.StatusAprovacao;
import br.com.vidroforte.catalog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmailService emailService;
    
    public User cadastrarUsuario(User user) {
        // Verificar se email já existe
        Optional<User> existente = userRepository.findByEmail(user.getEmail());
        if (existente.isPresent()) {
            throw new RuntimeException("E-mail já cadastrado");
        }
        
        user.setStatus(StatusAprovacao.PENDING);
        user.setDataCadastro(LocalDateTime.now());
        User userSalvo = userRepository.save(user);
        System.out.println("✅ Usuário cadastrado: " + userSalvo.getEmail() + " | ID: " + userSalvo.getId());
        return userSalvo;
    }
    
    public List<User> listarPendentes() {
        return userRepository.findByStatus(StatusAprovacao.PENDING);
    }
    
    public User aprovarUsuario(Long userId) {
        System.out.println("🔄 Iniciando aprovação do usuário ID: " + userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        System.out.println("📝 Status antes: " + user.getStatus());
        
        user.setStatus(StatusAprovacao.APPROVED);
        user.setDataAprovacao(LocalDateTime.now());
        
        User userSalvo = userRepository.save(user);
        userRepository.flush(); // Força o salvamento imediato
        
        System.out.println("✅ Usuário aprovado e salvo: " + userSalvo.getEmail() + " | Status: " + userSalvo.getStatus() + " | ID: " + userSalvo.getId());
        
        // Verificar se realmente foi salvo
        Optional<User> verificacao = userRepository.findById(userId);
        if (verificacao.isPresent()) {
            System.out.println("✅ Verificação: Status no banco = " + verificacao.get().getStatus());
        }
        
        // Enviar email de aprovação
        try {
            emailService.enviarEmailAprovacao(user.getEmail(), user.getNome());
            System.out.println("📧 E-mail de aprovação enviado para: " + user.getEmail());
        } catch (Exception e) {
            System.out.println("❌ Erro ao enviar email: " + e.getMessage());
        }
        
        return userSalvo;
    }
    
    public User rejeitarUsuario(Long userId) {
        System.out.println("🔄 Iniciando rejeição do usuário ID: " + userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        user.setStatus(StatusAprovacao.REJECTED);
        user.setDataAprovacao(LocalDateTime.now());
        
        User userSalvo = userRepository.save(user);
        userRepository.flush();
        
        System.out.println("❌ Usuário rejeitado: " + userSalvo.getEmail() + " | Status: " + userSalvo.getStatus());
        
        // Enviar email de rejeição
        try {
            emailService.enviarEmailRejeicao(user.getEmail(), user.getNome());
            System.out.println("📧 E-mail de rejeição enviado para: " + user.getEmail());
        } catch (Exception e) {
            System.out.println("❌ Erro ao enviar email: " + e.getMessage());
        }
        
        return userSalvo;
    }
    
    public Optional<User> buscarPorEmail(String email) {
        System.out.println("🔍 Buscando usuário por email: " + email);
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            System.out.println("✅ Usuário encontrado: " + user.get().getEmail() + " | Status: " + user.get().getStatus());
        } else {
            System.out.println("❌ Usuário não encontrado");
        }
        return user;
    }
}
