package fontys.sem3.likeme.business.interfaces.user.client;

import java.util.List;

import fontys.sem3.likeme.domain.user.client.Client;

public interface ClientService {
    Client createClient(Client client);
    Client updateClient(Client client);
    void deleteClient(Long id);
    Client getClient(Long id);
    Client getClientByUsername(String username);
    Client getClientByEmail(String email);
    List<Client> getAllClients();
} 