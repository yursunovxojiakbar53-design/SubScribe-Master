package com.example.d.user.controller;

import com.example.d.extra.ApiResponse;
import com.example.d.extra.Perms;
import com.example.d.extra.RequirePermission;
import com.example.d.subscription.enums.CurrencyType;
import com.example.d.user.dto.UserResponse;
import com.example.d.user.dto.UserUpdateResponse;
import com.example.d.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@Tag(name = "User", description = "Foydalanuvchi profili va sozlamalari")
public class UserController {
    private final UserService userService;

    //Admin
    @GetMapping("/{id}")
    @RequirePermission(Perms.ADMIN_USER_READ)
    @Operation(summary = "Foydalanuvchi (id bo'yicha)")
    public ResponseEntity<?> getUserById(@PathVariable Integer id) {
        ApiResponse apiResponse=userService.getUserById(id);
        return ResponseEntity.ok(apiResponse);
    }


    //Admin
    @GetMapping
    @RequirePermission(Perms.ADMIN_USER_READ)
    @Operation(summary = "Foydalanuvchilar ro'yxati (admin)")
    public Page<UserResponse> getUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
         return userService.getUsers(page, size);
    }


    @PutMapping("/{id}")
    @RequirePermission(Perms.USER_UPDATE)
    @Operation(summary = "Foydalanuvchini yangilash")
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @RequestBody UserUpdateResponse dto,Authentication authentication) {
        ApiResponse apiResponse=userService.updateUser(id, dto,authentication);
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/me/{id}")
    @RequirePermission(Perms.USER_DELETE)
    @Operation(summary = "O'z hisobini o'chirish")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id, Authentication authentication) {
        ApiResponse apiResponse=userService.deleteUser(id,authentication);
        return ResponseEntity.ok(apiResponse);
    }



    @PatchMapping("/preferences/currency")
    @RequirePermission(Perms.USER_UPDATE)
    @Operation(summary = "Asosiy valyutani o'zgartirish")
    public ResponseEntity<?> updatePreferredCurrency(
            @RequestParam CurrencyType currency, Authentication authentication) {
        return ResponseEntity.ok(userService.updatePreferredCurrency(currency, authentication));
    }

}
