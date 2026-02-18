package com.bourgeolet.task_manager.service;

import com.bourgeolet.task_manager.entity.Tag;
import com.bourgeolet.task_manager.repository.TagRepository;
import jakarta.validation.constraints.NotBlank;
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

    public Tag create(Tag tag) {
        return tagRepository.save(tag);
    }

    public void delete(Tag tag) {
        tagRepository.delete(tag);
    }
}
