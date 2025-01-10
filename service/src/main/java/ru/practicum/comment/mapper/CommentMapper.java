package ru.practicum.comment.mapper;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.user.mapper.UserMapper;

public class CommentMapper {

    private CommentMapper() {
    }

    // Создание сущности из NewCommentDto
    public static Comment toComment(NewCommentDto dto) {
        Comment c = new Comment();
        c.setContent(dto.getText());
        c.setPositive(dto.getPositive() != null ? dto.getPositive() : false);
        return c;
    }

    // Преобразовать Comment -> CommentDto
    public static CommentDto toCommentDto(Comment c) {
        CommentDto dto = new CommentDto();
        dto.setId(c.getId());
        dto.setEvent(c.getEvent().getId());
        dto.setAuthor(UserMapper.toUserShortDto(c.getUser()));
        dto.setText(c.getContent());
        dto.setCreated(c.getCreatedOn());
        dto.setPositive(c.getPositive());
        dto.setLastModify(c.getLastModify());
        dto.setApproved(c.getApproved());
        return dto;
    }
}
