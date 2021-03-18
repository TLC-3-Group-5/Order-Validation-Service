package io.turntabl.producer.resources.service;

import io.turntabl.producer.resources.model.Client;
import io.turntabl.producer.resources.model.Response;
import io.turntabl.producer.resources.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ClientService {
    private final ClientRepository clientRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public List<Client> getClients(){ return clientRepository.findAll();}

    // SignUp
    public Response addNewClient(Client client) {
        String password = client.getPassword();
        Response response = new Response();
        if (password.isEmpty()) {
            throw new IllegalStateException("Invalid password.");
        }
        Optional<Client> clientByEmail = this.clientRepository.findClientByEmail(client.getEmail());
        String encodedPassword = encoder.encode(password);
        client.setPassword(encodedPassword);
        client.setBalance(200.00);

        if(clientByEmail.isPresent()){
            response.setCode(HttpStatus.BAD_REQUEST.value());
            response.setStatus("Email already taken");
            throw new IllegalStateException("Email already taken");
        }
        this.clientRepository.save(client);

        response.setCode(HttpStatus.CREATED.value());
        response.setStatus("Created Successfully");
        return response;
    }

    // Retrieve a Client by Id
    public Client getClient(Long clientId) {
        boolean exists = this.clientRepository.existsById(clientId);
        if(!exists){
            throw new IllegalStateException("client with id "+ clientId + " does not exist");
        }
        return this.clientRepository.findById(clientId).orElse(null);
    }

    // Login
    public Response loginClient(Client client){
        Response response = new Response();
        System.out.println(client.getPassword());
        Client foundClient = this.clientRepository.findClientByEmail(client.getEmail()).orElse(null);
        if(foundClient!=null){

            response.setName(foundClient.getName());
            String password = foundClient.getPassword();
            if(encoder.matches(client.getPassword(), password)){
                response.setCode(HttpStatus.OK.value());
                response.setData(foundClient);
                response.setStatus("Success");
            }else{
                response.setCode(HttpStatus.BAD_REQUEST.value());
                response.setStatus("Login Failed");
            }
        }else{
            response.setCode(HttpStatus.NOT_FOUND.value());
            response.setStatus("Client Not Found");
        }
        return response;
    }

    // Update Client details
    @Transactional
    public void updateClient(Long clientId, String name, String email) {
        Client foundClient = this.clientRepository.findById(clientId)
                .orElseThrow(()-> new IllegalStateException("client with id " + clientId + " does not exist"));

        if(name!=null &&
                name.length()>0 &&
                !Objects.equals(foundClient.getName(), name)){
            foundClient.setName(name);
        }

        if(email!=null &&
                email.length()>0 &&
                email.contains("@") &&
                !Objects.equals(foundClient.getEmail(), email)){
            Optional<Client> clientOptional = this.clientRepository.findClientByEmail(email);

            if(clientOptional.isPresent()){
                throw new IllegalStateException("email already taken");
            }
            foundClient.setEmail(email);
        }
    }

}

