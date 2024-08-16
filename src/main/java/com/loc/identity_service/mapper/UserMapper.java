package com.loc.identity_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.loc.identity_service.dto.request.UserCreationRequest;
import com.loc.identity_service.dto.request.UserUpdateRequest;
import com.loc.identity_service.dto.response.UserResponse;
import com.loc.identity_service.entity.User;

@Mapper(componentModel="spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);
    User toUser(UserUpdateRequest request);

    // @Mapping(source="firstName", target="lastName")
    UserResponse toUserResponse(User user);
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
