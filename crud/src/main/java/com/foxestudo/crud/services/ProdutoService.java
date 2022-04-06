package com.foxestudo.crud.services;

import com.foxestudo.crud.data.vo.ProdutoVO;
import com.foxestudo.crud.entity.Produto;
import com.foxestudo.crud.exception.ResourceNotFoundException;
import com.foxestudo.crud.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProdutoService {

    private final ProdutoRepository repository;

    @Autowired
    public ProdutoService(ProdutoRepository repository) {
        this.repository = repository;
    }

    public ProdutoVO create(ProdutoVO produtoVO) {
        ProdutoVO produto = ProdutoVO.create(repository.save(Produto.create(produtoVO)));
        return produto;
    }

    public Page<ProdutoVO> findAll(Pageable pageable) {
        var page = repository.findAll(pageable);
        return page.map(this::convertToProdutoVO);
    }

    private ProdutoVO convertToProdutoVO(Produto produto){
        return ProdutoVO.create(produto);
    }

    public ProdutoVO findById(Long id){
        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Nenhum dado encontrado"));
        return ProdutoVO.create(entity);
    }

    public ProdutoVO update(ProdutoVO produtoVO){
        final Optional<Produto> optionalProduto = repository.findById(produtoVO.getId());
        if(!optionalProduto.isPresent()){
            new ResourceNotFoundException("Nenhum dado encontrado");
        }
        return ProdutoVO.create(repository.save(Produto.create(produtoVO)));
    }

    public void delete(Long id){
        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Nenhum dado encontrado"));
        repository.deleteById(id);
    }
}
