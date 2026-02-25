package com.simonatb.bookstore.mapper;

import com.simonatb.bookstore.dto.CartItemResponseDto;
import com.simonatb.bookstore.dto.CartResponseDto;
import com.simonatb.bookstore.entity.Cart;
import com.simonatb.bookstore.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(source = "user.id", target = "userId")
    CartResponseDto toResponseDTO(Cart cart);

    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "bookTitle")
    @Mapping(source = "subTotal", target = "totalPrice")
    CartItemResponseDto toCartItemDTO(CartItem item);

}

