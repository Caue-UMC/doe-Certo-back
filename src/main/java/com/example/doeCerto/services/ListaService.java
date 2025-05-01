//package com.example.doeCerto.services;
//
//import com.example.doeCerto.domain.Instituicao;
//import com.example.doeCerto.domain.Lista;
//import com.example.doeCerto.dtos.ListaRequestDTO;
//import com.example.doeCerto.dtos.ListaResponseDTO;
//import com.example.doeCerto.repositories.InstituicaoRepository;
//import com.example.doeCerto.repositories.ListaRepository;
//import com.example.doeCerto.infra.security.TokenService;
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class ListaService {
//
//    private final ListaRepository listaRepository;
//    private final InstituicaoRepository instituicaoRepository;
//    private final TokenService tokenService;
//    private final HttpServletRequest request;
//
//    public ListaService(ListaRepository listaRepository, InstituicaoRepository instituicaoRepository, TokenService tokenService, HttpServletRequest request) {
//        this.listaRepository = listaRepository;
//        this.instituicaoRepository = instituicaoRepository;
//        this.tokenService = tokenService;
//        this.request = request;
//    }
//
//    public ListaResponseDTO salvarProduto(ListaRequestDTO listaRequestDTO) {
//        // Pegar o token da requisição
//        String token = recuperarToken();
//        String emailInstituicao = tokenService.validaToken(token);
//
//        Instituicao instituicao = instituicaoRepository.findByEmail(emailInstituicao)
//                .orElseThrow(() -> new RuntimeException("Instituição não encontrada pelo token"));
//
//        Lista lista = new Lista();
//        lista.setNome(listaRequestDTO.nome());
//        lista.setDescricao(listaRequestDTO.descricao());
//        lista.setStatus(listaRequestDTO.status());
//        lista.setInstituicao(instituicao);
//
//        Lista listaSalvo = listaRepository.save(lista);
//        return mapearParaResponseDTO(listaSalvo);
//
//    }
//
//    public List<ListaResponseDTO> listarProdutos() {
//        List<Lista> listas = listaRepository.findAll();
//        return listas.stream()
//                .map(this::mapearParaResponseDTO)
//                .collect(Collectors.toList());
//    }
//
//    public List<ListaResponseDTO> listarProdutosPorNomeInstituicao(String nomeInstituicao) {
//        List<Lista> listas = listaRepository.findByInstituicaoNomeInstituicaoIgnoreCaseContaining(nomeInstituicao);;
//        return listas.stream()
//                .map(this::mapearParaResponseDTO)
//                .collect(Collectors.toList());
//    }
//
//    private ListaResponseDTO mapearParaResponseDTO(Lista lista) {
//        return new ListaResponseDTO(
//                lista.getNome(),
//                lista.getDescricao(),
//                lista.getStatus(),
//                lista.getInstituicao().getNomeInstituicao(),
//                lista.getInstituicao().getEndereco(),
//                lista.getInstituicao().getTelefone(),
//                lista.getInstituicao().getImagemPerfil()
//        );
//    }
//
//    private String recuperarToken() {
//        String authorizationHeader = request.getHeader("Authorization");
//        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
//            throw new RuntimeException("Token inválido");
//        }
//        return authorizationHeader.replace("Bearer ", "");
//    }
//}
package com.example.doeCerto.services;

import com.example.doeCerto.domain.Instituicao;
import com.example.doeCerto.domain.Lista;
import com.example.doeCerto.dtos.ListaRequestDTO;
import com.example.doeCerto.dtos.ListaResponseDTO;
import com.example.doeCerto.repositories.InstituicaoRepository;
import com.example.doeCerto.repositories.ListaRepository;
import com.example.doeCerto.infra.security.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ListaService {

    private final ListaRepository listaRepository;
    private final InstituicaoRepository instituicaoRepository;
    private final TokenService tokenService;
    private final HttpServletRequest request;

    public ListaService(ListaRepository listaRepository, InstituicaoRepository instituicaoRepository, TokenService tokenService, HttpServletRequest request) {
        this.listaRepository = listaRepository;
        this.instituicaoRepository = instituicaoRepository;
        this.tokenService = tokenService;
        this.request = request;
    }

    public ListaResponseDTO salvarProduto(ListaRequestDTO listaRequestDTO) {
        // Pegar o token da requisição
        String token = recuperarToken();
        String emailInstituicao = tokenService.validaToken(token);

        Instituicao instituicao = instituicaoRepository.findByEmail(emailInstituicao)
                .orElseThrow(() -> new RuntimeException("Instituição não encontrada pelo token"));

        Lista lista = new Lista();
        lista.setNome(listaRequestDTO.nome());
        lista.setDescricao(listaRequestDTO.descricao());
        lista.setStatus(listaRequestDTO.status());
        lista.setInstituicao(instituicao);

        Lista listaSalvo = listaRepository.save(lista);
        return mapearParaResponseDTO(listaSalvo);
    }

    public List<ListaResponseDTO> listarProdutos() {
        List<Lista> listas = listaRepository.findAll();
        return listas.stream()
                .map(this::mapearParaResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ListaResponseDTO> listarProdutosPorNomeInstituicao(String nomeInstituicao) {
        List<Lista> listas = listaRepository.findByInstituicaoNomeInstituicaoIgnoreCaseContaining(nomeInstituicao);
        return listas.stream()
                .map(this::mapearParaResponseDTO)
                .collect(Collectors.toList());
    }

    // Lista de produtos por ID da instituição
    public List<ListaResponseDTO> listarProdutosPorInstituicao(Long idInstituicao) {
        List<Lista> listas = listaRepository.findByInstituicaoIdInstituicao(idInstituicao);
        return listas.stream()
                .map(this::mapearParaResponseDTO)
                .collect(Collectors.toList());
    }

    private ListaResponseDTO mapearParaResponseDTO(Lista lista) {
        return new ListaResponseDTO(
                lista.getNome(),
                lista.getDescricao(),
                lista.getStatus(),
                lista.getInstituicao().getNomeInstituicao(),
                lista.getInstituicao().getEndereco(),
                lista.getInstituicao().getTelefone(),
                lista.getInstituicao().getImagemPerfil()
        );
    }

    private String recuperarToken() {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token inválido");
        }
        return authorizationHeader.replace("Bearer ", "");
    }
}