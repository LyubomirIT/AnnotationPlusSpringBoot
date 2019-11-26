package com.nbu.annotationplus.service;

import com.nbu.annotationplus.dto.DtoSource;
import com.nbu.annotationplus.exception.InvalidInputParamsException;
import com.nbu.annotationplus.exception.ResourceNotFoundException;
import com.nbu.annotationplus.persistence.entity.Category;
import com.nbu.annotationplus.persistence.entity.Source;
import com.nbu.annotationplus.persistence.repository.CategoryRepository;
import com.nbu.annotationplus.persistence.repository.SourceRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SourceService {

    @Autowired
    private SourceRepository sourceRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserService userService;

    private DtoSource toDtoSource(Source source) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(source, DtoSource.class);
    }

    private DtoSource toDtoSourceWithoutContent(Source source) {
        DtoSource dtoSource = toDtoSource(source);
        dtoSource.setContent(null);
        return dtoSource;
    }

    @Transactional
    public ResponseEntity<DtoSource> createSource(DtoSource dtoSource){
        Long currentUserId = userService.getUserId();
        validateSourceName(dtoSource.getName());
        validateSourceContent(dtoSource.getContent());
        if(dtoSource.getCategory() == null){
            throw new InvalidInputParamsException("Category is required!");
        }
        if(dtoSource.getCategory().getId() == null){
            throw new InvalidInputParamsException("Category id is required!");
        }
        Category category = categoryRepository.findByIdAndUserId(dtoSource.getCategory().getId(),currentUserId);
        if(category == null){
            throw new ResourceNotFoundException("Category", "id", dtoSource.getCategory().getId());
        }
        if(!sourceRepository.findByNameAndUserId(dtoSource.getName().trim(),currentUserId).isPresent()){
            Source source = new Source();
            source.setUserId(currentUserId);
            source.setName(dtoSource.getName().trim());
            source.setContent(dtoSource.getContent());
            source.setCategory(category);
            sourceRepository.save(source);
            return new ResponseEntity<DtoSource>(toDtoSource(source), HttpStatus.CREATED);
        }else{
            throw new InvalidInputParamsException("Source with name: " + "'" + dtoSource.getName() + "'" + " already exists.");
        }
    }

    @Transactional
    public DtoSource getSourceById(Long id){
        Long currentUserId = userService.getUserId();
        Source source = sourceRepository.findByIdAndUserId(id,currentUserId);
        if(source == null){
            throw new ResourceNotFoundException("Source", "id", id);
        }
        else{
            return toDtoSource(source);
        }
    }

    @Transactional
    public List<DtoSource> getAllSources(Long categoryId){
        Long currentUserId = userService.getUserId();
        List<DtoSource> list;
        list = new ArrayList<>();
        List<Source> sourceList;
        if(categoryId == null){
            sourceList = sourceRepository.findByUserId(currentUserId);
            for(Source source: sourceList){
                list.add(toDtoSourceWithoutContent(source));
            }
            return list;
        }else {
            sourceList = sourceRepository.findByCategoryIdAndUserId(categoryId, currentUserId);
            for (Source source : sourceList) {
                list.add(toDtoSourceWithoutContent(source));
            }
            return list;
        }
    }

    @Transactional
    public ResponseEntity<?> deleteSourceById(Long id){
        Long currentUserId = userService.getUserId();
        Source source = sourceRepository.findByIdAndUserId(id, currentUserId);
        if(source == null){
            throw new ResourceNotFoundException("Source", "id", id);
        }
        else{
            sourceRepository.delete(source);
            return ResponseEntity.ok().build();
        }
    }

    @Transactional
    public DtoSource updateSourceById(Long id, DtoSource dtoSource){
        Long currentUserId = userService.getUserId();
        Source source = sourceRepository.findByIdAndUserId(id,currentUserId);
        if(source == null){
            throw new ResourceNotFoundException("Source", "id", id);
        }
        if(dtoSource.getName() != null && !dtoSource.getName().trim().equals("")){
            if(!source.getName().equals(dtoSource.getName().trim())){
                if(sourceRepository.findByNameAndUserId(dtoSource.getName().trim(),currentUserId).isPresent()){
                    throw new InvalidInputParamsException("Source with name: " + "'" + dtoSource.getName() + "'" + " already exists.");
                }
                source.setName(dtoSource.getName().trim());
            }
        }
        if(dtoSource.getCategory() != null){
            if(!source.getCategory().getId().equals(dtoSource.getCategory().getId())){
                Category category = categoryRepository.findByIdAndUserId(dtoSource.getCategory().getId(),currentUserId);
                if(category == null){
                    throw new ResourceNotFoundException("Category", "id", dtoSource.getCategory().getId());
                }
                source.setCategory(dtoSource.getCategory());
            }
        }
        if(dtoSource.getContent() != null && !dtoSource.getContent().trim().equals("")){
            if(!source.getContent().equals(dtoSource.getContent())){
                source.setContent(dtoSource.getContent());
            }
        }

        source.setUpdatedTs(LocalDateTime.now(Clock.systemUTC()));
        sourceRepository.save(source);
        return toDtoSource(source);
    }

    private void validateSourceName(String sourceName){
        if(sourceName == null ||sourceName.trim().equals("")){
            throw new InvalidInputParamsException("Name is required");
        }
    }

    private void validateSourceContent(String sourceContent){
        if(sourceContent == null || sourceContent.trim().equals("")){
            throw new InvalidInputParamsException("Invalid Content");
        }
    }
}
