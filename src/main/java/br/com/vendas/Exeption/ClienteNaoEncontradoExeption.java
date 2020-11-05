package br.com.vendas.Exeption;

public class ClienteNaoEncontradoExeption extends RuntimeException {
    public ClienteNaoEncontradoExeption(String message) {
        super(message);
    }
}
