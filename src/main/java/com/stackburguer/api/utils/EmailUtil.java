package com.stackburguer.api.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailUtil {

    @Autowired
    private JavaMailSender mailSender;


    public void enviarEmailStatusPedido(String destinataria, String mensagemTexto) {
        System.out.println("E-mail do destinatário recebido no Serviceee: ->" + destinataria + "<-");

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("kauakauavidalpc@gmail.com");
        message.setTo(destinataria);
        message.setSubject("Atualização do seu Pedido - Stack Burguer");
        message.setText(mensagemTexto);

        mailSender.send(message);
    }
    //
}
