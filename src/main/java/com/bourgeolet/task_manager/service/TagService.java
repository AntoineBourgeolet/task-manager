package com.bourgeolet.task_manager.service;

import com.bourgeolet.task_manager.dto.tag.TagCreateDTO;
import com.bourgeolet.task_manager.dto.tag.TagResponseDTO;
import com.bourgeolet.task_manager.mapper.TagMapper;
import com.bourgeolet.task_manager.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    private final TagMapper tagMapper;

    public List<TagResponseDTO> getAll() {
        return tagRepository.findAll().stream().map(tagMapper::tagToTagResponseDTO).toList();
    }

    public TagResponseDTO create(TagCreateDTO tagCreateDTO) {
        return tagMapper.tagToTagResponseDTO(tagRepository.save(tagMapper.tagCreateDTOToTag(tagCreateDTO)));
    }
}
