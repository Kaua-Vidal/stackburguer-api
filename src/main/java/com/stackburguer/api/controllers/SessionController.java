package com.stackburguer.api.controllers;

import com.stackburguer.api.DTO.login.LoginRequestDTO;
import com.stackburguer.api.DTO.login.LoginResponseDTO;
import com.stackburguer.api.models.User;
import com.stackburguer.api.service.TokenService;
import com.stackburguer.api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sessions")
public class SessionController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @PostMapping
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDTO data){

        //Buscando o usuário
        User user = userService.findByEmail(data.email());


        //Comparando a senha digitada com o hash do banco
        if(!passwordEncoder.matches(data.password(), user.getPassword())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("E-mail e/ou senha incorretos");
        }

        //Gerando o token para aquele usuario
        String token = tokenService.generateToken(user);

        //Retornando o que o Front-end espera
        return ResponseEntity.ok(new LoginResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.isAdmin(),
                token
        ));
    }
}
