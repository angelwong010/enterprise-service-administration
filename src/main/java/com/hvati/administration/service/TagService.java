package com.hvati.administration.service;

import com.hvati.administration.dto.TagDto;
import com.hvati.administration.entity.TagEntity;
import com.hvati.administration.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public List<TagDto> getAllTags() {
        return tagRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public TagDto createTag(TagDto tagDto) {
        TagEntity entity = TagEntity.builder()
                .title(tagDto.getTitle())
                .build();
        entity = tagRepository.save(entity);
        return toDto(entity);
    }

    public TagDto updateTag(String id, TagDto tagDto) {
        TagEntity entity = tagRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("Tag not found: " + id));
        entity.setTitle(tagDto.getTitle());
        entity = tagRepository.save(entity);
        return toDto(entity);
    }

    public boolean deleteTag(String id) {
        tagRepository.deleteById(UUID.fromString(id));
        return true;
    }

    private TagDto toDto(TagEntity entity) {
        return TagDto.builder()
                .id(entity.getId().toString())
                .title(entity.getTitle())
                .build();
    }
}
