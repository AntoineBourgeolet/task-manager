package com.bourgeolet.task_manager.service;

import com.bourgeolet.task_manager.dto.tag.TagCreateDTO;
import com.bourgeolet.task_manager.dto.tag.TagResponseDTO;
import com.bourgeolet.task_manager.entity.Tag;
import com.bourgeolet.task_manager.mapper.TagMapper;
import com.bourgeolet.task_manager.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public List<Tag> getAll() {
        return tagRepository.findAll();
    }

    public Tag create(Tag tagCreateDTO, String actor) {
        return tagRepository.save(tagCreateDTO);
    }
}
