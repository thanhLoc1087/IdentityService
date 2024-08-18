package com.loc.identity_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.loc.identity_service.dto.request.UserCreationRequest;
import com.loc.identity_service.dto.request.UserUpdateRequest;
import com.loc.identity_service.dto.response.UserResponse;
import com.loc.identity_service.entity.User;

@Mapper(componentModel="spring")
public interface UserMapper {
    @Mapping(target = "roles", ignore = true)
    User toUser(UserCreationRequest request);
    @Mapping(target = "roles", ignore = true)
    User toUser(UserUpdateRequest request);

    // @Mapping(source="firstName", target="lastName")
    UserResponse toUserResponse(User user);
    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
