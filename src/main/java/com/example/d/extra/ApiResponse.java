package com.example.d.extra;

import lombok.*;

@Data
@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
@ToString
public class ApiResponse {
    private String message;
    private boolean status;
    private Object data;

    public ApiResponse(String message, boolean status) {
        this.message = message;
        this.status = status;
    }
}
