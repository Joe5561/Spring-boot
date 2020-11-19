package br.com.vendas.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CredenciaisDTO {

    private String login;
    private String senha;
}
