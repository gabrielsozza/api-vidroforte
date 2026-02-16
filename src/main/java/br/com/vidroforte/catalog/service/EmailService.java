package br.com.vidroforte.catalog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarEmailAprovacao(String destinatario, String nomeUsuario) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destinatario);
        message.setSubject("✅ Cadastro Aprovado - Vidroforte Catálogo");
        message.setText(
            "Olá " + nomeUsuario + ",\n\n" +
            "Seu cadastro no Catálogo Vidroforte foi APROVADO! 🎉\n\n" +
            "Você já pode acessar o catálogo completo de produtos e baixar as informações em PDF.\n\n" +
            "Acesse: http://localhost:5501/frontend/login.html\n\n" +
            "Atenciosamente,\n" +
            "Equipe Vidroforte"
        );
        
        mailSender.send(message);
    }

    public void enviarEmailRejeicao(String destinatario, String nomeUsuario) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destinatario);
        message.setSubject("❌ Cadastro Não Aprovado - Vidroforte Catálogo");
        message.setText(
            "Olá " + nomeUsuario + ",\n\n" +
            "Infelizmente seu cadastro no Catálogo Vidroforte não foi aprovado.\n\n" +
            "Se você acredita que houve algum erro, entre em contato conosco:\n" +
            "Email: contato@vidroforte.com.br\n" +
            "Telefone: (XX) XXXX-XXXX\n\n" +
            "Atenciosamente,\n" +
            "Equipe Vidroforte"
        );
        
        mailSender.send(message);
    }
}
