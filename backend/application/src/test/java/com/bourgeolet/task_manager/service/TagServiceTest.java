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

import static org.assertj.core.api.Assertions.assertThat;
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
        tag2 = new Tag();
    }

    @Test
    void getAll_whenCalled_shouldReturnAllTagsFromRepository () {
        when(tagRepository.findAll()).thenReturn(List.of(tag1, tag2));

        List<Tag> result = tagService.getAll();

        assertThat(result).containsExactly(tag1, tag2);
        verify(tagRepository, times(1)).findAll();
        verifyNoMoreInteractions(tagRepository);
    }

    @Test
    void create_whenValidInput_shouldSaveAndReturnTag () {
        Tag input = new Tag();
        when(tagRepository.save(input)).thenReturn(input);

        Tag created = tagService.create(input);

        assertThat(created).isSameAs(input);

        ArgumentCaptor<Tag> captor = ArgumentCaptor.forClass(Tag.class);
        verify(tagRepository, times(1)).save(captor.capture());
        assertThat(captor.getValue()).isSameAs(input);

        verifyNoMoreInteractions(tagRepository);
    }

    @Test
    void delete_whenCalledWithTag_shouldDelegateToRepository () {
        Tag toDelete = new Tag();

        tagService.delete(toDelete);

        verify(tagRepository, times(1)).delete(toDelete);
        verifyNoMoreInteractions(tagRepository);
    }
}