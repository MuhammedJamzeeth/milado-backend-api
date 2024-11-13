package com.milado_api_main.mappers;



import com.milado_api_main.dto.AccountDto;
import com.milado_api_main.entities.Account;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;


@Component
@Mapper
public interface AccountMapper {
    AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

    AccountDto accountToAccountDto(Account account);

    Account accountDtoToAccount(AccountDto accountDto);
}
