package com.example.dscommerce02.services;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.dscommerce02.dto.ProductDTO;
import com.example.dscommerce02.entities.Product;
import com.example.dscommerce02.entities.exceptions.DatabaseException;
import com.example.dscommerce02.entities.exceptions.ResourceNotFoundException;
import com.example.dscommerce02.repositories.ProductRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProductServices {
	
	@Autowired
	private ProductRepository repository;
	
	@Transactional(readOnly = true)
	public ProductDTO findById(Long id) {		
		Product product = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado"));
		return new ProductDTO(product);		
	}
	
	@Transactional(readOnly = true)
	public Page<ProductDTO> findAll(String name, Pageable pageable) {		
		Page<Product> result = repository.searchByName(name, pageable);
		return result.map(x -> new ProductDTO(x));		
	}	
	
	@Transactional
	public ProductDTO insert(ProductDTO dto) {		
		Product	entity = new Product();
		copyDtoToEntity(dto, entity);		
		entity = repository.save(entity);		
		return new ProductDTO(entity);
		
	}
		
	@Transactional
	public ProductDTO update(Long id, ProductDTO dto) {		
		try {
			Product	entity = repository.getReferenceById(id);			
			copyDtoToEntity(dto, entity);				
			entity = repository.save(entity);			
			return new ProductDTO(entity);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Recurso não encontrado");
		}
		
		
	}
	@Transactional(propagation = Propagation.SUPPORTS)
	public void delete(Long id) {
		if (!repository.existsById(id)) {
			throw new ResourceNotFoundException("Recurso não encontrado");
		}
		try {
			repository.deleteById(id);	
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Falha de integridade referencial");
		}
			
	}

	private void copyDtoToEntity(ProductDTO dto, Product entity) {
		entity.setName(dto.getName());
		entity.setDescription(dto.getDescription());
		entity.setPrice(dto.getPrice());
		entity.setImgUrl(dto.getImgUrl());
			
	}
		
		
		

}
