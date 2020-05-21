package com.demo.pocnema.controller;


import com.demo.pocnema.exception.ResourceNotFoundException;
import com.demo.pocnema.model.Client;
import com.demo.pocnema.service.ClientService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


// @RestController annotation is a combination of Spring’s @Controller and @ResponseBody annotations./
// /("/api") declares that the url for all the apis in this controller will start with /api.
@RestController
@RequestMapping(path = "/api")
public class ClientController {

    Logger logger = LoggerFactory.getLogger(ClientController.class);


    private ClientService clientService;

   // @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    // Get All clients
    @GetMapping("/clients")
    public List<Client> getAllClients() {
        List<Client> clients = clientService.findAll();
        return clients;
    }

    // Create a new Client
    /*The @RequestBody annotation is used to bind the request body with a method parameter.
    The @Valid annotation makes sure that the request body is valid. as Client first and lastname were marked
    with @NotBlank annotation in the Client model.
    If the request body doesn’t have a firstname or  lastname, then spring will return a 400 BadRequest error to the client.*/
    @PostMapping("/client")
    public Client createClient(@Valid @RequestBody Client client) {

        return clientService.save(client);
    }

    // Get a Single Client
    // The @PathVariable annotation, as the name suggests, is used to bind a path variable with a method parameter.
    @GetMapping("/clients/{id}")
     public Client getClientById(@PathVariable(value = "id") Long id) {
          /*a ResourceNotFoundException whenever a Note with the given id is not found.
        This will cause Spring Boot to return a 404 Not Found error to the user
        (as it was added a @ResponseStatus(value = HttpStatus.NOT_FOUND) annotation to the ResourceNotFoundException class).*/
        return clientService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", id));
    }


    // Update a Client
    @PutMapping("/clients/{id}")
    public Client updateClient(@PathVariable(value = "id") Long id, @Valid @RequestBody Client clientDetail) {

        Client client = clientService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", id));

        client.setFirstname(clientDetail.getFirstname());
        client.setLastname(clientDetail.getLastname());

        return clientService.save(client);
    }

    // Delete a Client
    @DeleteMapping("/clients/{id}")
    public ResponseEntity<?> deleteClient(@PathVariable(value = "id") Long id) {
        Client client = clientService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", id));

        clientService.delete(client);
        return ResponseEntity.ok().build();

    }
}
