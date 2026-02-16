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
        Tag tag = new Tag();
        tag.setName(tagCreateDTO.name());
        return tag;
    }

    public TagResponseDTO tagToTagResponseDTO(Tag tag){
        return new TagResponseDTO(tag.getId(), tag.getName());
    }

    public Tag tagDeleteDTOToTag(@Valid TagDeleteDTO dto) {
        Tag tag = new Tag();
        tag.setId(dto.id());
        return tag;
    }
}
