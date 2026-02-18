package com.bourgeolet.task_manager.controller;

import com.bourgeolet.task_manager.dto.tag.TagCreateDTO;
import com.bourgeolet.task_manager.dto.tag.TagDeleteDTO;
import com.bourgeolet.task_manager.dto.tag.TagResponseDTO;
import com.bourgeolet.task_manager.entity.Tag;
import com.bourgeolet.task_manager.mapper.TagMapper;
import com.bourgeolet.task_manager.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tag")
@RequiredArgsConstructor
public class TagController {

    private final TagMapper tagMapper;

    private final TagService tagService;


    @PostMapping
    public ResponseEntity<@NotNull TagResponseDTO> create(@Valid @RequestBody TagCreateDTO dto) {
        Tag tag = tagService.create(tagMapper.tagCreateDTOToTag(dto));
        return ResponseEntity.accepted().body(tagMapper.tagToTagResponseDTO(tag));
    }

    @GetMapping
    public List<TagResponseDTO> all() {
        return tagService.getAll().stream().map(tagMapper::tagToTagResponseDTO).toList();
    }

    @DeleteMapping
    public void delete (@Valid @RequestBody TagDeleteDTO dto){
        tagService.delete(tagMapper.tagDeleteDTOToTag(dto), dto.actor());
    }
}
