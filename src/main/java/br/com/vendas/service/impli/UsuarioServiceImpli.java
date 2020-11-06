package br.com.vendas.service.impli;

import br.com.vendas.domain.entity.Usuario;
import br.com.vendas.domain.repository.Usuarios;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioServiceImpli implements UserDetailsService {

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private Usuarios usuariosRepository;

    @Transactional
    public Usuario salvar(Usuario usuario){
        return usuariosRepository.save(usuario);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
      Usuario usuario = usuariosRepository.findByLogin(username).orElseThrow(
               () -> new UsernameNotFoundException("Usuario não encontrado"));

      String [] roles = usuario.isAdmin() ? new String[]{"ADMIN", "USER"} : new String[]{"USER"};

      return User
              .builder()
              .username(usuario.getLogin())
              .password(usuario.getSenha())
              .roles(roles)
              .build();
    }
}
