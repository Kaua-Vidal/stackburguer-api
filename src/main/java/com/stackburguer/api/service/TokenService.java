package com.stackburguer.api.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.stackburguer.api.exceptions.GeneratingTokenErrorException;
import com.stackburguer.api.models.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")  //Pegando do application.properties
    private String secret;

    public String generateToken(User user){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("stack-burguer-api")
                    .withSubject(user.getEmail())  //Identificador do dono do token
                    .withClaim("id", user.getId().toString())  //Dados extras
                    .withClaim("admin", user.isAdmin())
                    .withExpiresAt(getExpirationDate())
                    .sign(algorithm);
        } catch(JWTCreationException exception){
            throw new GeneratingTokenErrorException("Erro ao gerar token");
        }
    }

    public String validateToken(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("stack-burguer-api")
                    .build()
                    .verify(token) //Aqui ele verifica a assinatura e a validade
                    .getSubject();  //Se estiver tudo certo, ele retorna o email do usuário
        } catch(JWTVerificationException exception){
            return "";   //Se o token for falso ou expirado, retornamos cazio para o filtro barrar
        }
    }

    private Instant getExpirationDate(){
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
