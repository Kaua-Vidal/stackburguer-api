package com.stackburguer.api.utils;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WhatsappUtil {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.whatsapp.number}")
    private String fromNumber;

    public void enviarNotificacao(String para, String texto){
        try {
            Twilio.init(accountSid, authToken);

            String numeroLimpo = para.trim();

            if (!numeroLimpo.startsWith("+")){
                numeroLimpo = "+55"+numeroLimpo;
            }

            if (numeroLimpo.startsWith("+55") && numeroLimpo.length() == 14){
                numeroLimpo = numeroLimpo.substring(0, 5) + numeroLimpo.substring(6);
                System.out.println("🔧 Número ajustado (removido o 9º dígito): " + numeroLimpo);
            }

            if (!numeroLimpo.startsWith("whatsapp:")) {
                numeroLimpo = "whatsapp:" + numeroLimpo;
            }

            System.out.println("🚀 Tentando enviar para: " + numeroLimpo);
            Message message = Message.creator(
                    new PhoneNumber(numeroLimpo),
                    new PhoneNumber(fromNumber), texto
            ).create();

            System.out.println("Mensagem enviada com sucesso! SID: " + message.getSid());
        } catch (Exception e) {
            System.out.println("Erro ao enviar mensagem: " + e.getMessage());
        }
    }
}
