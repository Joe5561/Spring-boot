package br.com.vendas.service.impli;

import br.com.vendas.Exeption.PedidoNaoEncontradoException;
import br.com.vendas.Exeption.RegraNegocioExeption;
import br.com.vendas.domain.entity.Cliente;
import br.com.vendas.domain.entity.ItemPedido;
import br.com.vendas.domain.entity.Pedido;
import br.com.vendas.domain.entity.Produto;
import br.com.vendas.domain.enums.StatusPedido;
import br.com.vendas.domain.repository.Clientes;
import br.com.vendas.domain.repository.ItemsPedido;
import br.com.vendas.domain.repository.Pedidos;
import br.com.vendas.domain.repository.Produtos;
import br.com.vendas.dto.ItemPedidoDTO;
import br.com.vendas.dto.PedidoDTO;
import br.com.vendas.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class PedidoServiceImpli implements PedidoService {

    private final Pedidos pedidosRepository;
    private final Clientes clientesRespository;
    private final Produtos produtosRepository;
    private final ItemsPedido itemsPedidoRepository;

    @Override
    public Pedido salvar(PedidoDTO dto) {
        Integer idCliente = dto.getCliente();
        Cliente cliente = clientesRespository.findById(idCliente)
                .orElseThrow(() -> new RegraNegocioExeption("Código de cliente inválido: " + idCliente));

        Pedido pedido = new Pedido();
        pedido.setTotal(dto.getTotal());
        pedido.setDataPedido(LocalDate.now());
        pedido.setCliente(cliente);
        pedido.setStatus(StatusPedido.REALIZADO);

        List<ItemPedido> itemsPedido = converterItens(pedido, dto.getItems());

        pedidosRepository.save(pedido);
        itemsPedidoRepository.saveAll(itemsPedido);

        pedido.setItens(itemsPedido);
        return pedido;
    }

    @Override
    public Optional<Pedido> obterPedidoCompleto(Integer id) {

        return pedidosRepository.findByIdFetchItens(id);
    }

    @Override
    @Transactional
    public void atualizaStatus(Integer id, StatusPedido statusPedido) {
        pedidosRepository.findById(id)
                .map(pedido -> {
                    pedido.setStatus(statusPedido);
                    return pedidosRepository.save(pedido);
                }).orElseThrow(() -> new PedidoNaoEncontradoException("Pedido não encontrado: "+ id));
    }


    private List<ItemPedido> converterItens(Pedido pedido, List<ItemPedidoDTO> items) {
        if (items.isEmpty()) {
            throw new RegraNegocioExeption("Não é possivel realizar pedido sem itens");
        }

        return items
                .stream()
                .map(dto -> {
                    Integer idProduto = dto.getProduto();
                    Produto produto = produtosRepository.findById(idProduto)
                            .orElseThrow(() -> new RegraNegocioExeption("Código de produto inválido: " + idProduto));

                    ItemPedido itemPedido = new ItemPedido();

                    itemPedido.setQuantidade(dto.getQuantidade());
                    itemPedido.setPedido(pedido);
                    itemPedido.setProduto(produto);
                    return itemPedido;
                }).collect(Collectors.toList());
    }
}
