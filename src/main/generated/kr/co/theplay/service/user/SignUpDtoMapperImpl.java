package kr.co.theplay.service.user;

import javax.annotation.Generated;
import kr.co.theplay.domain.user.User;
import kr.co.theplay.dto.user.SignUpDto;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-03-28T17:04:21+0900",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 15.0.1 (AdoptOpenJDK)"
)
public class SignUpDtoMapperImpl implements SignUpDtoMapper {

    @Override
    public User toEntity(SignUpDto dto) {
        if ( dto == null ) {
            return null;
        }

        String email = null;
        String password = null;
        String nickname = null;

        email = dto.getEmail();
        password = dto.getPassword();
        nickname = dto.getNickname();

        Long id = null;
        String privacyYn = null;

        User user = new User( id, email, password, nickname, privacyYn );

        return user;
    }

    @Override
    public SignUpDto toDto(User entity) {
        if ( entity == null ) {
            return null;
        }

        SignUpDto signUpDto = new SignUpDto();

        return signUpDto;
    }
}
