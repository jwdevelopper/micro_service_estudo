package com.ms.auth.controller;

import com.ms.auth.jwt.JwtTokenProvider;
import com.ms.auth.repository.UserRepository;
import com.ms.auth.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    @GetMapping("/teste")
    @PreAuthorize("hasAuthority('ROLE_PESQUISAR_PAGAMENTO')")
    public String teste(){
        return "testando";
    }

    @PostMapping(produces = {"application/json", "application/xml", "application/x-yaml"},
            consumes = {"application/json", "application/xml", "application/x-yaml"})
    public ResponseEntity<?> login(@RequestBody UserVO userVO){
        try {
            var username = userVO.getUserName();
            var password = userVO.getPassword();

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            var user = userRepository.findByUserName(username);
            var token = "";
            if(user != null){
                token = jwtTokenProvider.createToken(username,user.getRoles());
            } else {
                throw new UsernameNotFoundException("Usuario não encontrado!");
            }
            Map<Object,Object> model = new HashMap<>();
            model.put("username",username);
            model.put("token", token);
            return ResponseEntity.ok(model);
        } catch (AuthenticationException e){
            throw new BadCredentialsException("Invalid username/password");
        }
    }
}
