package com.demo.pocnema.controller;

import com.demo.pocnema.model.Client;
import com.demo.pocnema.service.ClientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class ClientControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;
    @LocalServerPort
    private int port;

    private static final ObjectMapper om = new ObjectMapper();
    @Mock
    private ClientService clientService;

    @InjectMocks
    private ClientController clientController;
    private MockMvc mockMvc;
    private List<Client> clients = new ArrayList<>();

    private String getRootUrl() {
        return "http://localhost:" + 8080;
    }
    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(clientController).build();
        restTemplate = new TestRestTemplate();
    }

    private List<Client> getEntityListStubData(){
        List<Client> list = new ArrayList<>();
        list.add((Client) getEntityListStubData());
        return list;
    }

    private Client getEntityStubData(){
        Client entity = new Client();
        entity.setId(1L);
        entity.setFirstname("Leon");
        entity.setLastname("Taylor");
        return entity;
    }

    @Test
    public void getAllClients() throws Exception {

        // given
        Client client = new Client("Erik", "Taylor");
        clients.add(client);
        Client client2 = new Client("Joost", "Taylor");
        clients.add(client2);
        Client client3 = new Client("Jordy", "Taylor");
        clients.add(client3);

        // mock the clientService.findAll() method return value
        when(clientService.findAll()).thenReturn(clients);

        // when
        // Perform the behavior being tested
        String uri = "/api/clients";
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON)).andReturn();

        // extract the response status and body
        String content = result.getResponse().getContentAsString();
        int status = result.getResponse().getStatus();

        // then
        //verify the clientService.fidAll method was invoked once
        verify(clientService, times(1)).findAll();
        //perfom standard JUnit assertions on the response
        Assert.assertEquals("failure-expected HTTP status 200", 200, status);
        Assert.assertTrue("failure - expected HTTP response body to have a value", content.trim().length() > 0);
}


    @Test
    public void createClient() throws Exception {

        //given
        Client client = new Client("Noah", "Lopes");
        when(clientService.save(any(Client.class))).thenReturn(client);

        //then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/client")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"firstname\":\"Noah\",\"lastname\":\"Lopes\" }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname").value("Noah"))
                .andExpect(jsonPath("$.lastname").value("Lopes"));


        ArgumentCaptor<Client> otherClient = ArgumentCaptor.forClass(Client.class);
        verify(clientService).save(otherClient.capture());

        assertThat(otherClient.getValue().getFirstname()).isEqualTo("Noah");
        assertThat(otherClient.getValue().getLastname()).isEqualTo("Lopes");

    }

    @Test
    public void getClientById() throws Exception {
        // given
        Long id = 1L;
        Client client = new Client("Erik", "Taylor");
        client.setId(id);
        //when(clientService.findById(id)).thenReturn(Optional.of(client));
        Mockito.doReturn(Optional.of(client))
               .when(clientService)
               .findById(id);
        //when
        Client response = clientController.getClientById(id);

        //then
        mockMvc.perform((MockMvcRequestBuilders.get("/api/clients/1")))
        .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
        .andExpect(MockMvcResultMatchers.content().string(containsString(client.getFirstname())));

        assertThat(response.getId()).isEqualTo(client.getId());
        assertThat(response.getLastname()).isEqualTo(client.getLastname());
 }

    @Test
    public void updateClient() throws Exception {

        Long id = 1L;
        Client client = new Client("Noah", "Lopes");
        client.setId(id);

        given(clientService.findById(id)).willReturn(Optional.of(client));
        //given(clientService.save(any(Client.class))).willAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

       when(clientService.save(any(Client.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
       Client response = clientController.getClientById(id);

        //then
        mockMvc.perform((MockMvcRequestBuilders.put("/api/clients/{id}", client.getId()))
                .content(om.writeValueAsString(client))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstname").value(client.getFirstname()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastname").value(client.getLastname()));


                assertThat(response.getFirstname()).isEqualTo("Noah");
                assertThat(response.getLastname()).isEqualTo("Lopes");
    }

    @Test
   public void deleteClient() {
    }
}
