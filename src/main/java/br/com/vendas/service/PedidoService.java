package br.com.vendas.service;

import br.com.vendas.domain.entity.Pedido;
import br.com.vendas.domain.enums.StatusPedido;
import br.com.vendas.dto.PedidoDTO;

import java.util.Optional;

public interface PedidoService {

    Pedido salvar(PedidoDTO dto);

    Optional<Pedido> obterPedidoCompleto(Integer id);

    void atualizaStatus(Integer id, StatusPedido statusPedido);

}
