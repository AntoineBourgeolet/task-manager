package com.bourgeolet.task_manager.controller;

import com.bourgeolet.task_manager.api.tag.TagApi;
import com.bourgeolet.task_manager.dto.tag.TagCreateDTO;
import com.bourgeolet.task_manager.dto.tag.TagDeleteDTO;
import com.bourgeolet.task_manager.dto.tag.TagResponseDTO;
import com.bourgeolet.task_manager.entity.Tag;
import com.bourgeolet.task_manager.mapper.TagMapper;
import com.bourgeolet.task_manager.service.TagService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class TagApiImpl implements TagApi {

    private final TagService tagService;
    private final TagMapper tagMapper;


    @Override
    public ResponseEntity<@NotNull TagResponseDTO> createTag(TagCreateDTO tagCreateDTO) {
        Tag tag = tagService.create(tagMapper.tagCreateDTOToTag(tagCreateDTO));
        return ResponseEntity.accepted().body(tagMapper.tagToTagResponseDTO(tag));
    }

    @Override
    public ResponseEntity<@NotNull Void> deleteTag(TagDeleteDTO tagDeleteDTO) {
        tagService.delete(tagMapper.tagDeleteDTOToTag(tagDeleteDTO));
        return ResponseEntity.accepted().build();
    }

    @Override
    public ResponseEntity<@NotNull List<TagResponseDTO>> getAllTags(){
        return ResponseEntity.accepted().body(tagService.getAll().stream().map(tagMapper::tagToTagResponseDTO).toList());
    }
}