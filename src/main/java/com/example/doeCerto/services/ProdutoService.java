package com.example.doeCerto.services;

import com.example.doeCerto.domain.Instituicao;
import com.example.doeCerto.domain.Produto;
import com.example.doeCerto.dtos.ProdutoRequestDTO;
import com.example.doeCerto.dtos.ProdutoResponseDTO;
import com.example.doeCerto.repositories.InstituicaoRepository;
import com.example.doeCerto.repositories.ProdutoRepository;
import com.example.doeCerto.infra.security.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final InstituicaoRepository instituicaoRepository;
    private final TokenService tokenService;
    private final HttpServletRequest request;

    public ProdutoService(ProdutoRepository produtoRepository, InstituicaoRepository instituicaoRepository, TokenService tokenService, HttpServletRequest request) {
        this.produtoRepository = produtoRepository;
        this.instituicaoRepository = instituicaoRepository;
        this.tokenService = tokenService;
        this.request = request;
    }

    public ProdutoResponseDTO salvarProduto(ProdutoRequestDTO produtoRequestDTO) {
        // Pegar o token da requisição
        String token = recuperarToken();
        String emailInstituicao = tokenService.validaToken(token);

        Instituicao instituicao = instituicaoRepository.findByEmail(emailInstituicao)
                .orElseThrow(() -> new RuntimeException("Instituição não encontrada pelo token"));

        Produto produto = new Produto();
        produto.setNome(produtoRequestDTO.nome());
        produto.setDescricao(produtoRequestDTO.descricao());
        produto.setStatus(produtoRequestDTO.status());
        produto.setInstituicao(instituicao);

        Produto produtoSalvo = produtoRepository.save(produto);
        return mapearParaResponseDTO(produtoSalvo);

    }

    public List<ProdutoResponseDTO> listarProdutos() {
        List<Produto> produtos = produtoRepository.findAll();
        return produtos.stream()
                .map(this::mapearParaResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ProdutoResponseDTO> listarProdutosPorNomeInstituicao(String nomeInstituicao) {
        List<Produto> produtos = produtoRepository.findByInstituicaoNomeInstituicaoIgnoreCaseContaining(nomeInstituicao);;
        return produtos.stream()
                .map(this::mapearParaResponseDTO)
                .collect(Collectors.toList());
    }

    private ProdutoResponseDTO mapearParaResponseDTO(Produto produto) {
        return new ProdutoResponseDTO(
                produto.getNome(),
                produto.getDescricao(),
                produto.getStatus(),
                produto.getInstituicao().getNomeInstituicao(),
                produto.getInstituicao().getEndereco(),
                produto.getInstituicao().getTelefone(),
                produto.getInstituicao().getImagemPerfil()
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
