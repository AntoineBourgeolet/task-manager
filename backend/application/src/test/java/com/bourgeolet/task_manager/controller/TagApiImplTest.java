    package com.bourgeolet.task_manager.controller;

    import com.bourgeolet.task_manager.dto.tag.TagCreateDTO;
    import com.bourgeolet.task_manager.dto.tag.TagDeleteDTO;
    import com.bourgeolet.task_manager.dto.tag.TagResponseDTO;
    import com.bourgeolet.task_manager.entity.Tag;
    import com.bourgeolet.task_manager.mapper.TagMapper;
    import com.bourgeolet.task_manager.service.TagService;
    import org.junit.jupiter.api.Test;
    import org.junit.jupiter.api.extension.ExtendWith;
    import org.mockito.InjectMocks;
    import org.mockito.Mock;
    import org.mockito.junit.jupiter.MockitoExtension;
    import org.springframework.http.ResponseEntity;

    import java.util.List;

    import static org.assertj.core.api.Assertions.assertThat;
    import static org.mockito.Mockito.*;

    @ExtendWith(MockitoExtension.class)
    class TagApiImplTest {

        @Mock
        private TagService tagService;

        @Mock
        private TagMapper tagMapper;

        @InjectMocks
        private TagApiImpl tagApi;

        @Test
        void createTag_whenValidInput_shouldReturnAcceptedAndMappedDTO() {
            // Arrange
            TagCreateDTO createDTO = new TagCreateDTO();
            Tag mappedTag = new Tag();
            Tag savedTag = new Tag();
            TagResponseDTO responseDTO = new TagResponseDTO();

            when(tagMapper.tagCreateDTOToTag(createDTO)).thenReturn(mappedTag);
            when(tagService.create(mappedTag)).thenReturn(savedTag);
            when(tagMapper.tagToTagResponseDTO(savedTag)).thenReturn(responseDTO);

            // Act
            ResponseEntity<TagResponseDTO> response = tagApi.createTag(createDTO);

            // Assert
            assertThat(response.getStatusCode().value()).isEqualTo(202);
            assertThat(response.getBody()).isEqualTo(responseDTO);

            verify(tagMapper).tagCreateDTOToTag(createDTO);
            verify(tagService).create(mappedTag);
            verify(tagMapper).tagToTagResponseDTO(savedTag);
            verifyNoMoreInteractions(tagService, tagMapper);
        }

        @Test
        void deleteTag_whenValidInput_shouldReturnAccepted() {
            // Arrange
            TagDeleteDTO deleteDTO = new TagDeleteDTO();
            Tag mappedTag = new Tag();

            when(tagMapper.tagDeleteDTOToTag(deleteDTO)).thenReturn(mappedTag);
            doNothing().when(tagService).delete(mappedTag);

            // Act
            ResponseEntity<Void> response = tagApi.deleteTag(deleteDTO);

            // Assert
            assertThat(response.getStatusCode().value()).isEqualTo(202);
            assertThat(response.getBody()).isNull();

            verify(tagMapper).tagDeleteDTOToTag(deleteDTO);
            verify(tagService).delete(mappedTag);
            verifyNoMoreInteractions(tagService, tagMapper);
        }

        @Test
        void getAllTags_whenCalled_shouldReturnAcceptedAndListOfDTOs() {
            // Arrange
            Tag tag1 = new Tag();
            Tag tag2 = new Tag();

            TagResponseDTO dto1 = new TagResponseDTO();
            TagResponseDTO dto2 = new TagResponseDTO();

            when(tagService.getAll()).thenReturn(List.of(tag1, tag2));
            when(tagMapper.tagToTagResponseDTO(tag1)).thenReturn(dto1);
            when(tagMapper.tagToTagResponseDTO(tag2)).thenReturn(dto2);

            // Act
            ResponseEntity<List<TagResponseDTO>> response = tagApi.getAllTags();

            // Assert
            assertThat(response.getStatusCode().value()).isEqualTo(202);
            assertThat(response.getBody()).containsExactly(dto1, dto2);

            verify(tagService).getAll();
            verify(tagMapper).tagToTagResponseDTO(tag1);
            verify(tagMapper).tagToTagResponseDTO(tag2);
            verifyNoMoreInteractions(tagService, tagMapper);
        }
    }