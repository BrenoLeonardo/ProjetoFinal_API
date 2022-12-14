package org.serratec.ecommerce.api.controller;

import java.io.IOException;
import java.util.List;

import javax.transaction.Transactional;

import org.serratec.ecommerce.api.domain.Foto;
import org.serratec.ecommerce.api.domain.dto.ProdutoDTO;
import org.serratec.ecommerce.api.domain.dto.ProdutoInserirDTO;
import org.serratec.ecommerce.api.exception.ProdutoNotFoundException;
import org.serratec.ecommerce.api.service.FotoService;
import org.serratec.ecommerce.api.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {
		
	@Autowired
	private ProdutoService produtoService;

	@Autowired
	private FotoService fotoService;
	
	
	@GetMapping
	@Cacheable(value = "listaDeProdutos")
	@ApiOperation(value = "Listagem de todos os produtos cadastrados")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Retorna a lista de produtos"),
			@ApiResponse(code = 401, message = "Erro de autentica????o"),
			@ApiResponse(code = 403, message = "N??o h?? permiss??o para acessar o recurso"),
			@ApiResponse(code = 404, message = "Recurso n??o encontrado"),
			@ApiResponse(code = 505, message = "Exce????o interna da aplica????o"), })
	public List<ProdutoDTO> getProdutos() {
		List<ProdutoDTO> produtos = produtoService.findAll();
		return produtos;
	}

	@GetMapping("/{id}")
	@Cacheable(value = "produtosPorId")
	@ApiOperation(value = "Busca de produto por ID")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Retorna produto espec??fico"),
			@ApiResponse(code = 401, message = "Erro de autentica????o"),
			@ApiResponse(code = 403, message = "N??o h?? permiss??o para acessar o recurso"),
			@ApiResponse(code = 404, message = "Recurso n??o encontrado"),
			@ApiResponse(code = 505, message = "Exce????o interna da aplica????o"), })
	public ResponseEntity<ProdutoDTO> buscarPorId(@PathVariable Long id) 
	throws ProdutoNotFoundException{
		ProdutoDTO produto = produtoService.findById(id);
		return ResponseEntity.ok(produto);
	}
	
	@GetMapping("/url")
	@Cacheable(value = "produtosProdutosComUrl")
	@ApiOperation(value = "Listagem de todos os produtos com url cadastrados")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Retorna a lista de produtos com url"),
			@ApiResponse(code = 401, message = "Erro de autentica????o"),
			@ApiResponse(code = 403, message = "N??o h?? permiss??o para acessar o recurso"),
			@ApiResponse(code = 404, message = "Recurso n??o encontrado"),
			@ApiResponse(code = 505, message = "Exce????o interna da aplica????o"), })
	public ResponseEntity<List<ProdutoDTO>> listar() {
		List<ProdutoDTO> produtos = produtoService.findAll();
		return ResponseEntity.ok(produtos);
	}
	
	@PutMapping("/{id}")
	@Transactional
	@CacheEvict(value = "listaDeProdutos", allEntries = true)
	@ApiOperation(value = "Update produto por ID")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Retorna altera????o realizada"),
			@ApiResponse(code = 401, message = "Erro de autentica????o"),
			@ApiResponse(code = 403, message = "N??o h?? permiss??o para acessar o recurso"),
			@ApiResponse(code = 404, message = "Recurso n??o encontrado"),
			@ApiResponse(code = 505, message = "Exce????o interna da aplica????o"), })
	public ResponseEntity<ProdutoDTO> atualizar(@PathVariable Long id, @RequestBody ProdutoInserirDTO form) 
	throws ProdutoNotFoundException{
		return ResponseEntity.ok(produtoService.update(id, form));
	}
	
	@PostMapping("/noimg")
	@CacheEvict(value = "listaDeProdutos", allEntries = true)
	@ResponseStatus(code = HttpStatus.CREATED)
	@ApiOperation(value="Inser????o de produto(s) sem imagem  TESTAR")
    @ApiResponses(value= {
    		@ApiResponse(code = 200, message = "Inclui produto(s)"),
			@ApiResponse(code = 201, message = "Produto(s) criada com sucesso"),
			@ApiResponse(code = 401, message = "Erro de autentica????o"),
			@ApiResponse(code = 403, message = "N??o h?? permiss??o para acessar o recurso"),
			@ApiResponse(code = 404, message = "Recurso n??o encontrado"),
			@ApiResponse(code = 505, message = "Exce????o interna da aplica????o"), })
	public ProdutoDTO inserir(@RequestBody ProdutoInserirDTO novoProduto) {
		return produtoService.inserir(novoProduto);
	}
	
	@PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	@CacheEvict(value = "listaDeProdutos", allEntries = true)
	@ApiOperation(value="Inser????o de produto com imagem  TESTAR")
    @ApiResponses(value= {
    		@ApiResponse(code = 200, message = "Inclui produto(s)"),
			@ApiResponse(code = 201, message = "Produto(s) criada com sucesso"),
			@ApiResponse(code = 401, message = "Erro de autentica????o"),
			@ApiResponse(code = 403, message = "N??o h?? permiss??o para acessar o recurso"),
			@ApiResponse(code = 404, message = "Recurso n??o encontrado"),
			@ApiResponse(code = 505, message = "Exce????o interna da aplica????o"), })
	public ProdutoDTO inserir(
			@RequestPart ProdutoInserirDTO produto,
			@RequestPart MultipartFile file
			) throws IOException {
		return produtoService.inserir(produto, file);
	}
	
	@DeleteMapping("/{id}")
	@Transactional
	@CacheEvict(value = "listaDeProdutos", allEntries = true)
	@ApiOperation(value = "Remo????o de produto por ID")
	@ApiResponses(value = { @ApiResponse(code = 204, message = "Produto espec??fico removido"),
			@ApiResponse(code = 401, message = "Erro de autentica????o"),
			@ApiResponse(code = 403, message = "N??o h?? permiss??o para acessar o recurso"),
			@ApiResponse(code = 404, message = "Recurso n??o encontrado"),
			@ApiResponse(code = 505, message = "Exce????o interna da aplica????o"), })
	public ResponseEntity<Void> remover(@PathVariable Long id) 
	throws ProdutoNotFoundException{
		produtoService.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/{id}/foto")
	@Cacheable(value = "produtosProdutosComFoto")
	@ApiOperation(value = "Listagem de todos os produtos com foto cadastrados  TESTAR E RENOMEAR")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Retorna a lista de produtos com imagem"),
			@ApiResponse(code = 401, message = "Erro de autentica????o"),
			@ApiResponse(code = 403, message = "N??o h?? permiss??o para acessar o recurso"),
			@ApiResponse(code = 404, message = "Recurso n??o encontrado"),
			@ApiResponse(code = 505, message = "Exce????o interna da aplica????o"), })
	public ResponseEntity<byte[]> buscarFoto(@PathVariable Long id) {
		Foto foto = fotoService.buscarPorIdProduto(id).get();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", foto.getTipo());
		headers.add("Content-length", String.valueOf(foto.getDados().length));
		return new ResponseEntity<>(foto.getDados(), headers, HttpStatus.OK);
	}

}
