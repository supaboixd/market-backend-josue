package com.tecnm.merida.market_backend.domain.service;

import com.tecnm.merida.market_backend.persistence.crud.ClienteCrudRepository;
import com.tecnm.merida.market_backend.persistence.entity.Cliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class MarketUserDetailsService implements UserDetailsService {

    @Autowired
    private ClienteCrudRepository clienteCrudRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Cliente cliente = clienteCrudRepository.findByCorreoElectronico(username)
                .or(() -> clienteCrudRepository.findById(username))
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        if (cliente.getPassword() == null || cliente.getPassword().isBlank()) {
            throw new UsernameNotFoundException("El usuario no tiene contraseña configurada");
        }

        return new User(username, cliente.getPassword(), new ArrayList<>());
    }
}