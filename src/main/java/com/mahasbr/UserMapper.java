package com.mahasbr;

import org.mapstruct.Mapper;

import com.mahasbr.dto.UserDto;
import com.mahasbr.dto.UserProfileDto;
import com.mahasbr.entity.Role;
import com.mahasbr.entity.User;
import com.mahasbr.entity.UserProfileEntity;

@Mapper(componentModel = "spring")
public interface UserMapper {

	UserDto toUserDto(User user);

	UserProfileDto toUserProfileDto(UserProfileEntity profile);

	default String map(Role role) {
		return role != null ? role.getName().name() : null;
	}
}