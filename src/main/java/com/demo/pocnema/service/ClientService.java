package com.demo.pocnema.service;

import com.demo.pocnema.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientService extends JpaRepository<Client, Long> {

}
