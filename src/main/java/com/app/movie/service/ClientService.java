/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.app.movie.service;


import com.app.movie.dto.ReportClientDto;
import com.app.movie.entities.Client;
import com.app.movie.repository.ClientRepository;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClientService {

    @Autowired
    ClientRepository repository;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Iterable<Client> get() {
        Iterable<Client> response = repository.getAll();
        return response;
    }

    public Optional<Client> getByCredential(String credential) {
        String pair = new String(Base64.decodeBase64(credential.substring(6)));
        String email = pair.split(":")[0];
        String pass = pair.split(":")[1];

        Optional<Client> client = repository.findByEmail(email);
        if(!matchPass(pass,client.get().getPassword())){
            return null;
        }
        return client;
    }

    public ReportClientDto getReport() {
        Optional<Client> client = repository.findById("6380442df71ad74770fc57e1");
        ReportClientDto reportClientDto = new ReportClientDto();
        reportClientDto.birthDate = client.get().getBirthDate();
        reportClientDto.email = client.get().getEmail();
        reportClientDto.id = client.get().getId();
        return reportClientDto;
    }

    public Client create(Client request) {
        request.setPassword(encrypt(request.getPassword()));
        return repository.save(request);

    }

    public Client update(Client client) {
        Client clientToUpdate = new Client();

        Optional<Client> currentClient = repository.findById(client.getId());
        if (!currentClient.isEmpty()) {
            clientToUpdate = client;
            clientToUpdate = repository.save(clientToUpdate);
        }
        return clientToUpdate;
    }

    public Boolean delete(String id) {
        repository.deleteById(id);
        Boolean deleted = true;
        return deleted;
    }

    private String encrypt(String pass){
        return this.passwordEncoder.encode(pass);
    }

    private Boolean matchPass(String pass,String dbPass){
        return this.passwordEncoder.matches(pass,dbPass);
    }
}
