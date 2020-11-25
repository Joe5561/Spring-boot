package br.com.vendas.rest.controller;

import br.com.vendas.Exeption.ClienteNaoEncontradoExeption;
import br.com.vendas.Exeption.RegraNegocioExeption;
import br.com.vendas.domain.entity.Cliente;
import br.com.vendas.domain.repository.Clientes;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private Clientes clientes;

    public ClienteController(Clientes clientes) {
        this.clientes = clientes;
    }

    @GetMapping("{id}")
    public Cliente getClienteById(@PathVariable Integer id) {
        return clientes.findById(id)
                .orElseThrow(() -> new ClienteNaoEncontradoExeption("Cliente não encontrado: "+ id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cliente save(@RequestBody @Valid Cliente cliente) {
        return clientes.save(cliente);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        Optional<Cliente> buscaCliente = clientes.findById(id);
        clientes.findById(id)
                .map(cliente -> {
                    clientes.delete(cliente);
                    return cliente;
                })
                .orElseThrow(() -> new ClienteNaoEncontradoExeption("Cliente não encontrado: "+ id));
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable Integer id, @Valid @RequestBody Cliente cliente) {

        clientes.findById(id)
                .map(clienteExixtente -> {
                    cliente.setId(clienteExixtente.getId());
                    clientes.save(cliente);
                    return clienteExixtente;
                }).orElseThrow(() -> new RegraNegocioExeption("Não foi possivel atualizar"));
    }

    @GetMapping
    public List<Cliente> findAll(Cliente filtro) {
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        Example example = Example.of(filtro, exampleMatcher);
        return clientes.findAll(example);
    }
}
