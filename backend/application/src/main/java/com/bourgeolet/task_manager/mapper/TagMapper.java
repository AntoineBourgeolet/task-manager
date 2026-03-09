package com.bourgeolet.task_manager.mapper;

import com.bourgeolet.task_manager.dto.tag.TagCreateDTO;
import com.bourgeolet.task_manager.dto.tag.TagDeleteDTO;
import com.bourgeolet.task_manager.dto.tag.TagResponseDTO;
import com.bourgeolet.task_manager.entity.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TagMapper {


    public Tag tagCreateDTOToTag(TagCreateDTO tagCreateDTO){
        return Tag.builder().name(tagCreateDTO.getName()).build();
    }

    public TagResponseDTO tagToTagResponseDTO(Tag tag){
        return new TagResponseDTO(tag.getId(), tag.getName());
    }

    public Tag tagDeleteDTOToTag(@Valid TagDeleteDTO tagDeleteDTO) {
        return Tag.builder().id(tagDeleteDTO.getId()).build();
    }
}
