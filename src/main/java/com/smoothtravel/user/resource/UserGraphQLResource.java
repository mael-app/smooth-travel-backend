package com.smoothtravel.user.resource;

import com.smoothtravel.user.dto.CreateUserRequest;
import com.smoothtravel.user.dto.UserResponse;
import com.smoothtravel.user.service.UserService;
import jakarta.inject.Inject;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;

import java.util.UUID;

@GraphQLApi
public class UserGraphQLResource {

    @Inject
    UserService userService;

    @Query("userById")
    public UserResponse getUserById(@Name("id") String id) {
        return userService.getUserById(UUID.fromString(id)).orElse(null);
    }

    @Query("userByEmail")
    public UserResponse getUserByEmail(@Name("email") String email) {
        return userService.getUserByEmail(email).orElse(null);
    }

    @Mutation("createUser")
    public UserResponse createUser(CreateUserRequest request) {
        return userService.createUser(request);
    }
}
