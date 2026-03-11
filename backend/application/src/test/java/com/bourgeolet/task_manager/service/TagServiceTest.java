package com.bourgeolet.task_manager.service;

import com.bourgeolet.task_manager.entity.Tag;
import com.bourgeolet.task_manager.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagService tagService;

    private Tag tag1;
    private Tag tag2;

    @BeforeEach
    void setUp() {
        tag1 = new Tag();
        tag1.setId(1L);
        tag1.setName("tag-1");

        tag2 = new Tag();
        tag2.setId(2L);
        tag2.setName("tag-2");
    }

    @Test
    void getAll_whenCalled_shouldReturnAllTagsFromRepository() {
        when(tagRepository.findAll()).thenReturn(List.of(tag1, tag2));

        List<Tag> result = tagService.getAll();

        assertThat(result).containsExactly(tag1, tag2);
        verify(tagRepository, times(1)).findAll();
        verifyNoMoreInteractions(tagRepository);
    }

    @Test
    void create_whenTagDoesNotExist_shouldSaveAndReturnTag() {
        Tag input = new Tag();
        input.setName("backend");

        when(tagRepository.findByName("backend")).thenReturn(Optional.empty());
        when(tagRepository.save(input)).thenReturn(input);

        Tag created = tagService.create(input);

        assertThat(created).isSameAs(input);

        ArgumentCaptor<Tag> captor = ArgumentCaptor.forClass(Tag.class);
        verify(tagRepository, times(1)).findByName("backend");
        verify(tagRepository, times(1)).save(captor.capture());
        assertThat(captor.getValue()).isSameAs(input);
        verifyNoMoreInteractions(tagRepository);
    }

    @Test
    void create_whenTagAlreadyExists_shouldReturnExistingTagWithoutSaving() {
        Tag input = new Tag();
        input.setName("backend");

        Tag existing = new Tag();
        existing.setId(10L);
        existing.setName("backend");

        when(tagRepository.findByName("backend")).thenReturn(Optional.of(existing));

        Tag created = tagService.create(input);

        assertThat(created).isSameAs(existing);
        verify(tagRepository, times(1)).findByName("backend");
        verify(tagRepository, never()).save(any());
        verifyNoMoreInteractions(tagRepository);
    }

    @Test
    void delete_whenCalledWithTag_shouldDelegateToRepository() {
        Tag toDelete = new Tag();

        tagService.delete(toDelete);

        verify(tagRepository, times(1)).delete(toDelete);
        verifyNoMoreInteractions(tagRepository);
    }

    @Test
    void patch_whenTagExists_shouldSaveAndReturnTag() {
        Tag input = new Tag();
        input.setId(1L);
        input.setName("updated");

        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag1));
        when(tagRepository.save(input)).thenReturn(input);

        Tag result = tagService.patch(input, "actor");

        assertThat(result).isSameAs(input);
        verify(tagRepository, times(1)).findById(1L);
        verify(tagRepository, times(1)).save(input);
        verifyNoMoreInteractions(tagRepository);
    }

    @Test
    void patch_whenTagDoesNotExist_shouldThrowAndNotSave() {
        Tag input = new Tag();
        input.setId(999L);

        when(tagRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tagService.patch(input, "actor"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Le tag n'existe pas");

        verify(tagRepository, times(1)).findById(999L);
        verify(tagRepository, never()).save(any());
        verifyNoMoreInteractions(tagRepository);
    }
}